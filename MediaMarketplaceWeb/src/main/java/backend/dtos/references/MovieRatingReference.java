package backend.dtos.references;

/**
 * Data Transfer Object for referencing a movie rating by a user, using their id.
 */
public class MovieRatingReference {

    /**
     * The unique identifier for the movie.
     */
    private Long movieId;

    /**
     * The unique identifier for the user who rated the movie.
     */
    private Long userId;

    /**
     * The rating given to the movie by the user.
     * A value between 1 and 100.
     */
    private Integer rating;

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
     * Gets the unique identifier for the user who rated the movie.
     * 
     * @return the unique identifier of the user
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier for the user who rated the movie.
     * 
     * @param userId the unique identifier of the user
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the rating given to the movie by the user.
     * 
     * @return the rating given to the movie
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Sets the rating given to the movie by the user.
     * 
     * @param rating the rating given to the movie
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}