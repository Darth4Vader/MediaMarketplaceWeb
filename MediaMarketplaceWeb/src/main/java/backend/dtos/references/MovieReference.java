package backend.dtos.references;

import java.util.Objects;

/**
 * Data Transfer Object for referencing a movie, using their id.
 */
public class MovieReference {

    /**
     * The unique identifier for the movie.
     */
    private Long id;

    /**
     * The name of the movie.
     */
    private String name;

    /**
     * The path to the movie's poster image.
     */
    private String posterPath;

    /**
     * Gets the unique identifier for the movie.
     * 
     * @return the unique identifier of the movie
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the movie.
     * 
     * @param id the unique identifier of the movie
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the movie.
     * 
     * @return the name of the movie
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the movie.
     * 
     * @param name the name of the movie
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the path to the movie's poster image.
     * 
     * @return the poster path of the movie
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Sets the path to the movie's poster image.
     * 
     * @param posterPath the path to the movie's poster image
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * Checks if this movie reference is equal to another object, using their id
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MovieReference other = (MovieReference) obj;
        return Objects.equals(id, other.id);
    }

    /**
     * Computes the hash code for this movie reference.
     * 
     * @return the hash code of this movie reference
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}