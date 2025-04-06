package backend.tmdb;

/**
 * A class representing a name and the associated exception that occurred.
 * This class is used to encapsulate information about a specific problem encountered,
 * including the name related to the issue and the exception that was thrown.
 */
public class NameAndException {

    /**
     * The name associated with the exception.
     */
    private String name;

    /**
     * The exception that was thrown.
     */
    private Exception exception;

    /**
     * Constructs a NameAndException with the specified name and exception.
     *
     * @param name the name associated with the exception
     * @param exception the exception that was thrown
     */
    public NameAndException(String name, Exception exception) {
        this.name = name;
        this.exception = exception;
    }

    /**
     * Gets the name associated with the exception.
     *
     * @return the name associated with the exception
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with the exception.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the exception that was thrown.
     *
     * @return the exception that was thrown
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Sets the exception that was thrown.
     *
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}