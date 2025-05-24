package backend.dtos.references;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for referencing an actor and their role in a movie, using their id.
 */
public class ActorReference {
	
    /**
     * The unique identifier for the person (actor).
     */
    @Nonnull
    private Long personId;
    
    @Nonnull
    private Long movieId;

    /**
     * The name of the role played by the actor.
     */
    @NotBlank
    private String roleName;

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