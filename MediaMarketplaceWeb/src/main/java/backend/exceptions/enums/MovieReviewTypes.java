package backend.exceptions.enums;

/**
 * Enum representing different types of attributes or fields related to movie reviews.
 * <p>
 * This enum can be used to specify or filter movie reviews based on different attributes such as 
 * creation date, rating, title, or the review content itself.
 * </p>
 */
public enum MovieReviewTypes {
    
    /**
     * Represents the date when the movie review was created.
     */
    CREATED_DATE,

    /**
     * Represents the rating given to the movie in the review.
     */
    RATING,

    /**
     * Represents the title of the movie being reviewed.
     */
    TITLE,

    /**
     * Represents the content of the review for the movie.
     */
    REVIEW;
}