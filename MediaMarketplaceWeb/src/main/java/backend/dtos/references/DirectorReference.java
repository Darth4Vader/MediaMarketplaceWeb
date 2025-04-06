package backend.dtos.references;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for referencing a director in a movie, using their id.
 */
public class DirectorReference {

    /**
     * The unique media identifier for the person.
     * This field must not be blank.
     */
    @NotBlank
    private String personMediaID;

    /**
     * The unique identifier for the movie.
     * This field cannot be null.
     */
    @Nonnull
    private Long movieId;

    /**
     * The unique media identifier for the movie.
     * This field cannot be null.
     */
    @Nonnull
    private String movieMediaId;

    /**
     * The unique identifier for the person.
     * This field cannot be null.
     */
    @Nonnull
    private Long personId;

    /**
     * Gets the unique media identifier for the person.
     * 
     * @return the unique media identifier of the person
     */
    public String getPersonMediaID() {
        return personMediaID;
    }

    /**
     * Sets the unique media identifier for the person.
     * 
     * @param personMediaID the unique media identifier of the person
     */
    public void setPersonMediaID(String personMediaID) {
        this.personMediaID = personMediaID;
    }

    /**
     * Gets the unique identifier for the movie.
     * 
     * @return the unique identifier of the movie
     */
    public Long getMovieId() {
        return movieId;
    }

    /**
     * Sets the unique identifier for the movie.
     * 
     * @param movieId the unique identifier of the movie
     */
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    /**
     * Gets the unique media identifier for the movie.
     * 
     * @return the unique media identifier of the movie
     */
    public String getMovieMediaId() {
        return movieMediaId;
    }

    /**
     * Sets the unique media identifier for the movie.
     * 
     * @param movieMediaId the unique media identifier of the movie
     */
    public void setMovieMediaId(String movieMediaId) {
        this.movieMediaId = movieMediaId;
    }

    /**
     * Gets the unique identifier for the person.
     * 
     * @return the unique identifier of the person
     */
    public Long getPersonId() {
        return personId;
    }

    /**
     * Sets the unique identifier for the person.
     * 
     * @param personId the unique identifier of the person
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}