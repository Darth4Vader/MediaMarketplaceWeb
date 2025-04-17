package backend.dtos;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import backend.dtos.references.MovieReviewReference;

/**
 * Data Transfer Object for representing a movie review.
 */
public class MovieReviewDto {

    /**
     * Reference to the movie review.
     */
	@JsonUnwrapped
    private MovieReviewReference movieReview;

    /**
     * The username of the person who wrote the review.
     */
    private String username;

    /**
     * Gets the reference to the movie review.
     * 
     * @return the reference to the movie review
     */
    public MovieReviewReference getMovieReview() {
        return movieReview;
    }

    /**
     * Sets the reference to the movie review.
     * 
     * @param movieReview the reference to the movie review
     */
    public void setMovieReview(MovieReviewReference movieReview) {
        this.movieReview = movieReview;
    }

    /**
     * Gets the username of the person who wrote the review.
     * 
     * @return the username of the reviewer
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the person who wrote the review.
     * 
     * @param username the username of the reviewer
     */
    public void setUsername(String username) {
        this.username = username;
    }
}