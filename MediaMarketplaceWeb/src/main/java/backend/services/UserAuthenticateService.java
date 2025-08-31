package backend.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.DataUtils;
import backend.auth.AuthenticateAdmin;
import backend.dtos.users.LogInDto;
import backend.dtos.users.LoginResponse;
import backend.dtos.users.RegisterLocal;
import backend.dtos.users.UserBasicInformationDto;
import backend.dtos.users.UserInformationDto;
import backend.dtos.users.VerifyAccountDto;
import backend.entities.AccountVerificationToken;
import backend.entities.RefreshToken;
import backend.entities.Role;
import backend.entities.User;
import backend.entities.enums.RoleType;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.JwtTokenExpiredException;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserAlreadyExistsException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserNotLoggedInException;
import backend.exceptions.UserNotVerifiedException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.exceptions.enums.UserLogInfo;
import backend.repositories.AccountVerificationTokenRepository;
import backend.repositories.RoleRepository;
import backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service for user authentication and registration.
 * <p>
 * This service handles user registration, login, and updates. It uses Spring Security's {@link AuthenticationManager}
 * to manage authentication and a {@link PasswordEncoder} for password encryption. It also manages user roles and
 * provides methods to check if the current user has admin privileges.
 * </p>
 */
@Service
public class UserAuthenticateService {
	
	public static final Duration ACCOUNT_VERIFICATION_EXPIRATION_TIME = Duration.ofMinutes(1); // 1 minute
	public static final Duration ACCOUNT_VERIFICATION_EXPIRATION_COOLDOWN = Duration.ofSeconds(10); // 10 seconds

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private AccountVerificationTokenRepository accountVerificationTokenRepository;

    /**
     * Registers a new user with the provided information.
     * <p>
     * This method validates the registration information, checks if the email already exists, encodes the user's password,
     * assigns default roles, and saves the new user. After registration, it logs the user in automatically.
     * </p>
     * 
     * @param registerDto The DTO containing user registration information.
     * @return A success message if the registration is successful.
     * @throws UserAlreadyExistsException If a user with the same email already exists.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     */
    @Transactional
    public AccountVerificationToken registerUser(RegisterLocal registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
    	validateRegisteration(registerDto);
    	// Check that the email does not exist
        String email = registerDto.getEmail();
        Optional<User> existingUserOpt = findUserByEmail(email); 
        if (existingUserOpt.isPresent()) {
        	// check if account is not verified and the duration for verification token is not expired
        	boolean userExists = true;
        	User existingUser = existingUserOpt.get();
        	if(!existingUser.isAccountValidated()) {
        		AccountVerificationToken token = getActiveAccountVerificationToken(existingUser);
				if(token == null) {
					// Account verification token expired, delete the token and the user and notify them to register again
					userRepository.delete(existingUser);
					userExists = false;
				}
        	}
        	if(userExists) {
        		// if we reach here, then the user already exists and is verified
        		throw new UserAlreadyExistsException();
        	}
        }
        
        // Register the new user
        email = DataUtils.emailFormatted(email);
        String encodedPassword = encodePassword(registerDto.getPassword());
        User user = new User(email, encodedPassword, getUserDefaultRoles());
        user.setAccountValidated(false); // Local users need to verify their email
        String name = registerDto.getName();
        if(DataUtils.isNotBlank(name)) {
        	user.setName(name);
        }
        user = userRepository.save(user);
        
        // after registering user, create account verification token
        return createAccountVerificationToken(user, registerDto.getRedirectUrl());
    }
    
    @Transactional
    public User registerViaOAuth(UserInformationDto registerDto) throws LogValuesAreIncorrectException {
    	String email = registerDto.getEmail();
    	checkForEmailException(email);
    	email = DataUtils.emailFormatted(email);

        // Create and save the new user
        User newUser = new User(email, getUserDefaultRoles());
        newUser.setAccountValidated(true); // OAuth users are considered verified
        String name = registerDto.getName();
        if(DataUtils.isNotBlank(name)) {
        	newUser.setName(name);
        }
        return userRepository.save(newUser);
    }
    
