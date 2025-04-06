package backend.dtos.users;

/**
 * Data Transfer Object for user information, including login credentials and user details.
 */
public class UserInformationDto {

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The full name of the user.
     */
    private String name;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The confirmation of the user's password to verify it matches.
     */
    private String passwordConfirm;

    /**
     * Gets the username of the user.
     * 
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the full name of the user.
     * 
     * @return the full name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name of the user.
     * 
     * @param name the full name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the password of the user.
     * 
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * 
     * @param password the password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the confirmation of the user's password.
     * 
     * @return the password confirmation
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * Sets the confirmation of the user's password.
     * 
     * @param passwordConfirm the password confirmation
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}