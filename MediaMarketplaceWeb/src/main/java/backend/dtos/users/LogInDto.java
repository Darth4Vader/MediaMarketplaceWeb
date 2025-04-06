package backend.dtos.users;

/**
 * Data Transfer Object for user login information.
 */
public class LogInDto {

    /**
     * The username of the user attempting to log in.
     */
    private String username;

    /**
     * The password of the user attempting to log in.
     */
    private String password;

    /**
     * Gets the username of the user attempting to log in.
     * 
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user attempting to log in.
     * 
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
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