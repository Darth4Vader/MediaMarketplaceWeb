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
    private String title;

    /**
     * The content of the movie review.
     */
    private String content;

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
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the movie review.
     * 
     * @param title the title of the review
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the content of the movie review.
     * 
     * @return the content of the review
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the movie review.
     * 
     * @param content the content of the review
     */
    public void setContent(String content) {
        this.content = content;
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