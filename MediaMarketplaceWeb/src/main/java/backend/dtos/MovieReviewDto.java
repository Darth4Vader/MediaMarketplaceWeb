package backend.dtos;

import backend.dtos.references.MovieReviewReference;

/**
 * Data Transfer Object for representing a movie review.
 */
public class MovieReviewDto {

    /**
     * Reference to the movie review.
     */
    private MovieReviewReference movieReview;

    /**
     * The username of the person who wrote the review.
     */
    private String userName;

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
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the person who wrote the review.
     * 
     * @param userName the username of the reviewer
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}