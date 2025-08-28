package backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import backend.entities.User;
import backend.repositories.UserRepository;

/**
 * Service implementation for managing user details and authentication.
 * <p>
 * This service implements the {@link UserDetailsService} interface provided by Spring Security,
 * allowing for the loading of user-specific data during authentication. It interacts with the UserRepository
 * to retrieve user details based on the email.
 * </p>
 */
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a user by their email.
     * <p>
     * This method queries the UserRepository to find a user with the specified email. If the user is not found,
     * a {@link emailNotFoundException} is thrown.
     * </p>
     * 
     * @param email The email of the user to be retrieved.
     * @return The User entity corresponding to the provided email.
     * @throws emailNotFoundException if no user is found with the specified email.
     */
    public User getUserByEmail(String email) throws UsernameNotFoundException {
    	return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }
    
    /**
     * Loads user-specific data for the given email.
     * <p>
     * This method is required by the {@link UserDetailsService} interface and retrieves a UserDetails object
     * for the specified email. It uses the {@link #getUserByemail(String)} method to fetch the user details.
     * </p>
     * 
     * @param email The email of the user to be loaded.
     * @return A {@link UserDetails} object representing the user with the specified email.
     * @throws emailNotFoundException if no user is found with the specified email.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByEmail(username);
    }
}