    @Transactional
    public User getUserViaOAuth(OAuth2User oauth2) throws LogValuesAreIncorrectException {
        // Try to find the user by email, otherwise register a new one
        String email = oauth2.getAttribute("email");
    	Optional<User> userOpt = findUserByEmail(email);
    	User user = null;
    	if(userOpt.isPresent()) {
    		user = userOpt.get();
    	}
    	else {
            UserInformationDto userInfo = new UserInformationDto();
            userInfo.setEmail(email);
			user = registerViaOAuth(userInfo);
    	}
        
        // oauth verified email, so we set the user as verified
        verifyAccount(user);
        
        //update user info from oauth2 provider
        user.setName(oauth2.getAttribute("name"));
        user.setProfilePicture(oauth2.getAttribute("picture"));
        return userRepository.save(user);
    }
    
    public LoginResponse loginViaOAuth(User user) {
        // Generate oauth2 tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        
        return loginResponse;
    }
    
    @Transactional
    private AccountVerificationToken createAccountVerificationToken(User user, String redirectUrl) {
		AccountVerificationToken accountVerificationToken = new AccountVerificationToken();
		accountVerificationToken.setToken(UUID.randomUUID().toString());
		accountVerificationToken.setUser(user);
		accountVerificationToken.setCreatedDate(LocalDateTime.now());
		accountVerificationToken.setDuration(ACCOUNT_VERIFICATION_EXPIRATION_TIME);
		accountVerificationToken.setRedirectUrl(redirectUrl);
		return accountVerificationTokenRepository.save(accountVerificationToken);
    }
    
    @Transactional
    public void verifyAccount(VerifyAccountDto verifyAccountDto) throws EntityNotFoundException {
    	// check that the token is valid
		String token = verifyAccountDto != null ? verifyAccountDto.getToken() : null;
		Optional<AccountVerificationToken> tokenOpt = accountVerificationTokenRepository.findByToken(token);
		if(tokenOpt.isEmpty()) {
			//token not found,, throw exception
			throw new EntityNotFoundException("Account verification token not found");
		}
		AccountVerificationToken verifyToken = tokenOpt.get();
		// check if token is expired
		if (!DataUtils.isUseable(verifyToken.getCreatedDate(), verifyToken.getDuration())) {
			//token expired, delete it and throw exception
			accountVerificationTokenRepository.delete(verifyToken);
			throw new EntityNotFoundException("Account verification token has expired");
		}
		// load the user
		User user = verifyToken.getUser();
		if(user == null) {
			//user not found,, throw exception
			throw new EntityNotFoundException("User not found for token");
		}
		// set user as verified
		user.setAccountValidated(true);
		userRepository.save(user);
		// delete the token
		accountVerificationTokenRepository.delete(verifyToken);
    }
    
    @Transactional
    public void verifyAccount(User user) {
    	if(user.isAccountValidated()) {
			//user already verified
			return;
		}
    	// remove setted password, to prevent malicious activity
    	user.setPassword(null);
		// set user as verified
		user.setAccountValidated(true);
		userRepository.save(user);
    	// load the token
		Optional<AccountVerificationToken> tokenOpt = accountVerificationTokenRepository.findByUser(user);
		if(tokenOpt.isPresent()) {
			AccountVerificationToken verifyToken = tokenOpt.get();
			// delete the token
			accountVerificationTokenRepository.delete(verifyToken);
		}
    }
    
    private Set<Role> getUserDefaultRoles() {
    	// Create the authorities for the new user
		Set<Role> roles = new HashSet<>();
		roles.add(getRoleByType(RoleType.ROLE_USER));
		return roles;
	}

