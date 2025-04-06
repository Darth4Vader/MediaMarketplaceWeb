package backend.tmdb;

import backend.dtos.CreateMovieDto;
import backend.exceptions.EntityAlreadyExistsException;

/**
 * Exception thrown to indicate that an existing movie can be updated.
 * It provides details about the movie to be updated and the original exception that caused this situation.
 */
public class CanUpdateException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * The data transfer object containing information about the movie to be updated.
     */
    private CreateMovieDto createMovieDto;

    /**
     * The exception that was thrown, indicating that the entity already exists.
     */
    private EntityAlreadyExistsException exception;

    /**
     * Constructs a CanUpdateException with the specified movie data and the original exception.
     *
     * @param createMovieDto the data transfer object containing movie details
     * @param exception the exception indicating that the entity already exists
     */
    public CanUpdateException(CreateMovieDto createMovieDto, EntityAlreadyExistsException exception) {
        this.createMovieDto = createMovieDto;
        this.exception = exception;
    }

    /**
     * Gets the data transfer object containing information about the movie to be updated.
     *
     * @return the CreateMovieDto associated with this exception
     */
    public CreateMovieDto getCreateMovieDto() {
        return createMovieDto;
    }

    /**
     * Sets the data transfer object containing information about the movie to be updated.
     *
     * @param createMovieDto the CreateMovieDto to set
     */
    public void setCreateMovieDto(CreateMovieDto createMovieDto) {
        this.createMovieDto = createMovieDto;
    }

    /**
     * Gets the exception that indicates that the entity already exists.
     *
     * @return the EntityAlreadyExistsException associated with this exception
     */
    public EntityAlreadyExistsException getException() {
        return exception;
    }

    /**
     * Sets the exception that indicates that the entity already exists.
     *
     * @param exception the EntityAlreadyExistsException to set
     */
    public void setException(EntityAlreadyExistsException exception) {
        this.exception = exception;
    }
}