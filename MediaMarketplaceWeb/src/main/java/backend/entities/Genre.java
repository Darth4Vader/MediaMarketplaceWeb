package backend.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a genre in the database.
 * This entity corresponds to the 'genres' table and includes fields that map to its columns.
 * 
 * <p>
 * The Genre class is used to categorize movies by genre. It maintains a bidirectional many-to-many
 * relationship with the Movie class, allowing for efficient queries and management of genres associated with movies.
 * </p>
 * 
 */
@Entity
@Table(name = "genres")
public class Genre {

    /**
     * The unique identifier for this genre.
     * This field is the primary key of the 'genres' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The list of movies associated with this genre.
     * This field represents the many-to-many relationship between genres and movies.
     * It is fetched lazily to optimize performance.
     * 
     * @return the list of movies associated with this genre
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "genres")
    private List<Movie> movies;
    
    /**
     * The name of the genre.
     * This field maps to the 'name' column in the 'genres' table and must be unique and not blank.
     * 
     * @return the name of the genre
     */
    @Column(nullable = false, unique = true)
    @NotBlank
    private String name;
    
    /**
     * Default constructor for the Genre class.
     */
    public Genre() {
    }
    
    /**
     * Constructs a Genre instance with the specified name.
     * 
     * @param name the name of the genre
     */
    public Genre(@NotBlank String name) {
        this.name = name;
    }

    /**
     * Gets the unique identifier for this genre.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the genre.
     * 
     * @return the name of the genre
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the genre.
     * 
     * @param name the name to set
     */
    public void setGenreName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the list of movies associated with this genre.
     * 
     * @return the list of movies associated with this genre
     */
    public List<Movie> getMovies() {
        return movies;
    }

    /**
     * Calculates the hash code for this genre based on its unique identifier.
     * 
     * @return the hash code for this genre
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compares this genre with another object for equality.
     * Two genres are considered equal if they have the same unique identifier.
     * 
     * @param obj the object to compare with
     * @return {@code true} if this genre is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Genre other = (Genre) obj;
        return Objects.equals(id, other.id);
    }
}