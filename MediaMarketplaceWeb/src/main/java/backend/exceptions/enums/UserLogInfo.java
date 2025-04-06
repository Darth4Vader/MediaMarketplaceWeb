package backend.exceptions.enums;

/**
 * Enum representing different types of information related to user login and registration processes.
 * <p>
 * This enum can be used to specify or validate various user-related attributes such as name, password, and password confirmation.
 * </p>
 */
public enum UserLogInfo {
    
    /**
     * Represents the user's name or username.
     */
    NAME,

    /**
     * Represents the user's password.
     */
    PASSWORD,

    /**
     * Represents the confirmation of the user's password for validation purposes.
     */
    PASSWORD_CONFIRM;
}