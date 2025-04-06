package backend.dtos;

/**
 * Data Transfer Object for representing a director in a movie.
 */
public class DirectorDto {

    /**
     * The ID of the movie directed by the director.
     */
    private Long movieId;

    /**
     * The person who directed the movie.
     */
    private PersonDto person;

    /**
     * Gets the movie ID.
     * 
     * @return the ID of the movie
     */
    public Long getMovieId() {
        return movieId;
    }

    /**
     * Sets the movie ID.
     * 
     * @param movieId the ID of the movie
     */
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    /**
     * Gets the person who directed the movie.
     * 
     * @return the person who directed the movie
     */
    public PersonDto getPerson() {
        return person;
    }

    /**
     * Sets the person who directed the movie.
     * 
     * @param person the person who directed the movie
     */
    public void setPerson(PersonDto person) {
        this.person = person;
    }
}