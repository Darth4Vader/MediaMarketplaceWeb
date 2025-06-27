package backend.exceptions.enums;

import com.fasterxml.jackson.annotation.JsonValue;

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
    CREATED_DATE("createdDate"),

    /**
     * Represents the rating given to the movie in the review.
     */
    RATING("rating"),

    /**
     * Represents the title of the movie being reviewed.
     */
    TITLE("title"),

    /**
     * Represents the content of the review for the movie.
     */
    REVIEW("review");
	
	private String value;
	
	private MovieReviewTypes(String value) {
		this.value = value;
	}
	
	@JsonValue
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}