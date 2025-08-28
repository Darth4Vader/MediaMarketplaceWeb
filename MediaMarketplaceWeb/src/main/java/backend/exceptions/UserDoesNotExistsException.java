package backend.exceptions;

/**
 * Exception thrown when an attempt is made to access a user that does not exist in the system.
 * <p>
 * This exception is typically used in situations where a user lookup fails because the user with the
 * specified email or identifier could not be found.
 * </p>
 */
public class UserDoesNotExistsException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code UserDoesNotExistsException} with a default detail message.
     * <p>
     * The default message is "The User does not exists for the given username".
     * </p>
     */
    public UserDoesNotExistsException() {
        super("The User does not exists for the given email");
    }

    /**
     * Constructs a new {@code UserDoesNotExistsException} with the specified detail message.
     * 
     * @param message The detail message.
     */
    public UserDoesNotExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code UserDoesNotExistsException} with the specified cause.
     * 
     * @param cause The cause of the exception.
     */
    public UserDoesNotExistsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code UserDoesNotExistsException} with the specified detail message and cause.
     * 
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public UserDoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code UserDoesNotExistsException} with the specified detail message, cause, suppression
     * enabled or disabled, and writable stack trace enabled or disabled.
     * 
     * @param message The detail message.
     * @param cause The cause of the exception.
     * @param enableSuppression Whether or not suppression is enabled.
     * @param writableStackTrace Whether or not the stack trace should be writable.
     */
    public UserDoesNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}