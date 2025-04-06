package backend.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a movie in the database.
 * This entity corresponds to the 'movies' table and includes fields that map to its columns.
 * 
 * <p>
 * The Movie class encapsulates all relevant details about a movie, including its title, release date, and associated
 * genres. It also maintains relationships with directors, actors, and genres through various mappings.
 * </p>
 * 
 */
@Entity
@Table(name = "movies")
public class Movie {
	
    /**
     * The unique identifier for this movie.
     * This field is the primary key of the 'movies' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The media ID associated with this movie.
     * This field maps to the 'media_id' column in the 'movies' table.
     * 
     * @return the media ID
     */
    @Column(name = "media_id", nullable = false, unique = true)
    private String mediaID;
    
    /**
     * The name of the movie.
     * This field maps to the 'name' column in the 'movies' table and cannot be blank.
     * 
     * @return the name of the movie
     */
    @NotBlank
    private String name;
    
    /**
     * The runtime of the movie in minutes.
     * 
     * @return the runtime of the movie
     */
    private Integer runtime;
    
    /**
     * The path to the movie's poster image.
     * This field maps to the 'poster_path' column in the 'movies' table.
     * 
     * @return the poster path
     */
    @Column(name = "poster_path")
    private String posterPath;
    
    /**
     * The path to the movie's backdrop image.
     * This field maps to the 'backdrop_path' column in the 'movies' table.
     * 
     * @return the backdrop path
     */
    @Column(name = "backdrop_path")
    private String backdropPath;
    
    /**
     * A brief synopsis of the movie.
     * This field maps to the 'synopsis' column in the 'movies' table and can contain up to 1000 characters.
     * 
     * @return the synopsis of the movie
     */
    @Column(length = 1000)
    private String synopsis;
    
    /**
     * The year the movie was released.
     * 
     * @return the release year of the movie
     */
    private Integer year;
    
    /**
     * The release date of the movie.
     * This field maps to the 'release_date' column in the 'movies' table.
     * The date format is "dd-MM-yyyy".
     * 
     * @return the release date of the movie
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    /**
     * The list of directors associated with this movie.
     * This field represents a one-to-many relationship between movies and directors.
     * The directors are fetched eagerly and are managed with cascading operations.
     * 
     * @return the list of directors for this movie
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Director> directors;
    
    /**
     * The list of actors and their roles in this movie.
     * This field represents a one-to-many relationship between movies and actors.
     * The actors are fetched eagerly and are managed with cascading operations.
     * 
     * @return the list of actors' roles in this movie
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Actor> actorsRoles;
    
    /**
     * The list of genres associated with this movie.
     * This field represents a many-to-many relationship between movies and genres.
     * 
     * @return the list of genres for this movie
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;

    /**
     * Default constructor for the Movie class.
     * Initializes the lists of directors and actor roles.
     */
    public Movie() {
        this.directors = new ArrayList<>();
        this.actorsRoles = new ArrayList<>();
    }

    /**
     * Gets the unique identifier for this movie.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the media ID associated with this movie.
     * 
     * @return the media ID
     */
    public String getMediaID() {
        return mediaID;
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
     * Gets the path to the movie's poster image.
     * 
     * @return the poster path
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Gets the list of genres associated with this movie.
     * 
     * @return the list of genres for this movie
     */
    public List<Genre> getGenres() {
        return genres;
    }

    /**
     * Sets the unique identifier for this movie.
     * 
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the media ID associated with this movie.
     * 
     * @param mediaID the media ID to set
     */
    public void setMediaID(String mediaID) {
        this.mediaID = mediaID;
    }

    /**
     * Sets the name of the movie.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the path to the movie's poster image.
     * 
     * @param posterPath the poster path to set
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * Sets the list of genres associated with this movie.
     * 
     * @param genres the list of genres to set
     */
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    /**
     * Gets the synopsis of the movie.
     * 
     * @return the synopsis of the movie
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * Sets the synopsis of the movie.
     * 
     * @param synopsis the synopsis to set
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * Gets the year the movie was released.
     * 
     * @return the release year of the movie
     */
    public Integer getYear() {
        return year;
    }
    
    /**
     * Checks if the movie has a specified release year.
     * 
     * @return {@code true} if the release year is not null, {@code false} otherwise
     */
    public boolean hasYear() {
        return year != null;
    }

    /**
     * Gets the list of actors and their roles in this movie.
     * 
     * @return the list of actors' roles in this movie
     */
    public List<Actor> getActorsRoles() {
        return actorsRoles;
    }

    /**
     * Sets the release year of the movie.
     * 
     * @param year the release year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Sets the release year of the movie.
     * 
     * @param year the release year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Gets the list of directors associated with this movie.
     * 
     * @return the list of directors for this movie
     */
    public List<Director> getDirectors() {
        return directors;
    }

    /**
     * Sets the list of directors associated with this movie.
     * 
     * @param directors the list of directors to set
     */
    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    /**
     * Gets the runtime of the movie in minutes.
     * 
     * @return the runtime of the movie
     */
    public Integer getRuntime() {
        return runtime;
    }

    /**
     * Sets the runtime of the movie in minutes.
     * 
     * @param runtime the runtime to set
     */
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    /**
     * Gets the path to the movie's backdrop image.
     * 
     * @return the backdrop path
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     * Sets the path to the movie's backdrop image.
     * 
     * @param backdropPath the backdrop path to set
     */
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /**
     * Gets the release date of the movie.
     * 
     * @return the release date of the movie
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the release date of the movie.
     * 
     * @param releaseDate the release date to set
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Calculates the hash code for this movie based on its unique identifier.
     * 
     * @return the hash code for this movie
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compares this movie with another object for equality.
     * Two movies are considered equal if they have the same unique identifier.
     * 
     * @param obj the object to compare with
     * @return {@code true} if this movie is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Movie other = (Movie) obj;
        return Objects.equals(id, other.id);
    }
}