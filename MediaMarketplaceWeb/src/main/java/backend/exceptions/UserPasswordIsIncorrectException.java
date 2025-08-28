package backend.exceptions;

/**
 * Exception thrown when the password provided for a given email is incorrect.
 * <p>
 * This exception is used in scenarios where a user attempts to authenticate with an incorrect password.
 * </p>
 */
public class UserPasswordIsIncorrectException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code UserPasswordIsIncorrectException} with a default detail message.
     * <p>
     * The default message is "The password is incorrect for the given email".
     * </p>
     */
    public UserPasswordIsIncorrectException() {
        super("The password is incorrect for the given email");
    }

    /**
     * Constructs a new {@code UserPasswordIsIncorrectException} with the specified detail message.
     * 
     * @param message The detail message.
     */
    public UserPasswordIsIncorrectException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code UserPasswordIsIncorrectException} with the specified cause.
     * 
     * @param cause The cause of the exception.
     */
    public UserPasswordIsIncorrectException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code UserPasswordIsIncorrectException} with the specified detail message and cause.
     * 
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public UserPasswordIsIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code UserPasswordIsIncorrectException} with the specified detail message, cause, suppression
     * enabled or disabled, and writable stack trace enabled or disabled.
     * 
     * @param message The detail message.
     * @param cause The cause of the exception.
     * @param enableSuppression Whether or not suppression is enabled.
     * @param writableStackTrace Whether or not the stack trace should be writable.
     */
    public UserPasswordIsIncorrectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}