    /**
     * Logs in a user using the provided login information.
     * <p>
     * This method authenticates the user and generates a JWT token if the credentials are valid.
     * </p>
     * 
     * @param loginDto The DTO containing login information.
     * @return A String of the generated JWT token.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws UserPasswordIsIncorrectException If the password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     * @throws UserNotVerifiedException 
     */
    public LoginResponse loginUser(LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException, UserNotVerifiedException {
    	String email = DataUtils.emailFormatted(loginDto.getEmail());
    	String password = loginDto.getPassword();
		// Check for missing values
    	checkForEmailPasswordException(email, password);
    	// If we can't find the user by their email, then they don't exist
        User user = getExistingUserByEmail(email);
        
        if(user.getPassword() == null) {
			// User registered via OAuth2 and does not have a password set
			throw new UserPasswordIsIncorrectException("User registered via OAuth2, no password set");
		}

        try {
        	// Set as the current authentication user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password, user.getAuthorities()));
            // Set as the current authentication user
            setAuthentication(auth);
            
            //check if user is verified
            try {
            	checkIfUserIsVerified(user);
            }
            catch(UserNotVerifiedException e) {
				SecurityContextHolder.getContext().setAuthentication(null);
				// Issue new account verification token if user tries login after the previous one cooldown expired
				AccountVerificationToken token = getActiveAccountVerificationToken(user);
				if(token != null && !DataUtils.isUseable(token.getCreatedDate(), ACCOUNT_VERIFICATION_EXPIRATION_COOLDOWN)) {
					// Account verification token expired, delete the token and the user and notify them to register again
					VerifyAccountDto verifyAccountDto = new VerifyAccountDto();
					verifyAccountDto.setEmail(user.getEmail());
					String redirectUrl = token.getRedirectUrl();
					token.setUser(null);
					accountVerificationTokenRepository.delete(token);
					AccountVerificationToken newToken = createAccountVerificationToken(user, redirectUrl);
					verifyAccountDto.setToken(newToken.getToken());
					throw new UserNotVerifiedException(verifyAccountDto, redirectUrl, "User email is not verified, new verification email sent");
				}
				throw e;
			}
            
            // Generate the JWT token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            String accessToken = tokenService.generateAccessToken(user);
            return new LoginResponse(accessToken, refreshToken.getToken());
        } catch (AuthenticationException e) {
            // If there is a problem with authenticating the user, the password is incorrect
        	// Because we check that the email exists, so it can only be the password.
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new UserPasswordIsIncorrectException();
        }
    }

    /**
     * Updates the current user's information.
     * <p>
     * This method updates user details if the current user is logged in and has the correct password.
     * </p>
     * 
     * @param userDto The DTO containing user information to be updated.
     * @throws UserNotLoggedInException If the user is not logged in.
     * @throws UserPasswordIsIncorrectException If the provided password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided values are incorrect.
     * @throws UserNotVerifiedException 
     * @throws UserDoesNotExistsException 
     */
    @Transactional
    public void updateUserInformation(UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException, UserDoesNotExistsException, UserNotVerifiedException {
        // Check that the current user is trying to change their own information
        User authUser = tokenService.getCurretUser();
        
        //first check if verified
        checkIfUserIsVerified(authUser);
        
        //check that the email matches the logged in user
        String email = authUser.getEmail();
        email = DataUtils.emailFormatted(email);
        if (!Objects.equals(email, userDto.getEmail())) {
            throw new UserNotLoggedInException("The user: " + userDto.getEmail() + " is not logged in, cannot update information");
        }

        // Load the user from the database
        User user = findUserByEmail(email)
            .orElseThrow(() -> new UserNotLoggedInException("User not found"));

        user.setName(userDto.getName());
        
        userRepository.save(user);

        // After the change, re-login the user again with their updated information
        reloadAuthentication(user);
    }
    
    public boolean loginUserFromToken(String token, HttpServletRequest request) throws UserDoesNotExistsException {
    	String email = tokenService.extractUsername(token);
        //if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if(email != null) {
        	System.out.println(email);
        	User user = getExistingUserByEmail(email);
            if (tokenService.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                setAuthentication(authenticationToken);
                return true;
            }
            else if(tokenService.isTokenExpired(token)) {
            	throw new JwtTokenExpiredException("Access Token Expired. Client must refresh token");
            }
        }
        return false;
    }
    
    public LoginResponse refreshLoginToken(String refreshToken) throws EntityNotFoundException {
    	RefreshToken newRefreshToken = refreshTokenService.refreshToken(refreshToken);
        String accessToken = tokenService.generateAccessToken(newRefreshToken.getUser());
        return new LoginResponse(accessToken, newRefreshToken.getToken());
    }
    
    /**
     * Signs out the currently logged-in user by invalidating their authentication token.
     * 
     * This method first checks if there is a currently logged-in user by attempting to retrieve
     * the current user's token. If a user is logged in, their authentication state is set to null,
     * effectively signing them out. If no user is logged in, a {@link UserNotLoggedInException} 
     * is thrown to indicate that there is no active session to terminate.
     * 
     * @throws UserNotLoggedInException if no user is currently logged in
     */
    public void logoutFromCurrentUser() throws UserNotLoggedInException {
        // Check if there is a logged user in order to allow sign out.
        tokenService.getCurretUser();
        setAuthentication(null);
    }
    
    public void logoutRefreshToken(String refreshToken) throws EntityNotFoundException {
        // Check if there is a logged user in order to allow sign out.
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
    
    @Transactional
    public void checkIfUserIsVerified(User user) throws UserDoesNotExistsException, UserNotVerifiedException {
        if(!user.isAccountValidated()) {
            AccountVerificationToken token = getActiveAccountVerificationToken(user);
            SecurityContextHolder.getContext().setAuthentication(null);
            if(token == null) {
				// User is not verified, authentication is cleared
				throw new UserDoesNotExistsException("Account verification token expired, please register again");
            }
            else {
            	throw new UserNotVerifiedException("User email is not verified");
            }
		}
	}
    
    @Transactional
    public AccountVerificationToken getActiveAccountVerificationToken(User user) {
        // if email and password are correct, check if the user is verified
    	AccountVerificationToken token = null;
    	if(!user.isAccountValidated()) {
        	// Account is not validated from email verification
        	// see if account verification token is expired
			Optional<AccountVerificationToken> tokenOpt = accountVerificationTokenRepository.findByUser(user);
			if(tokenOpt.isPresent()) {
				token = tokenOpt.get();
				// if the token exists, check if it is expired
	        	if(!DataUtils.isUseable(token.getCreatedDate(), token.getDuration())) {
					// Account verification token expired, delete the token and the user and notify them to register again
					accountVerificationTokenRepository.delete(token);
					token = null;
				}
			}
        }
    	return token;
	}

    /**
     * Verifies if the user is currently logged in by checking the authentication token.
     * 
     * @throws UserNotLoggedInException If no user is logged in.
     */
    public void authenticateLoggedUser() throws UserNotLoggedInException {
        tokenService.getCurretUser();
    }

    /**
     * Checks if the current user is an admin.
     * <p>
     * This method uses the {@link AuthenticateAdmin} annotation to check the user's admin status.
     * </p>
     */
    @AuthenticateAdmin
    public void checkIfCurrentUserIsAdmin() {
        // Method annotated to check admin status
    }

    /**
     * Determines if the current user is an admin.
     * <p>
     * This method checks the user's roles to determine if they have admin privileges.
     * </p>
     * 
     * @return true if the current user has admin privileges; false otherwise.
     */
    public boolean isCurrentUserAdmin() {
        User user = tokenService.getCurretUser();
        Role adminRole = getRoleByType(RoleType.ROLE_ADMIN);
        Collection<? extends GrantedAuthority> roles = user.getAuthorities();
        return roles != null && roles.contains(adminRole);
    }

    /**
     * Retrieves the UserInformationDto (like email, name) for the currently logged-in user.
     * <p>
     * This method converts the currently authenticated {@link User} to a {@link UserInformationDto}.
     * </p>
     * 
     * @return The {@link UserInformationDto} for the current user.
     * @throws UserNotLoggedInException If no user is logged in.
     */
    public UserBasicInformationDto getCurrentUserDto() throws UserNotLoggedInException {
        User user = tokenService.getCurretUser();
        UserBasicInformationDto userDto = new UserBasicInformationDto();
        userDto.setName(user.getName());
        userDto.setProfilePicture(user.getProfilePicture());
        
    	return userDto;
    }
    
	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(DataUtils.emailFormatted(email));
	}
	
	public User getExistingUserByEmail(String email) throws UserDoesNotExistsException {
		return findUserByEmail(email)
				.orElseThrow(() -> new UserDoesNotExistsException());
	}
    
    /**
     * Retrieves a {@link Role} by its type. If the role does not exist, it is created and saved to the repository.
     * 
     * @param roleType The type of the role to retrieve.
     * @return The {@link Role} associated with the given type.
     */
    public Role getRoleByType(RoleType roleType) {
    	// Attempt to find the role in the repository
        Optional<Role> userRole = roleRepository.findByRoleType(roleType);
        if(userRole.isPresent())
        	return userRole.get();
        // If does not exists, then we will add the role to the database and return it.
        Role role = new Role(roleType);
        role = roleRepository.save(role);
        return role;
    }

    /**
     * Reloads the authentication for the given user.
     * <p>
     * This method creates a new {@link Authentication} object for the specified user and updates the
     * {@link SecurityContextHolder} with the new authentication. It is typically used after updating user
     * information to refresh the security context.
     * </p>
     * 
     * @param user The {@link User} object representing the user to be authenticated.
     */
    public void reloadAuthentication(User user) {
    	reloadAuthentication(user, user.getPassword(), user.getAuthorities());
    }
    
    /**
     * Reloads the authentication for the given user principal and returns the new {@link Authentication} object.
     * <p>
     * This method creates a new {@link Authentication} object using the provided principal, password, and
     * authorities, updates the {@link SecurityContextHolder} with the new authentication, and returns it. It is
     * typically used to refresh the security context after changing user credentials or authorities.
     * </p>
     * 
     * @param principal The principal (user) to be authenticated. This can be any object that represents the user.
     * @param password The password of the user to be authenticated.
     * @param authorities A collection of {@link GrantedAuthority} representing the user's authorities.
     * @return The new {@link Authentication} object that has been set in the {@link SecurityContextHolder}.
     */
    private Authentication reloadAuthentication(Object principal, String password, Collection <? extends GrantedAuthority> authorities) {
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, authorities);
        // Set as the current authentication user
        setAuthentication(auth);
        return auth;
    }
    
