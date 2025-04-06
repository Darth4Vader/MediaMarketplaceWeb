package backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents an actor in the database.
 * This entity corresponds to the 'actors' table and includes fields that map to its columns.
 * 
 * <p>
 * The Actor class is used to represent the relationship between an actor and a movie, 
 * including the specific role the actor plays and linking to both the movie hew plays in and the person representing the actor.
 * </p>
 * 
 */
@Entity
@Table(name = "actors")
public class Actor {

    /**
     * The unique identifier for this actor.
     * This field is the primary key of the 'actors' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The role name of the actor in the movie.
     * This field maps to the 'role_name' column in the 'actors' table.
     * 
     * @return the role name of the actor
     */
    @Column(name = "role_name", nullable = false)
    @NotBlank
    private String roleName;

    /**
     * The movie in which the actor performs.
     * This field maps to the 'movie_id' column in the 'actors' table.
     * 
     * @return the movie associated with this actor
     */
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;

    /**
     * The person who is the actor.
     * This field maps to the 'person_id' column in the 'actors' table.
     * 
     * @return the person who is the actor
     */
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
    private Person person;

    /**
     * Gets the unique identifier for this actor.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the role name of the actor in the movie.
     * 
     * @return the role name of the actor
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Gets the movie in which the actor performs.
     * 
     * @return the movie associated with this actor
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Gets the person who is the actor.
     * 
     * @return the person who is the actor
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the role name of the actor in the movie.
     * 
     * @param roleName the role name to set
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Sets the movie in which the actor performs.
     * 
     * @param movie the movie to set
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * Sets the person who is the actor.
     * 
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }
}