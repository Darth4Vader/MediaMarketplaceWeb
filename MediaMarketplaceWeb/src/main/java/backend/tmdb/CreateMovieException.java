package backend.tmdb;

import java.util.List;

/**
 * Exception thrown when there are issues creating movies, such as validation failures
 * or other errors during the movie creation process.
 * This exception contains a list of {@link NameAndException} instances, each representing
 * a specific issue encountered.
 */
public class CreateMovieException extends Exception {

    /**
     * Unique identifier for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A list of {@link NameAndException} instances, each detailing a specific problem encountered.
     */
    private List<NameAndException> list;

    /**
     * Constructs a new CreateMovieException with the specified detail message and a list of exceptions.
     *
     * @param list the list of {@link NameAndException} instances detailing the problems encountered
     * @param message the detail message
     */
    public CreateMovieException(List<NameAndException> list, String message) {
        super(message);
        this.list = list;
    }

    /**
     * Gets the list of {@link NameAndException} instances detailing the problems encountered.
     *
     * @return the list of {@link NameAndException} instances
     */
    public List<NameAndException> getList() {
        return list;
    }

    /**
     * Sets the list of {@link NameAndException} instances detailing the problems encountered.
     *
     * @param list the list of {@link NameAndException} instances to set
     */
    public void setList(List<NameAndException> list) {
        this.list = list;
    }
}