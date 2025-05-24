package backend.dtos.references;

import jakarta.annotation.Nonnull;

/**
 * Data Transfer Object for referencing a director in a movie, using their id.
 */
public class DirectorReference {

    /**
     * The unique identifier for the movie.
     * This field cannot be null.
     */
    @Nonnull
    private Long movieId;

    /**
     * The unique identifier for the person.
     * This field cannot be null.
     */
    @Nonnull
    private Long personId;

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