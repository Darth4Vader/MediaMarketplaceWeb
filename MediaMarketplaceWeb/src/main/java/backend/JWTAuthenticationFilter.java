package backend;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import backend.services.GeolocationService;
import backend.utils.RequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
	
	private static final Logger GENERAL_LOGGER = LoggerFactory.getLogger("myapp.logging.general");
	private static final Logger AUTH_LOGGER = LoggerFactory.getLogger("myapp.logging.auth");
	private static final Logger SESSION_LOGGER = LoggerFactory.getLogger("myapp.logging.session");
	
	@Autowired
	private UserAuthenticateController userAuthenticateController;
	
	@Autowired
	private GeolocationService geolocationService;
	
	@Autowired
	//@Qualifier("exceptionResolver")
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
    		// skip loading images (only for localhost development environment)
    	    String path = request.getRequestURI();

    	    if (path.startsWith("/api/images/")) {
    	        filterChain.doFilter(request, response);  // skip your filter logic here
    	        return;
    	    }

    	    // log new Users accessing the server by IP address
    	    HttpSession session = request.getSession(false);
    	    if (session != null) {
    	        Boolean ipLogged = (Boolean) session.getAttribute("ipLogged");
    	        if (ipLogged == null || !ipLogged) {
    	        	session.setAttribute("ipLogged", true);
    	            String ipAddress = RequestUtils.getClientIpForCloudflare(request);
    	            SESSION_LOGGER.info("New connection from IP: {} Country: {}", ipAddress, geolocationService.getCountryOfSession(request));
    	        }
    	    } else {
    	        // No session - could create one or handle differently
    	    }
    	    
			// try log user by access token
        	// also check if access token i missingg or expired
        	// if so try to refresh it
        	boolean refreshToken = false;
        	try {
	        	Cookie accessTokenCookie = WebUtils.getCookie(request, CookieNames.ACCESS_TOKEN);
				if(accessTokenCookie != null) {
					String accessToken = accessTokenCookie.getValue();
		        	if(userAuthenticateController.loginUserFromToken(accessToken, request))
		        		AUTH_LOGGER.info("User authenticated successfully");
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
	        			//AUTH_LOGGER.info("Trying to refresh token");
						String accessToken = userAuthenticateController.refreshTokenRequestForFilter(request, response, refreshTokenCookie);
			        	if(userAuthenticateController.loginUserFromToken(accessToken, request))
			        		AUTH_LOGGER.info("Token Refreshed: User authenticated successfully");
					}
	        	}
        	}
        	catch(Exception e) {}
	        filterChain.doFilter(request, response);
	        try {
	        	userAuthenticateController.logoutFromCurrentUser();
	        }
	        catch(UserNotLoggedInException e) {
	        	// ignore, not every method need authentication
	        }
        }
        catch(Exception e) {
        	String uri = request.getRequestURI();
        	GENERAL_LOGGER.error("Error in JWTAuthenticationFilter: " + request.getMethod() + " - " + uri);
        	if(resolver != null) {
        		ModelAndView m = resolver.resolveException(request, response, null, e);
        		// exception not resolved, then rethrow it
        		if(m == null) {
        			throw e;
        		}
        		else {
                	e.printStackTrace();
        		}
        	}
        	else
        		GENERAL_LOGGER.error("Exception Resolver is null");
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