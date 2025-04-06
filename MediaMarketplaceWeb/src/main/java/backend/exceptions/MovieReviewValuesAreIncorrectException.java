package backend.exceptions;

import java.util.Map;

import backend.exceptions.enums.MovieReviewTypes;

/**
 * Exception thrown when values provided in a movie review Data Transfer Object (DTO) are incorrect or invalid.
 * This exception indicates that one or more fields in the movie review DTO do not meet the required validation criteria.
 * <p>
 * The exception contains a map where each entry associates a {@link MovieReviewTypes} value with an error message,
 * providing detailed information about which specific fields are incorrect and the nature of the validation errors.
 * </p>
 * 
 * Example use cases:
 * <ul>
 *     <li>When a user submits a movie review with invalid values such as a rating outside the acceptable range.</li>
 *     <li>When a movie review DTO contains fields that exceed maximum length constraints.</li>
 * </ul>
 */
public class MovieReviewValuesAreIncorrectException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** A map of {@link MovieReviewTypes} to error messages indicating which fields are invalid. */
    private Map<MovieReviewTypes, String> map;

    /**
     * Constructs a new MovieReviewValuesAreIncorrectException with the specified map of validation errors.
     * 
     * @param map A map where each key is a {@link MovieReviewTypes} value indicating the field type, 
     *            and each value is a string describing the validation error associated with that field.
     */
    public MovieReviewValuesAreIncorrectException(Map<MovieReviewTypes, String> map) {
        this.map = map;
    }

    /**
     * Returns the map of validation errors.
     * 
     * @return A map where each key is a {@link MovieReviewTypes} value indicating the field type, 
     *         and each value is a string describing the validation error associated with that field.
     */
    public Map<MovieReviewTypes, String> getMap() {
        return map;
    }
}