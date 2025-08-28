package backend.dtos.users;

/**
 * Data Transfer Object for user login information.
 */
public class LogInDto {

    /**
     * The email of the user attempting to log in.
     */
    private String email;

    /**
     * The password of the user attempting to log in.
     */
    private String password;

    /**
     * Gets the email of the user attempting to log in.
     * 
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user attempting to log in.
     * 
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user attempting to log in.
     * 
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user attempting to log in.
     * 
     * @param password the password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
}