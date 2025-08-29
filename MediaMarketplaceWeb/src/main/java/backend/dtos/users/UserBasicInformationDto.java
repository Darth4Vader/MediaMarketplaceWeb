package backend.dtos.users;

public class UserBasicInformationDto {

    /**
     * The full name of the user.
     */
    private String name;
    
    private String profilePicture;

    /**
     * Gets the full name of the user.
     * 
     * @return the full name of the user
     */
    public String getName() {
        return name;
    }
    
    public String getProfilePicture() {
    	return profilePicture;
    }

    /**
     * Sets the full name of the user.
     * 
     * @param name the full name of the user
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
}
