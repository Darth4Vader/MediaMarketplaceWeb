package backend.controllers;

import java.time.Duration;

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
import backend.dtos.users.LogInDto;
import backend.dtos.users.LoginResponse;
import backend.dtos.users.UserInformationDto;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.MissingCookieException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.services.RefreshTokenService;
import backend.services.TokenService;
import backend.services.UserAuthenticateService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

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

    @Autowired
    private UserAuthenticateService userAuthService;
    
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
    
    private static HttpCookie createAccessTokenCookie(String accessToken, Duration maxAge) {
    	HttpCookie accessTokenCookie = ResponseCookie.from(CookieNames.ACCESS_TOKEN, accessToken)
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
    
    private static HttpCookie createRefreshTokenCookie(String refreshToken, Duration maxAge) {
    	HttpCookie refreshTokenCookie = ResponseCookie.from(CookieNames.REFRESH_TOKEN, refreshToken)
			.path("/api/users/refresh")
			//.path("/")
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
     */
    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody UserInformationDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
        try {
        	LoginResponse loginResponse = userAuthService.registerUser(registerDto);
        	return createAuthenticationResponse(loginResponse);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to register the user", e);
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
     */
    @PostMapping(value = "/login")
    public ResponseEntity<?> loginUser(@RequestBody LogInDto loginDto/*, HttpServletResponse response*/) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
    	LoginResponse loginResponse = userAuthService.loginUser(loginDto);
    	/*Cookie cookie = new Cookie("accessToken", null);
    	cookie.setPath("/");
    	cookie.setMaxAge((int) TokenService.ACCESS_TOKEN_EXPIRATION_TIME.toSeconds());
    	cookie.setHttpOnly(true);
    	cookie.setSecure(true);
    	response.addCookie(cookie);*/
    	
    	return createAuthenticationResponse(loginResponse);
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
     */
    @PutMapping("/current")
    public void updateUserInformation(@RequestBody UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
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
    public UserInformationDto getCurrentUserDto() throws UserNotLoggedInException {
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