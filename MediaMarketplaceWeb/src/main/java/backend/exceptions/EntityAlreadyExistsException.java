package backend.exceptions;

/**
 * Exception thrown when attempting to create or add an entity that already exists.
 * This exception is used in scenarios where the entity being created or added is
 * already present in the database, violating unique constraints or business rules.
 * 
 * For example, this exception might be thrown when trying to add an actor to a movie
 * where the actor is already listed in the movie's cast.
 * 
 * <p>This exception extends {@link Exception}, indicating that it is a checked exception,
 * and must be either caught or declared to be thrown in method signatures.</p>
 */
public class EntityAlreadyExistsException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new {@code EntityAlreadyExistsException} with no detail message.
     */
    public EntityAlreadyExistsException() {
        super();
    }

    /**
     * Constructs a new {@code EntityAlreadyExistsException} with the specified detail message.
     * 
     * @param message the detail message
     */
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}