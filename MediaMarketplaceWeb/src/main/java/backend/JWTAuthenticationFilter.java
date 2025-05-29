package backend;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import backend.controllers.UserAuthenticateController;
import backend.exceptions.JwtTokenExpiredException;
import backend.exceptions.UserNotLoggedInException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
	
	private final Log LOGGER = LogFactory.getLog(getClass());
	
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	@Autowired
	//@Qualifier("exceptionResolver")
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
			// try log user by access token
        	// also check if access token i missingg or expired
        	// if so try to refresh it
        	boolean refreshToken = false;
        	try {
	        	Cookie accessTokenCookie = WebUtils.getCookie(request, CookieNames.ACCESS_TOKEN);
				if(accessTokenCookie != null) {
					String accessToken = accessTokenCookie.getValue();
		        	if(userAuthenticateController.loginUserFromToken(accessToken, request))
		        		LOGGER.info("User authenticated successfully");
				}
				else {
					// maybe missing becuase expired and removed
					// ty to refresh token
					refreshToken = true;
				}
        	}
        	catch(JwtTokenExpiredException e) {
        		// try to refresh token
        		refreshToken = true;
        	}
        	try {
	        	if(refreshToken) {
	        		Cookie refreshTokenCookie = WebUtils.getCookie(request, CookieNames.REFRESH_TOKEN);
	        		if(refreshTokenCookie != null) {
						LOGGER.info("Trying to refresh token");
						String accessToken = userAuthenticateController.refreshTokenRequestForFilter(request, response, refreshTokenCookie);
			        	if(userAuthenticateController.loginUserFromToken(accessToken, request))
			        		LOGGER.info("User authenticated successfully");
					}
	        	}
        	}
        	catch(Exception e) {}
        	// DEBUG REMOVE
        	//System.out.println(request.getRequestURI());
        	//System.out.println(request.getParameterMap());
        	Map<String, String[]> params = request.getParameterMap();
        	for (Map.Entry<String, String[]> entry : params.entrySet()) {
				String key = entry.getKey();
				String[] values = entry.getValue();
				System.out.println(key + ": " + Arrays.toString(values));
			}
	        filterChain.doFilter(request, response);
	        //System.out.println("Filter chain passed");
	        try {
	        	userAuthenticateController.logoutFromCurrentUser();
	        }
	        catch(UserNotLoggedInException e) {
	        	// ignore, not every method need authentication
	        }
        }
        catch(Exception e) {
        	String uri = request.getRequestURI();
        	LOGGER.error("Error in JWTAuthenticationFilter: " + request.getMethod() + " - " + uri);
        	System.out.println(resolver);
        	if(resolver != null) {
        		ModelAndView m = resolver.resolveException(request, response, null, e);
        		System.out.println("ModelAndView: " + m);
        		// exception not resolved, then rethrow it
        		if(m == null) {
        			throw e;
        		}
        		else {
                	e.printStackTrace();
        		}
        	}
        	else
        		LOGGER.error("Exception Resolver is null");
        }
    }
    
    private final List<RequestMatcher> uriMatcher = Arrays.asList(
		//new AntPathRequestMatcher("/api/main/**", HttpMethod.GET.name()),
		new AntPathRequestMatcher("/api/users/login", HttpMethod.POST.name()),
		new AntPathRequestMatcher("/api/users/register", HttpMethod.POST.name()),
		new AntPathRequestMatcher("/api/users/refresh", HttpMethod.POST.name()),
		new AntPathRequestMatcher("/api/users/refresh/logout", HttpMethod.POST.name())
	);
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
    	for(RequestMatcher matcher : uriMatcher) {
			if(matcher.matches(request)) {
				return true;
			}
		}
    	return false;
    }
}