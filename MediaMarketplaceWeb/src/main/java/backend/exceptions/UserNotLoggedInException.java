package backend.exceptions;

/**
 * Exception thrown when a user is not logged in.
 * <p>
 * This exception is a runtime exception used to signal that an operation
 * requiring an authenticated user is attempted when no user is currently
 * logged in. It extends {@link RuntimeException} and provides various 
 * constructors to support different ways of initializing the exception.
 * </p>
 */
public class UserNotLoggedInException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code UserNotLoggedInException} with {@code null}
     * as its detail message. The cause is not initialized.
     */
    public UserNotLoggedInException() {
        super();
    }

    /**
     * Constructs a new {@code UserNotLoggedInException} with the specified
     * detail message. The cause is not initialized.
     * 
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public UserNotLoggedInException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code UserNotLoggedInException} with the specified
     * cause and a detail message of {@code (cause==null ? null : cause.toString())}.
     * 
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public UserNotLoggedInException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code UserNotLoggedInException} with the specified
     * detail message and cause.
     * 
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public UserNotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code UserNotLoggedInException} with the specified
     * detail message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     * 
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @param enableSuppression whether or not suppression is enabled or
     *                          disabled
     * @param writableStackTrace whether or not the stack trace should be
     *                           writable
     */
    public UserNotLoggedInException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}