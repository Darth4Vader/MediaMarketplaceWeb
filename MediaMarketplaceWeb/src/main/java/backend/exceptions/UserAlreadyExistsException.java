package backend.exceptions;

/**
 * Exception thrown when an attempt is made to create a user that already exists in the system.
 * <p>
 * This exception is typically used during user registration to indicate that the email
 * being used for the new user account is already taken by an existing user.
 * </p>
 */
public class UserAlreadyExistsException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code UserAlreadyExistsException} with a default detail message.
     * <p>
     * The default message is "The user already exists".
     * </p>
     */
    public UserAlreadyExistsException() {
        super("The user already exists");
    }
}