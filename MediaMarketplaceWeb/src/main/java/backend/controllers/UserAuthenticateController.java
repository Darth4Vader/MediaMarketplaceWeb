package backend.controllers;

import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import backend.CookieNames;
import backend.DataUtils;
import backend.dtos.general.TurnstileResponse;
import backend.dtos.users.LogInDto;
import backend.dtos.users.LoginResponse;
import backend.dtos.users.RegisterLocal;
import backend.dtos.users.UserBasicInformationDto;
import backend.dtos.users.UserInformationDto;
import backend.dtos.users.VerifyAccountDto;
import backend.entities.AccountVerificationToken;
import backend.exceptions.EmailSendFailedException;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.HumanVerificationException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.MissingCookieException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserNotVerifiedException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.services.CloudflareTurnstileService;
import backend.services.EmailSenderService;
import backend.services.RefreshTokenService;
import backend.services.TokenService;
import backend.services.UserAuthenticateService;
import backend.utils.RequestUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * REST controller for user authentication and management.
 * <p>
 * This controller provides endpoints for user registration, login, information updates, and authentication checks.
 * </p>
 */
@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAuthenticateController {
	
	private final Log LOGGER = LogFactory.getLog(getClass());

    @Autowired
    private UserAuthenticateService userAuthService;
    
    @Autowired
    private EmailSenderService emailSenderService;
    
    @Autowired
    private CloudflareTurnstileService cloudflareTurnstileService;
    
    private static ResponseEntity<?> createAuthenticationResponse(LoginResponse loginResponse) {
    	HttpCookie accessTokenCookie = createAccessTokenCookie(
    			loginResponse.getAccessToken(), TokenService.ACCESS_TOKEN_EXPIRATION_TIME);
    	
    	HttpCookie refreshTokenCookie = createRefreshTokenCookie(
    			loginResponse.getRefreshToken(), RefreshTokenService.REFRESH_TOKEN_EXPIRATION_TIME);
    	
    	return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.build();
	}
    
    public static void createAuthenticationResponse(HttpServletResponse response, LoginResponse loginResponse) {
    	HttpCookie accessTokenCookie = createAccessTokenCookie(
    			loginResponse.getAccessToken(), TokenService.ACCESS_TOKEN_EXPIRATION_TIME);
    	
    	HttpCookie refreshTokenCookie = createRefreshTokenCookie(
    			loginResponse.getRefreshToken(), RefreshTokenService.REFRESH_TOKEN_EXPIRATION_TIME);
    	
    	response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    	response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
	}
    
    private static ResponseCookie createAccessTokenCookie(String accessToken, Duration maxAge) {
    	ResponseCookie accessTokenCookie = ResponseCookie.from(CookieNames.ACCESS_TOKEN, accessToken)
			.path("/")
			//.path("/")
			.maxAge(maxAge)
			.httpOnly(true)
			.secure(true)
			.sameSite("None")
			//.sameSite("Strict")
			.build();
    	return accessTokenCookie;
    }
    
    private static ResponseCookie createRefreshTokenCookie(String refreshToken, Duration maxAge) {
    	ResponseCookie refreshTokenCookie = ResponseCookie.from(CookieNames.REFRESH_TOKEN, refreshToken)
			//.path("/api/users/refresh")
			.path("/")
			.maxAge(maxAge)
			.httpOnly(true)
			.secure(true)
			.sameSite("None")
			//.sameSite("Strict")
			.build();
    	return refreshTokenCookie;
    }
    
    /**
     * Registers a new user.
     * <p>
     * This endpoint registers a new user with the provided {@link UserInformationDto}.
     * If the user already exists or if the provided values are incorrect, appropriate exceptions will be thrown.
     * </p>
     *
     * @param registerDto The {@link UserInformationDto} object containing the details of the user to be registered.
     * @return A success message indicating the user has been registered.
     * @throws UserAlreadyExistsException If a user with the same details already exists.
     * @throws LogValuesAreIncorrectException If the provided values are incorrect.
     * @throws UserPasswordIsIncorrectException If the password provided is incorrect.
     * @throws EmailSendFailedException 
     * @throws HumanVerificationException 
     */
    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterLocal registerDto, HttpServletRequest request) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException, EmailSendFailedException, HumanVerificationException {
    	// first verify the turnstile token
    	String turnstileToken = registerDto.getCfTurnstileToken();
    	String clientIp = RequestUtils.getClientIpForCloudflare(request);
    	TurnstileResponse response = cloudflareTurnstileService.validateToken(turnstileToken, clientIp);
    	if(!response.isSuccess()) {
    		// log if there are any warning or error codes
    		LOGGER.error("Turnstile verification warnings or errors: " + (response.getErrorCodes() != null ? String.join(", ", response.getErrorCodes()) : ""));
    		// failed human verification
    		throw new HumanVerificationException("Human verification failed, please try again later");
    	}
    	
    	try {
    		AccountVerificationToken verify = userAuthService.registerUser(registerDto);
			//send email with the token
			try {
				emailSenderService.sendRegistrationConfirmationEmail(verify.getUser().getEmail(), registerDto.getRedirectUrl() + verify.getToken(), DataUtils.timeLeftString(UserAuthenticateService.ACCOUNT_VERIFICATION_EXPIRATION_TIME));
			} catch (MessagingException e2) {
			    throw new EmailSendFailedException("Unable to send verification email. Please try again later.");
			}
			return ResponseEntity.ok("User registered successfully. Please check your email to verify your account.");
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to register the user", e);
        }
    }
    
    @PostMapping(value = "verify")
    public ResponseEntity<?> verifyAccount(@RequestBody @Valid VerifyAccountDto verifyAccountDto) throws EntityNotFoundException {
    	try {
			userAuthService.verifyAccount(verifyAccountDto);
			return ResponseEntity.ok("Account has been verified successfully");
		} catch (DataAccessException e) {
			throw new EntityAdditionException("Unable to verify account", e);
		}
    }
    
    /**
     * Logs in a user.
     * <p>
     * This endpoint logs in a user using the provided {@link LogInDto}.
     * If the user does not exist or the password is incorrect, appropriate exceptions will be thrown.
     * </p>
     *
     * @param loginDto The {@link LogInDto} object containing the login credentials of the user.
     * @return A success message indicating the user has been logged in.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws UserPasswordIsIncorrectException If the provided password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     * @throws EmailSendFailedException 
     * @throws UserNotVerifiedException 
     */
    @PostMapping(value = "/login")
    public ResponseEntity<?> loginUser(@RequestBody LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException, EmailSendFailedException, UserNotVerifiedException {
    	try {
    		LoginResponse loginResponse = userAuthService.loginUser(loginDto);
        	return createAuthenticationResponse(loginResponse);
    	}
    	catch(UserNotVerifiedException e) {
    		//check if new verification token created
    		VerifyAccountDto verify = e.getVerifyAccountDto();
    		if(verify != null) {
    			//send email with the token
    			try {
    				emailSenderService.sendRegistrationConfirmationEmail(verify.getEmail(), e.getRedirectUrl() + verify.getToken(), DataUtils.timeLeftString(UserAuthenticateService.ACCOUNT_VERIFICATION_EXPIRATION_TIME));
    			} catch (MessagingException e2) {
    			    throw new EmailSendFailedException("Unable to send new verification email. Please try again later.");
    			}
    		}
    		throw e;
    	}
    }
    
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refreshTokenRequest(HttpServletRequest request) throws EntityNotFoundException, EntityAlreadyExistsException {
    	try {
    		Cookie refreshTokenCookie = getRefreshTokenCookie(request);
    		LoginResponse loginResponse = userAuthService.refreshLoginToken(refreshTokenCookie.getValue());
        	return createAuthenticationResponse(loginResponse);
    	}
    	catch (DataIntegrityViolationException e) {
    		// catch if the token already exists
    		// if so don't create it (React development call the server twice for every loading request)
			throw new EntityAlreadyExistsException("Token already exists");
		}
    }
    
    /**
     * Updates the information of the currently logged-in user.
     * <p>
     * This endpoint updates the user information using the provided {@link UserInformationDto}.
     * If the user is not logged in or if the password is incorrect, appropriate exceptions will be thrown.
     * </p>
     *
     * @param userDto The {@link UserInformationDto} object containing the updated user information.
     * @throws UserNotLoggedInException If the user is not logged in.
     * @throws UserPasswordIsIncorrectException If the provided password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided values are incorrect.
     * @throws UserNotVerifiedException 
     * @throws UserDoesNotExistsException 
     */
    @PutMapping("/current")
    public void updateUserInformation(@RequestBody UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException, UserDoesNotExistsException, UserNotVerifiedException {
        try {
        	userAuthService.updateUserInformation(userDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to update the user information", e);
        }
    }
    
    /**
     * Signs out the currently logged-in user.
     * <p>
     * This endpoint triggers the user sign-out process by calling the {@link UserAuthService#signOutFromCurrentUser()} method.
     * </p>
     * @throws EntityNotFoundException 
     * @throws UserNotLoggedInException if no user is currently logged in
     */
    @PostMapping("/refresh/logout")
    public ResponseEntity<?> logoutRefreshToken(HttpServletRequest request) throws EntityNotFoundException {
    	// first revoke the refresh token
    	Cookie refreshTokenCookie = getRefreshTokenCookie(request);
    	userAuthService.logoutRefreshToken(refreshTokenCookie.getValue());
    	// create empty cookie to delete the refresh token cookie and access token cookie
    	HttpCookie emptyRefreshTokenCookie = createRefreshTokenCookie(null, Duration.ZERO);
    	HttpCookie emptyAccessTokenCookie = createAccessTokenCookie(null, Duration.ZERO);
    	return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, emptyRefreshTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, emptyAccessTokenCookie.toString())
				.build();
    }
    
    /**
     * Checks if the current user is an administrator.
     * <p>
     * This endpoint checks the current user's role to determine if they are an administrator.
     * </p>
     */
    @GetMapping("/current/admin")
    public void checkIfCurrentUserIsAdmin() {
        userAuthService.checkIfCurrentUserIsAdmin();
    }
    
    /**
     * Retrieves the information of the currently logged-in user.
     * <p>
     * This endpoint returns the {@link UserInformationDto} for the currently logged-in user.
     * If the user is not logged in, an exception will be thrown.
     * </p>
     *
     * @return The {@link UserInformationDto} object containing the current user's information.
     * @throws UserNotLoggedInException If the user is not logged in.
     */
    @GetMapping("/current")
    public UserBasicInformationDto getCurrentUserDto() throws UserNotLoggedInException {
        return userAuthService.getCurrentUserDto();
    }
    
    /**
     * Checks if the current user is an administrator.
     * <p>
     * This endpoint returns a boolean value indicating whether the current user is an administrator.
     * If the user is not logged in, an exception will be thrown.
     * </p>
     *
     * @return {@code true} if the current user is an administrator, {@code false} otherwise.
     * @throws UserNotLoggedInException If the user is not logged in.
     */
    @GetMapping("/current/is-admin")
    public boolean isCurrentUserAdmin() throws UserNotLoggedInException {
        return userAuthService.isCurrentUserAdmin();
    }
    
    /**
     * Authenticates the currently logged-in user.
     * <p>
     * This endpoint performs an authentication check on the current user.
     * If the user is not logged in, an exception will be thrown.
     * </p>
     *
     * @throws UserNotLoggedInException If the user is not logged in.
     */
    @GetMapping("/authenticate")
    public void authenticateLoggedUser() throws UserNotLoggedInException {
        userAuthService.authenticateLoggedUser();
    }
    
    private Cookie getRefreshTokenCookie(HttpServletRequest request) {
    	Cookie refreshTokenCookie = WebUtils.getCookie(request, CookieNames.REFRESH_TOKEN);
    	if (refreshTokenCookie == null) {
			throw new MissingCookieException("Refresh token Cookie is missing");
    	}
    	return refreshTokenCookie;
	}
    
    // for authentication filter
    
    public String refreshTokenRequestForFilter(HttpServletRequest request, HttpServletResponse response, Cookie refreshTokenCookie) throws EntityNotFoundException, EntityAlreadyExistsException {
		LoginResponse loginResponse = userAuthService.refreshLoginToken(refreshTokenCookie.getValue());
		ResponseCookie newAccessTokenCookie = createAccessTokenCookie(
    			loginResponse.getAccessToken(), TokenService.ACCESS_TOKEN_EXPIRATION_TIME);
    	
		ResponseCookie newRefreshTokenCookie = createRefreshTokenCookie(
    			loginResponse.getRefreshToken(), RefreshTokenService.REFRESH_TOKEN_EXPIRATION_TIME);
		
		Cookie accessToken = convertResponseCookieToCookie(newAccessTokenCookie);
		Cookie refreshToken = convertResponseCookieToCookie(newRefreshTokenCookie);
		
		response.addCookie(accessToken);
		response.addCookie(refreshToken);
		
		return loginResponse.getAccessToken();
    }
    
    private static final Cookie convertResponseCookieToCookie(ResponseCookie responseCookie) {
		Cookie cookie = new Cookie(responseCookie.getName(), responseCookie.getValue());
		cookie.setPath(responseCookie.getPath());
		cookie.setMaxAge((int) responseCookie.getMaxAge().getSeconds());
		cookie.setHttpOnly(responseCookie.isHttpOnly());
		cookie.setSecure(responseCookie.isSecure());
		return cookie;
	}
    
    public boolean loginUserFromToken(String token, HttpServletRequest request) {
    	try {
    		return userAuthService.loginUserFromToken(token, request);
    	}
    	catch (UserDoesNotExistsException e) {
    		throw new RuntimeException(e);
		}
	}
    
    public void logoutFromCurrentUser() throws UserNotLoggedInException {
    	userAuthService.logoutFromCurrentUser();
    }
}