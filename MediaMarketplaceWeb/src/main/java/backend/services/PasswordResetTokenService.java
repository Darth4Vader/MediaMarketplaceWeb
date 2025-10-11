package backend.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.DataUtils;
import backend.dtos.users.ResetPasswordDto;
import backend.dtos.users.ResetPasswordTokenRequest;
import backend.entities.PasswordResetToken;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.PasswordResetTokenCooldownException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotVerifiedException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.exceptions.enums.UserLogInfo;
import backend.repositories.PasswordResetTokenRepository;
import backend.repositories.UserRepository;

@Service
public class PasswordResetTokenService {
	
	public static final Duration PASSWORD_RESET_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(10); // 10 minutes
	public static final Duration PASSWORD_RESET_TOKEN_EXPIRATION_COOLDOWN = Duration.ofSeconds(60); // 60 seconds
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private UserAuthenticateService userAuthenticateService;
	
	@Autowired
	private UserRepository userRepository;
	
	public PasswordResetToken createPasswordResetToken(ResetPasswordTokenRequest resetPasswordTokenRequest) throws UserDoesNotExistsException, PasswordResetTokenCooldownException, LogValuesAreIncorrectException, UserNotVerifiedException {
		String email = resetPasswordTokenRequest != null ? resetPasswordTokenRequest.getEmail() : null;
		UserAuthenticateService.checkForEmailException(email);
		User user = userAuthenticateService.getExistingUserByEmail(email);
		//check if user is verified
		userAuthenticateService.checkIfUserIsVerified(user);
		// check if already exists
		removeExpiredPasswordResetTokenOfUser(user);
		
		// create new token
		PasswordResetToken newToken = new PasswordResetToken();
		newToken.setToken(UUID.randomUUID().toString());
		newToken.setUser(user);
		newToken.setCreatedDate(LocalDateTime.now());
		newToken.setDuration(PASSWORD_RESET_TOKEN_EXPIRATION_TIME);
		//save the token
		return passwordResetTokenRepository.save(newToken);
	}
    
    @Transactional
    public void resetPassword(ResetPasswordDto resetPasswordDto) throws UserPasswordIsIncorrectException, LogValuesAreIncorrectException, EntityNotFoundException {
        // check that the token is valid
		String token = resetPasswordDto != null ? resetPasswordDto.getToken() : null;
		String newPassword = resetPasswordDto != null ? resetPasswordDto.getPassword() : null;
		String newPasswordConfirm = resetPasswordDto != null ? resetPasswordDto.getPasswordConfirm() : null;
		checkForException(newPassword, newPasswordConfirm);
		
		Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
		if(tokenOpt.isEmpty()) {
			//token not found,, throw exception
			throw new EntityNotFoundException("Password reset token not found");
		}
		
		PasswordResetToken resetToken = tokenOpt.get();
		// check if token is expired
		if (!DataUtils.isUseable(resetToken.getCreatedDate(), resetToken.getDuration())) {
			//token expired, delete it and throw exception
			passwordResetTokenRepository.delete(resetToken);
			throw new EntityNotFoundException("Password reset token has expired");
		}
		
		// check if new password matches confirm password
		if (!Objects.equals(newPassword, newPasswordConfirm)) {
			throw new UserPasswordIsIncorrectException("Password not matching");
		}
		
		// load the user
		User user = resetToken.getUser();
		if(user == null) {
			//user not found,, throw exception
			throw new EntityNotFoundException("User not found for token");
		}
		
		// update the user's password
		user.setPassword(userAuthenticateService.encodePassword(newPassword));
		
		// save the user
		userRepository.save(user);
		
		// delete the token
		passwordResetTokenRepository.delete(resetToken);
		
		// After the change we will reloging the user again to a new session with his updated information.
		userAuthenticateService.reloadAuthentication(user);
    }
    
    @Transactional
    public void removeExpiredPasswordResetTokenOfUser(User user) throws PasswordResetTokenCooldownException {
		Optional<PasswordResetToken> existingTokenOpt = passwordResetTokenRepository.findByUser(user);
		// check if already exists
		if (existingTokenOpt.isPresent()) {
			PasswordResetToken existingToken = existingTokenOpt.get();
			// check if cooldown period has passed
			if (DataUtils.isUseable(existingToken.getCreatedDate(), PASSWORD_RESET_TOKEN_EXPIRATION_COOLDOWN)) {
				//cooldown period not passed, do nothing
				LocalDateTime cooldownEnd = existingToken.getCreatedDate().plus(PASSWORD_RESET_TOKEN_EXPIRATION_COOLDOWN);
				Duration timeSinceCreation = Duration.between(LocalDateTime.now(), cooldownEnd);
				throw new PasswordResetTokenCooldownException("Please wait more " + DataUtils.timeLeftString(timeSinceCreation) + " before requesting a new password reset.");
			}
			//delete existing token
			existingToken.setUser(null);
			passwordResetTokenRepository.delete(existingToken);
			System.out.println("Deleted By Bye");
		}
	}
    
    @Transactional
    public void removePasswordResetTokenOfUser(User user) throws EntityNotFoundException {
		Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByUser(user);
		if(tokenOpt.isEmpty()) {
			//token not found,, throw exception
			throw new EntityNotFoundException("Password reset token not found");
		}
		PasswordResetToken resetToken = tokenOpt.get();
		passwordResetTokenRepository.delete(resetToken);
		//passwordResetTokenRepository.deleteByUser(user);
	}
	
    public void checkForException(String password, String confirmPassword) throws LogValuesAreIncorrectException {
        Map<UserLogInfo, String> logInfo = new HashMap<>();
        UserAuthenticateService.loadExceptionsPassword(password, logInfo);
        UserAuthenticateService.loadExceptionsPasswordConfirm(confirmPassword, logInfo);
        if (!logInfo.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfo, "One or more values are missing");
        }
    }
}