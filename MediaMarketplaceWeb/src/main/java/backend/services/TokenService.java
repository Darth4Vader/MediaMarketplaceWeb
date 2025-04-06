package backend.services;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import backend.entities.User;
import backend.exceptions.JwtTokenNotFoundException;
import backend.exceptions.UserNotLoggedInException;

/**
 * Service for handling JWT (JSON Web Token) operations and user authentication.
 * <p>
 * This service is responsible for generating JWT tokens, retrieving the current token,
 * and fetching the currently authenticated user. It interacts with Spring Security's
 * JWT support to encode and decode tokens.
 * </p>
 */
@Service
public class TokenService {
	
    @Autowired
    private JwtEncoder jwtEncoder;
    
    @Autowired
    private JwtDecoder jwtDecoder;

    /**
     * Generates a JWT for the given authentication object.
     * <p>
     * This method creates a JWT with claims that include the issuer, issued time,
     * subject (username), and authorities (roles) of the authenticated user. The JWT
     * is then encoded and returned as a string.
     * </p>
     * 
     * @param auth The {@link Authentication} object representing the authenticated user.
     * @return The generated JWT as a {@link String}.
     */
    public String generateJwt(Authentication auth){

        Instant nowTime = Instant.now();

        String scope = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(nowTime)
            .subject(auth.getName())
            .claim("scope", scope)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    
    public Jwt decodeJwt(String token) {
    	try {
    		return jwtDecoder.decode(token);
    	}
    	catch (BadJwtException e) {
    		throw new JwtTokenNotFoundException("Can't find user for the token", e);
		}
	}
    
    public String extractUsername(String token) {
    	Jwt jwt = decodeJwt(token);
		if (jwt != null) {
			return jwt.getSubject();
		}
		// If the token is invalid or expired, return null or throw an exception as needed
    	return null;
        //return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
		Jwt jwt = decodeJwt(token);
        Instant expiration = jwt.getExpiresAt(); // Retrieves the expiration claim (exp)
        return expiration != null && expiration.isBefore(Instant.now()); // Compare expiration time with current time
    }
    
    /**
     * Retrieves the current JWT from the security context.
     * <p>
     * This method extracts the JWT from the {@link Authentication} object stored in
     * the {@link SecurityContextHolder}. It assumes that the JWT is stored as the
     * principal in the security context.
     * </p>
     * 
     * @return The current JWT as a {@link String}.
     */
    public String getCurretToken() {
    	return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    /**
     * Retrieves the currently authenticated user.
     * <p>
     * This method extracts the {@link User} object from the {@link Authentication}
     * object stored in the {@link SecurityContextHolder}. If there is no authentication
     * information available, it throws a {@link UserNotLoggedInException}.
     * </p>
     * 
     * @return The currently authenticated {@link User}.
     * @throws UserNotLoggedInException If no authentication information is available.
     */
    public User getCurretUser() throws UserNotLoggedInException {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	//if there is no authentication then the user is not logged
    	//then we will throw a runtime exception to notify the not logged user.
    	if(auth == null)
    		throw new UserNotLoggedInException();
    	//The current user
    	User user = (User) auth.getPrincipal();
    	return user;
    }
}