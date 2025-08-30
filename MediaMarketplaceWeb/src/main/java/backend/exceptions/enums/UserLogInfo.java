package backend.exceptions.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing different types of information related to user login and registration processes.
 * <p>
 * This enum can be used to specify or validate various user-related attributes such as name, password, and password confirmation.
 * </p>
 */
public enum UserLogInfo {
    
    /**
     * Represents the user's name or email.
     */
    EMAIL("email"),

    /**
     * Represents the user's password.
     */
    PASSWORD("password"),

    /**
     * Represents the confirmation of the user's password for validation purposes.
     */
    PASSWORD_CONFIRM("passwordConfirm"),
	
    HUMAN_VERIFICATION("humanVerification");
	
	private String value;
	
	private UserLogInfo(String value) {
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