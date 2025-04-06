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
 * to retrieve user details based on the username.
 * </p>
 */
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a user by their username.
     * <p>
     * This method queries the UserRepository to find a user with the specified username. If the user is not found,
     * a {@link UsernameNotFoundException} is thrown.
     * </p>
     * 
     * @param username The username of the user to be retrieved.
     * @return The User entity corresponding to the provided username.
     * @throws UsernameNotFoundException if no user is found with the specified username.
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    /**
     * Loads user-specific data for the given username.
     * <p>
     * This method is required by the {@link UserDetailsService} interface and retrieves a UserDetails object
     * for the specified username. It uses the {@link #getUserByUsername(String)} method to fetch the user details.
     * </p>
     * 
     * @param username The username of the user to be loaded.
     * @return A {@link UserDetails} object representing the user with the specified username.
     * @throws UsernameNotFoundException if no user is found with the specified username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }
}