package backend;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.WebUtils;

import backend.controllers.UserAuthenticateController;
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
			Cookie accessTokenCookie = WebUtils.getCookie(request, CookieNames.ACCESS_TOKEN);
			if(accessTokenCookie != null) {
				String accessToken = accessTokenCookie.getValue();
	        	if(userAuthenticateController.loginUserFromToken(accessToken, request))
	        		LOGGER.info("User authenticated successfully");
			}
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
        	LOGGER.error("Error in JWTAuthenticationFilter: " + request.getMethod() + " - " + uri);
        	e.printStackTrace();
        	if(resolver != null)
        		resolver.resolveException(request, response, null, e);
        	else
        		LOGGER.error("Exception Resolver is null");
        	//throw e;
        }
    }
    
    /*
    private final List<RequestMatcher> uriMatcher = Arrays.asList(
		new AntPathRequestMatcher("/api/main/**", HttpMethod.GET.name()),
		new AntPathRequestMatcher("/api/auth/login"),
		new AntPathRequestMatcher("/api/auth/register")
	);
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
    	return uriMatcher.stream().anyMatch(matcher -> matcher.matches(request));
    }
    */
}