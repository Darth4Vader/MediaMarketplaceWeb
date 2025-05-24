package backend.dtos.admin;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public class ActorAdminReference {

    /**
     * The unique media identifier for the person (actor).
     */
    @NotBlank
    private String personMediaID;

    /**
     * The name of the role played by the actor.
     */
    @NotBlank
    private String roleName;

    /**
     * The unique media identifier for the movie in which the actor played.
     */
    @Nonnull
    private String movieMediaId;

    /**
     * The unique identifier for the person (actor).
     */
    @Nonnull
    private Long personId;

    /**
     * Gets the unique media identifier for the person (actor).
     * 
     * @return the unique media identifier of the person
     */
    public String getPersonMediaID() {
        return personMediaID;
    }

    /**
     * Sets the unique media identifier for the person (actor).
     * 
     * @param personMediaID the unique media identifier of the person
     */
    public void setPersonMediaID(String personMediaID) {
        this.personMediaID = personMediaID;
    }

    /**
     * Gets the name of the role played by the actor.
     * 
     * @return the name of the role
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets the name of the role played by the actor.
     * 
     * @param roleName the name of the role
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Gets the unique media identifier for the movie in which the actor played.
     * 
     * @return the unique media identifier of the movie
     */
    public String getMovieMediaId() {
        return movieMediaId;
    }

    /**
     * Sets the unique media identifier for the movie in which the actor played.
     * 
     * @param movieMediaId the unique media identifier of the movie
     */
    public void setMovieMediaId(String movieMediaId) {
        this.movieMediaId = movieMediaId;
    }

    /**
     * Gets the unique identifier for the person (actor).
     * 
     * @return the unique identifier of the person
     */
    public Long getPersonId() {
        return personId;
    }

    /**
     * Sets the unique identifier for the person (actor).
     * 
     * @param personId the unique identifier of the person
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}