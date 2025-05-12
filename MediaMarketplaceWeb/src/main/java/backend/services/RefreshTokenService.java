package backend.services;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.entities.RefreshToken;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.RefreshTokenExpiredException;
import backend.exceptions.RevokedRefreshTokenAccessException;
import backend.repositories.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	
	public static final Duration REFRESH_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(5);
	
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private TokenService tokenService;
    
    public RefreshToken findByToken(String token) throws EntityNotFoundException {
		return refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new EntityNotFoundException("Refresh token not found"));
	}
    
    @Transactional
    public void removeAllRefreshTokensOfUser(User user) throws EntityNotFoundException {
		Optional<List<RefreshToken>> refreshTokens = refreshTokenRepository.findByUserId(user.getId());
		if(refreshTokens.isPresent()) {
			List<RefreshToken> tokens = refreshTokens.get();
			for(RefreshToken token : tokens) {
				refreshTokenRepository.delete(token);
			}
		}
	}
    
    @Transactional
    public RefreshToken createRefreshToken(User user) {
		Jwt jwt = tokenService.generateJwtWithTime(user, REFRESH_TOKEN_EXPIRATION_TIME);
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken(jwt.getTokenValue());
		refreshToken.setIssuedAt(jwt.getIssuedAt());
		refreshToken.setExpiryDate(jwt.getExpiresAt());
		return refreshTokenRepository.save(refreshToken);
	}
    
    @Transactional
    public RefreshToken verifyTokenExpiration(RefreshToken refreshToken) {
    	if(tokenService.isTokenExpired(refreshToken.getToken())) {
    		//refreshTokenRepository.delete(refreshToken);
    		// mark token as "revoked"
    		refreshToken.setRevoked(true);
    		refreshTokenRepository.save(refreshToken);
    		throw new RefreshTokenExpiredException("Current session expired. Please re-authenticate.");
    	}
        return refreshToken;
    }
    
    @Transactional
    public RefreshToken refreshToken(String token) throws EntityNotFoundException {
		RefreshToken refreshToken = findByToken(token);
		if(refreshToken.isRevoked()) {
			try {
				removeAllRefreshTokensOfUser(refreshToken.getUser());
			}
			catch (Throwable e) {}
			throw new RevokedRefreshTokenAccessException("Malicous activity detected");
		}
		// check that the refresh token is not expired, if it is, then the user will need to login again
		refreshToken = verifyTokenExpiration(refreshToken);
		refreshToken.setRevoked(true);
		refreshTokenRepository.save(refreshToken);
		return createRefreshToken(refreshToken.getUser());
    }
    
    @Transactional
    public void revokeRefreshToken(String token) throws EntityNotFoundException {
		RefreshToken refreshToken = findByToken(token);
		refreshToken.setRevoked(true);
		refreshTokenRepository.save(refreshToken);
    }

}
