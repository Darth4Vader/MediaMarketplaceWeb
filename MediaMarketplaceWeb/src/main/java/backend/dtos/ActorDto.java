package backend.dtos;

/**
 * Data Transfer Object for representing an actor in a movie.
 */
public class ActorDto {

    /**
     * The ID of the movie in which the actor played a role.
     */
    private Long movieId;

    /**
     * The person who played the role in the movie.
     */
    private PersonDto person;

    /**
     * The name of the role played by the actor.
     */
    private String roleName;

    /**
     * Gets the ID of the movie.
     * 
     * @return the ID of the movie in which the actor played a role
     */
    public Long getMovieId() {
        return movieId;
    }

    /**
     * Sets the ID of the movie.
     * 
     * @param movieId the ID of the movie in which the actor played a role
     */
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    /**
     * Gets the person who played the role.
     * 
     * @return the {@link PersonDto} representing the actor
     */
    public PersonDto getPerson() {
        return person;
    }

    /**
     * Sets the person who played the role.
     * 
     * @param person the {@link PersonDto} representing the actor
     */
    public void setPerson(PersonDto person) {
        this.person = person;
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
}