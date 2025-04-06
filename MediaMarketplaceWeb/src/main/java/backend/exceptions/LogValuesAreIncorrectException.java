package backend.exceptions;

import java.util.Set;
import backend.exceptions.enums.UserLogInfo;

/**
 * Exception thrown when one or more of the login values provided by a user are incorrect or missing.
 * <p>
 * This exception includes a set of {@link UserLogInfo} enums which indicate which specific login values are incorrect
 * or missing. It also includes a message that describes the nature of the error.
 * </p>
 */
public class LogValuesAreIncorrectException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    /**
     * A set of {@link UserLogInfo} enums representing which login values are incorrect or missing.
     */
	private Set<UserLogInfo> logInfoSet;

    /**
     * Constructs a new {@code LogValuesAreIncorrectException} with the specified detail message and a set of
     * {@link UserLogInfo} enums indicating the incorrect or missing values.
     * 
     * @param logInfoSet A set of {@link UserLogInfo} enums representing which login values are incorrect or missing.
     * @param message A detail message explaining the exception.
     */
    public LogValuesAreIncorrectException(Set<UserLogInfo> logInfoSet, String message) {
        super(message);
        this.logInfoSet = logInfoSet;
    }

    /**
     * Retrieves the set of {@link UserLogInfo} enums that specify which login values are incorrect or missing.
     * 
     * @return A set of {@link UserLogInfo} enums.
     */
    public Set<UserLogInfo> getUserLogInfo() {
        return this.logInfoSet;
    }
}