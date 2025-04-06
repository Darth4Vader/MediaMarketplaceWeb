package backend;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import backend.controllers.UserAuthenticateController;
import backend.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    /*@Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsService userDetailsService;*/
	
	@Autowired
	private  UserAuthenticateController userAuthenticateController;
	
	/*@Autowired
	@Qualifier("handlerExceptionResolver")*/
	private final HandlerExceptionResolver resolver;
	
    @Autowired
    public JWTAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver) {
        this.resolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
	    	final String authorizationHeader = request.getHeader("Authorization");
	        System.out.println("Authorization Header: " + authorizationHeader);
	        String username = null;
	        String jwt = null;
	        System.out.println("Ok Before filter");
	        if (authorizationHeader != null) {
				jwt = authorizationHeader;
	        	userAuthenticateController.loginUserFromToken(jwt, request);
	        }
	        
	        /*if (authorizationHeader != null) {
	            jwt = authorizationHeader;
	            System.out.println(jwt);
	            username = tokenService.extractUsername(jwt);
	        }
	
	        //if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        if(username != null) {
	        	System.out.println(username);
	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
	            if (tokenService.validateToken(jwt, userDetails)) {
	                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	            }
	        }
	        */
	        filterChain.doFilter(request, response);
        }
        catch(Exception e) {
        	resolver.resolveException(request, response, null, e);
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