    /**
     * Sets the provided {@link Authentication} object as the current authentication user in the
     * {@link SecurityContextHolder}.
     * <p>
     * This method updates the {@link SecurityContextHolder} with the given {@link Authentication} object
     * and ensures that the security context supports threading of child threads by setting the strategy to
     * {@link SecurityContextHolder#MODE_INHERITABLETHREADLOCAL}.
     * </p>
     * 
     * @param auth The {@link Authentication} object to be set as the current authentication.
     */
    private void setAuthentication(Authentication auth) {
        // Set that the security context support threading of children.
    	// Set as the current authentication user
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Validates the user information DTO.
     * <p>
     * This method checks for inconsistencies in the user data transfer object (DTO), such as password mismatches
     * and invalid values. It throws exceptions if the DTO is invalid.
     * </p>
     * 
     * @param userDto The {@link UserInformationDto} object containing user information to be validated.
     * @throws LogValuesAreIncorrectException If any of the provided values are incorrect, or if the passwords do not match.
     */
    private void validateRegisteration(RegisterLocal registerDto) throws LogValuesAreIncorrectException {
    	Map<UserLogInfo, String> logInfo = new HashMap<>();
        String email = registerDto.getEmail();
        String password = registerDto.getPassword();
        String passwordConfirm = registerDto.getPasswordConfirm();
        loadExceptionsMailPassword(email, password, logInfo);
        loadExceptionsPasswordConfirm(passwordConfirm, logInfo);
        if (!logInfo.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfo, "One or more values are missing or incorrect");
        }
        if (!Objects.equals(password, passwordConfirm)) {
        	logInfo.put(UserLogInfo.PASSWORD, "Field is not matching");
        	logInfo.put(UserLogInfo.PASSWORD_CONFIRM, "Field is not matching");
            throw new LogValuesAreIncorrectException(logInfo, "Password confirmation does not match");
        }
    }

