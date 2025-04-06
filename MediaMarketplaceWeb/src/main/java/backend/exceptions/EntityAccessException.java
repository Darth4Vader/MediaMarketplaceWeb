package backend.exceptions;

/**
 * Custom exception to handle access-related errors for entities.
 * <p>
 * This exception is thrown when there is an issue accessing or manipulating an entity.
 * </p>
 * 
 */
public class EntityAccessException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code EntityAccessException} with {@code null} as its detail message.
     */
    public EntityAccessException() {
        super();
    }

    /**
     * Constructs a new {@code EntityAccessException} with the specified detail message.
     * 
     * @param message The detail message, saved for later retrieval by the {@link #getMessage()} method.
     */
    public EntityAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code EntityAccessException} with the specified cause.
     * 
     * @param cause The cause, saved for later retrieval by the {@link #getCause()} method.
     */
    public EntityAccessException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code EntityAccessException} with the specified detail message and cause.
     * 
     * @param message The detail message, saved for later retrieval by the {@link #getMessage()} method.
     * @param cause The cause, saved for later retrieval by the {@link #getCause()} method.
     */
    public EntityAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code EntityAccessException} with the specified detail message, cause, suppression enabled or disabled,
     * and writable stack trace enabled or disabled.
     * 
     * @param message The detail message, saved for later retrieval by the {@link #getMessage()} method.
     * @param cause The cause, saved for later retrieval by the {@link #getCause()} method.
     * @param enableSuppression Whether or not suppression is enabled or disabled.
     * @param writableStackTrace Whether or not the stack trace should be writable.
     */
    public EntityAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}