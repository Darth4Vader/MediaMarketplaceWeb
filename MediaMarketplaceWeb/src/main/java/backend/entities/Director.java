package backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a director in the database.
 * This entity corresponds to the 'directors' table and includes fields that map to its columns.
 * 
 * <p>
 * The Director class links a director to a specific movie and person. It maintains the relationship between 
 * directors, movies, and individuals who are directors of movies.
 * </p>
 * 
 */
@Entity
@Table(name = "directors")
public class Director {

    /**
     * The unique identifier for this director.
     * This field is the primary key of the 'directors' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The movie directed by this director.
     * This field maps to the 'movie_id' column in the 'directors' table.
     * 
     * @return the movie associated with this director
     */
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;
    
    /**
     * The person who is the director.
     * This field maps to the 'person_id' column in the 'directors' table.
     * 
     * @return the person who is the director
     */
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
    private Person person;

    /**
     * Gets the unique identifier for this director.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the movie directed by this director.
     * 
     * @return the movie associated with this director
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Gets the person who is the director.
     * 
     * @return the person who is the director
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the movie directed by this director.
     * 
     * @param movie the movie to set
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * Sets the person who is the director.
     * 
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }
}