    /**
     * Encodes the provided password using a password encoder.
     * <p>
     * This method applies encoding to the given plain text password to ensure it is stored securely. The encoded password
     * can then be used for authentication purposes.
     * </p>
     * 
     * @param password The plain text password to be encoded.
     * @return The encoded password as a {@link String}.
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Converts a {@link User} entity to a {@link UserInformationDto}.
     * 
     * @param user The {@link User} entity to convert.
     * @return The corresponding {@link UserInformationDto}.
     */
    public UserInformationDto convertUserToDto(User user) {
        UserInformationDto userDto = new UserInformationDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }
    
    public static void checkForEmailException(String email) throws LogValuesAreIncorrectException {
        Map<UserLogInfo, String> logInfo = new HashMap<>();
        loadMailExceptions(email, logInfo);
        if (!logInfo.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfo, "One or more values are missing");
        }
    }

    /**
     * Checks for missing or incorrect values in the provided email and password.
     * <p>
     * This method verifies that both the email and password are not blank. If either value is missing,
     * an exception is thrown with details about the missing values.
     * </p>
     * 
     * @param email The email to be checked.
     * @param password The password to be checked.
     * @throws LogValuesAreIncorrectException if any of the values (email or password) are missing or incorrect.
     */
    public static void checkForEmailPasswordException(String email, String password) throws LogValuesAreIncorrectException {
        Map<UserLogInfo, String> logInfo = new HashMap<>();
        loadExceptionsMailPassword(email, password, logInfo);
        if (!logInfo.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfo, "One or more values are missing");
        }
    }
    
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
    	    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public static boolean validateEmail(String emailStr) {
	        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
	        return matcher.matches();
	}

    /**
     * Loads exceptions for missing values based on email and password.
     * <p>
     * This helper method adds the appropriate {@link UserLogInfo} values to the provided set if the email or password
     * are blank. This helps in identifying which values are missing.
     * </p>
     * 
     * @param email The email to be checked.
     * @param password The password to be checked.
     * @param logInfoSet The set to be populated with missing value information.
     */
    private static void loadExceptionsMailPassword(String email, String password, Map<UserLogInfo, String> logInfo) {
    	loadMailExceptions(email, logInfo);
    	loadExceptionsPassword(password, logInfo);
    }
    
    private static void loadMailExceptions(String email, Map<UserLogInfo, String> logInfo) {
        if (DataUtils.isBlank(email)) {
        	logInfo.put(UserLogInfo.EMAIL, "Please fill this field");
        } else if(!validateEmail(email)) {
        	logInfo.put(UserLogInfo.EMAIL, "Email is not valid");
        }
    }
    
    public static void loadExceptionsPassword(String password, Map<UserLogInfo, String> logInfo) {
        if (DataUtils.isBlank(password)) {
        	logInfo.put(UserLogInfo.PASSWORD, "Please fill this field");
        }
    }
    
    public static void loadExceptionsPasswordConfirm(String passwordConfirm, Map<UserLogInfo, String> logInfo) {
        if (DataUtils.isBlank(passwordConfirm)) {
        	logInfo.put(UserLogInfo.PASSWORD_CONFIRM, "Please fill this field");
        }
    }
}