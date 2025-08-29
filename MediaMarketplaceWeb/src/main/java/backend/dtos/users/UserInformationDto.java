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
}