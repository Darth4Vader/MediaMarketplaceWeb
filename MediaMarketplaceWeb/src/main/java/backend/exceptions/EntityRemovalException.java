package backend.exceptions;

/**
 * Exception thrown when there is an error removing an entity.
 * <p>
 * This exception is a specific type of {@link EntityAccessException} used to indicate
 * issues that occur during the removal of an entity. It extends {@code EntityAccessException}
 * and inherits its properties.
 * </p>
 * 
 * @see {@link EntityAccessException}
 */
public class EntityRemovalException extends EntityAccessException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code EntityRemovalException} with {@code null} as its detail message.
     */
    public EntityRemovalException() {
        super();
    }

    /**
     * Constructs a new {@code EntityRemovalException} with the specified detail message.
     * 
     * @param message The detail message, saved for later retrieval by the {@link #getMessage()} method.
     */
    public EntityRemovalException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code EntityRemovalException} with the specified cause.
     * 
     * @param cause The cause, saved for later retrieval by the {@link #getCause()} method.
     */
    public EntityRemovalException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code EntityRemovalException} with the specified detail message and cause.
     * 
     * @param message The detail message, saved for later retrieval by the {@link #getMessage()} method.
     * @param cause The cause, saved for later retrieval by the {@link #getCause()} method.
     */
    public EntityRemovalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code EntityRemovalException} with the specified detail message, cause, suppression enabled or disabled,
     * and writable stack trace enabled or disabled.
     * 
     * @param message The detail message, saved for later retrieval by the {@link #getMessage()} method.
     * @param cause The cause, saved for later retrieval by the {@link #getCause()} method.
     * @param enableSuppression Whether or not suppression is enabled or disabled.
     * @param writableStackTrace Whether or not the stack trace should be writable.
     */
    public EntityRemovalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}