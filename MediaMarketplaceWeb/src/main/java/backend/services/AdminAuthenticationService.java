package backend.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.DataUtils;
import backend.dtos.users.AdminLogInDto;
import backend.dtos.users.LoginResponse;
import backend.entities.User;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.UserDoesNotExistsException;
import backend.exceptions.UserPasswordIsIncorrectException;
import backend.exceptions.enums.UserLogInfo;

@Service
public class AdminAuthenticationService {
	
	@Autowired
	private UserAuthenticateService userAuthenticateService;

    public LoginResponse loginAdmin(AdminLogInDto loginDto) throws LogValuesAreIncorrectException, UserDoesNotExistsException, UserPasswordIsIncorrectException {
    	String username = loginDto.getUsername();
    	String password = loginDto.getPassword();
		// Check for missing values
    	checkForUsernamePasswordException(username, password);
    	// If we can't find the user by their username, then they don't exist
    	User user = userAuthenticateService.getExistingUserByEmail(username);
    	
    	// this login is only for admins
    	// we check if the user is admin
    	if(!userAuthenticateService.isUserAdmin(user)) {
    		throw new UserDoesNotExistsException("User with the given username does not exist");
    	}
    	
    	// try login authentication
    	userAuthenticateService.loginAuthentication(username, password, user);
        
        // Generate the JWT token
        return userAuthenticateService.generateLoginResponseForUser();
    }
    
    public static void checkForUsernamePasswordException(String username, String password) throws LogValuesAreIncorrectException {
        Map<UserLogInfo, String> logInfo = new HashMap<>();
        loadExceptionsUsername(username, logInfo);
        UserAuthenticateService.loadExceptionsPassword(password, logInfo);
        if (!logInfo.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfo, "One or more values are missing");
        }
	}
    
    private static void loadExceptionsUsername(String password, Map<UserLogInfo, String> logInfo) {
        if (DataUtils.isBlank(password)) {
        	logInfo.put(UserLogInfo.USERNAME, "Please fill this field");
        }
    }
}