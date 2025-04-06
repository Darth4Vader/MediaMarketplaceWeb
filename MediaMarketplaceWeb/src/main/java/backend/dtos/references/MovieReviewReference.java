package backend.dtos.references;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Data Transfer Object for referencing a movie review.
 * Extends {@link MovieRatingReference} to include review-specific details.
 */
public class MovieReviewReference extends MovieRatingReference {

    /**
     * The title of the movie review.
     */
    private String reviewTitle;

    /**
     * The content of the movie review.
     */
    private String review;

    /**
     * The date and time when the review was created.
     * Formatted as "dd-MM-yyyy".
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime createdDate;

    /**
     * Gets the title of the movie review.
     * 
     * @return the title of the review
     */
    public String getReviewTitle() {
        return reviewTitle;
    }

    /**
     * Sets the title of the movie review.
     * 
     * @param reviewTitle the title of the review
     */
    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    /**
     * Gets the content of the movie review.
     * 
     * @return the content of the review
     */
    public String getReview() {
        return review;
    }

    /**
     * Sets the content of the movie review.
     * 
     * @param review the content of the review
     */
    public void setReview(String review) {
        this.review = review;
    }

    /**
     * Gets the date and time when the review was created.
     * 
     * @return the creation date and time of the review
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the date and time when the review was created.
     * 
     * @param createdDate the creation date and time of the review
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}