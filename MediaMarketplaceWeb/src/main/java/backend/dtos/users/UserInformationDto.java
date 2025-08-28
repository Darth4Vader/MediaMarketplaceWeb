package backend.dtos.users;

/**
 * Data Transfer Object for user information, including login credentials and user details.
 */
public class UserInformationDto {

    /**
     * The email of the user.
     */
    private String email;

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
     * Gets the email of the user.
     * 
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     * 
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
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