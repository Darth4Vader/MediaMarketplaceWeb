package backend.exceptions;

/**
 * Exception thrown when an entity is not found in the database or the system.
 * This exception is used to indicate that a requested entity could not be located,
 * which might occur when querying by ID or other unique identifiers.
 * 
 * For example, this exception might be thrown when attempting to retrieve a movie,
 * actor, or user that does not exist in the system.
 * 
 * <p>This exception extends {@link Exception}, indicating that it is a checked exception,
 * and must be either caught or declared to be thrown in method signatures.</p>
 */
public class EntityNotFoundException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code EntityNotFoundException} with no detail message.
     */
    public EntityNotFoundException() {
        super();
    }

    /**
     * Constructs a new {@code EntityNotFoundException} with the specified detail message.
     * 
     * @param message the detail message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}