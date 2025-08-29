package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.DataUtils;
import backend.dtos.users.ResetPasswordDto;
import backend.dtos.users.ResetPasswordTokenRequest;
import backend.entities.PasswordResetToken;
import backend.exceptions.EmailSendFailedException;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.PasswordResetTokenCooldownException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.services.EmailSenderService;
import backend.services.PasswordResetTokenService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/users/reset-password")
public class PasswordResetTokenController {
	
	@Autowired
	private PasswordResetTokenService passwordResetTokenService;
	
	@Autowired
	private EmailSenderService emailSenderService;

    @PostMapping(value = "/request")
    public ResponseEntity<?> createResetPasswordToken(@RequestBody @Valid ResetPasswordTokenRequest resetPasswordTokenRequest) throws UserDoesNotExistsException, PasswordResetTokenCooldownException, LogValuesAreIncorrectException, EmailSendFailedException {
    	try {
    		PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetToken(resetPasswordTokenRequest);
    		//send email with the token
    		try {
    			emailSenderService.sendResetPasswordEmail(passwordResetToken.getUser().getEmail(), resetPasswordTokenRequest.getRedirectUrl() + passwordResetToken.getToken(), DataUtils.timeLeftString(PasswordResetTokenService.PASSWIRD_RESET_TOKEN_EXPIRATION_TIME));
    		} catch (MessagingException e) {
    		    // Log the error
    		    //log.error("Failed to send password reset email to {}", user.getEmail(), e);

    		    // Optionally notify the user (via HTTP error)
    		    throw new EmailSendFailedException("Unable to send email. Please try again later.");
    		}
			return ResponseEntity.ok("Password reset token sent to email if it exists in our system");
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to create reset password request", e);
        }
    }
    
    @PostMapping(value = "")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) throws UserPasswordIsIncorrectException, LogValuesAreIncorrectException, EntityNotFoundException {
    	try {
    		passwordResetTokenService.resetPassword(resetPasswordDto);
			return ResponseEntity.ok("Password has been reset successfully");
    	}
    	catch (DataAccessException e) {
			throw new EntityAdditionException("Unable to reset password", e);
		}
    }
}