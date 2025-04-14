package backend.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.DataUtils;
import backend.auth.AuthenticateAdmin;
import backend.dtos.users.LogInDto;
import backend.dtos.users.LoginResponse;
import backend.dtos.users.RefreshTokenRequest;
import backend.dtos.users.UserInformationDto;
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
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.exceptions.enums.UserLogInfo;
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

    /**
     * Registers a new user with the provided information.
     * <p>
     * This method validates the registration information, checks if the username already exists, encodes the user's password,
     * assigns default roles, and saves the new user. After registration, it logs the user in automatically.
     * </p>
     * 
     * @param registerDto The DTO containing user registration information.
     * @return A success message if the registration is successful.
     * @throws UserAlreadyExistsException If a user with the same username already exists.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     */
    @Transactional
    public LoginResponse registerUser(UserInformationDto registerDto) throws UserAlreadyExistsException, LogValuesAreIncorrectException, UserPasswordIsIncorrectException {
        // Check that the username does not exist
        validateUserDto(registerDto);
        String username = registerDto.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        // Encode the password
        String password = registerDto.getPassword();
        String encodedPassword = encodePassword(password);

        // Create the authorities for the new user
        Role userRole = getRoleByType(RoleType.ROLE_USER);
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        // Create and save the new user
        User newUser = new User(username, encodedPassword, authorities);
        userRepository.save(newUser);

        // Log in the user after registration
        try {
            return loginUser(username, password);
        } catch (UserDoesNotExistsException | UserPasswordIsIncorrectException | LogValuesAreIncorrectException e) {
        	// We just created so no exception can be made
        	// Handle unexpected exceptions that shouldn't occur after successful registration
            throw new RuntimeException("Error during login after registration", e);
        }
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
     */
    public LoginResponse loginUser(LogInDto loginDto) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        return loginUser(username, password);
    }

    /**
     * Logs in a user with the specified username and password.
     * <p>
     * This method performs authentication and generates a JWT token if the credentials are valid.
     * </p>
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @return A String of the generated JWT token.
     * @throws UserDoesNotExistsException If the user does not exist.
     * @throws UserPasswordIsIncorrectException If the password is incorrect.
     * @throws LogValuesAreIncorrectException If the provided login values are incorrect.
     */
    private LoginResponse loginUser(String username, String password) throws UserDoesNotExistsException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
        checkForException(username, password);
        Optional<User> userOpt = userRepository.findByUsername(username);

        // If we can't find the user by their username, then they don't exist
        if (userOpt.isEmpty()) {
            throw new UserDoesNotExistsException();
        }

        try {
        	// Set as the current authentication user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password, userOpt.get().getAuthorities()));
            // Set as the current authentication user
            setAuthentication(auth);
            // Generate the JWT token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userOpt.get());
            String accessToken = tokenService.generateAccessToken(userOpt.get());
            return new LoginResponse(accessToken, refreshToken.getToken());
        } catch (AuthenticationException e) {
            // If there is a problem with authenticating the user, the password is incorrect
        	// Because we check that the username exists, so it can only be the password.
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
     */
    @Transactional
    public void updateUserInformation(UserInformationDto userDto) throws UserNotLoggedInException, UserPasswordIsIncorrectException, LogValuesAreIncorrectException {
        // Check that the current user is trying to change their own information
        User authUser = tokenService.getCurretUser();
        String username = authUser.getUsername();
        if (!Objects.equals(username, userDto.getUsername())) {
            throw new UserNotLoggedInException("The user: " + userDto.getUsername() + " is not logged in, cannot update information");
        }

        String password = userDto.getPassword();
        String passwordConfirm = userDto.getPasswordConfirm();

        // Load the user from the database
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotLoggedInException("User not found"));

        if (DataUtils.isNotBlank(password) || DataUtils.isNotBlank(passwordConfirm)) {
            // If there is a new password, check that it matches the confirm password
        	// Otherwise throw an exception for the user to know the problem
        	checkForException(userDto);
            if (!Objects.equals(password, passwordConfirm)) {
                throw new UserPasswordIsIncorrectException("Password not matching");
            }
            // After the change we will reloging the user again to a new session with his updated information.
            user.setPassword(encodePassword(password));
        }

        user.setName(userDto.getName());

        // After the change, re-login the user again with their updated information
        reloadAuthentication(user);
    }
    
    public boolean loginUserFromToken(String token, HttpServletRequest request) throws UserDoesNotExistsException {
    	String username = tokenService.extractUsername(token);
        //if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if(username != null) {
        	System.out.println(username);
            User user = getUserByUsername(username);
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
    
    public LoginResponse refreshLoginToken(RefreshTokenRequest refreshTokenRequest) throws EntityNotFoundException {
    	RefreshToken refreshToken = refreshTokenService.refreshToken(refreshTokenRequest.getRefreshToken());
        String accessToken = tokenService.generateAccessToken(refreshToken.getUser());
        return new LoginResponse(accessToken, refreshToken.getToken());
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
    public void signOutFromCurrentUser() throws UserNotLoggedInException {
        // Check if there is a logged user in order to allow sign out.
        tokenService.getCurretUser();
        setAuthentication(null);
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
     * Retrieves the UserInformationDto (like username, name) for the currently logged-in user.
     * <p>
     * This method converts the currently authenticated {@link User} to a {@link UserInformationDto}.
     * </p>
     * 
     * @return The {@link UserInformationDto} for the current user.
     * @throws UserNotLoggedInException If no user is logged in.
     */
    public UserInformationDto getCurrentUserDto() throws UserNotLoggedInException {
        return convertUserToDto(tokenService.getCurretUser());
    }
    
	public User getUserByUsername(String username) throws UserDoesNotExistsException {
		return userRepository.findByUsername(username)
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
    private void reloadAuthentication(User user) {
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
    private void validateUserDto(UserInformationDto userDto) throws LogValuesAreIncorrectException {
        checkForException(userDto);
        String password = userDto.getPassword();
        if (!Objects.equals(password, userDto.getPasswordConfirm())) {
        	Set<UserLogInfo> logInfoSet = new HashSet<>();
			logInfoSet.add(UserLogInfo.PASSWORD);
        	logInfoSet.add(UserLogInfo.PASSWORD_CONFIRM);
            throw new LogValuesAreIncorrectException(logInfoSet, "Password confirmation does not match");
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
    private String encodePassword(String password) {
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
        userDto.setUsername(user.getUsername());
        userDto.setName(user.getName());
        return userDto;
    }

    /**
     * Checks for missing or incorrect values in the provided username and password.
     * <p>
     * This method verifies that both the username and password are not blank. If either value is missing,
     * an exception is thrown with details about the missing values.
     * </p>
     * 
     * @param username The username to be checked.
     * @param password The password to be checked.
     * @throws LogValuesAreIncorrectException if any of the values (username or password) are missing or incorrect.
     */
    public static void checkForException(String username, String password) throws LogValuesAreIncorrectException {
        Set<UserLogInfo> logInfoSet = new HashSet<>();
        loadExceptions(username, password, logInfoSet);
        if (!logInfoSet.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfoSet, "One or more values are missing");
        }
    }

    /**
     * Checks for missing or incorrect values in the provided UserInformationDto.
     * <p>
     * This method verifies that the username, password, and password confirmation fields are not blank. 
     * If any of these fields are missing, or if the password confirmation does not match, an exception is thrown
     * with details about the missing values.
     * </p>
     * 
     * @param userInformationDto The UserInformationDto containing user information to be checked.
     * @throws LogValuesAreIncorrectException if any of the values (username, password, or password confirmation) are missing or incorrect.
     */
    public static void checkForException(UserInformationDto userInformationDto) throws LogValuesAreIncorrectException {
        Set<UserLogInfo> logInfoSet = new HashSet<>();
        String username = userInformationDto.getUsername();
        String password = userInformationDto.getPassword();
        String passwordConfirm = userInformationDto.getPasswordConfirm();
        loadExceptions(username, password, logInfoSet);
        if (DataUtils.isBlank(passwordConfirm)) {
            logInfoSet.add(UserLogInfo.PASSWORD_CONFIRM);
        }
        if (!logInfoSet.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfoSet, "One or more values are missing or incorrect");
        }
    }

    /**
     * Loads exceptions for missing values based on username and password.
     * <p>
     * This helper method adds the appropriate {@link UserLogInfo} values to the provided set if the username or password
     * are blank. This helps in identifying which values are missing.
     * </p>
     * 
     * @param username The username to be checked.
     * @param password The password to be checked.
     * @param logInfoSet The set to be populated with missing value information.
     */
    private static void loadExceptions(String username, String password, Set<UserLogInfo> logInfoSet) {
        if (DataUtils.isBlank(username)) {
            logInfoSet.add(UserLogInfo.NAME);
        }
        if (DataUtils.isBlank(password)) {
            logInfoSet.add(UserLogInfo.PASSWORD);
        }
    }
}