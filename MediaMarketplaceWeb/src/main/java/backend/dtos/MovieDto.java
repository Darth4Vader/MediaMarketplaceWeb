package backend.dtos;

import java.time.LocalDate;
import java.util.List;

import backend.dtos.movies.RuntimeDto;

/**
 * Data Transfer Object for representing a movie.
 */
public class MovieDto {

    /**
     * The unique identifier of the movie.
     */
    private Long id;

    /**
     * The name or title of the movie.
     */
    private String name;

    /**
     * The path to the movie's poster image.
     */
    private String posterPath;

    /**
     * The path to the movie's backdrop image.
     */
    private String backdropPath;

    /**
     * The runtime of the movie in minutes.
     */
    private RuntimeDto runtime;

    /**
     * The list of genres associated with the movie.
     */
    private List<String> genres;

    /**
     * A brief synopsis or summary of the movie.
     */
    private String synopsis;

    /**
     * The release year of the movie.
     */
    private Integer year;

    /**
     * The release date of the movie.
     */
    private LocalDate releaseDate;

    /**
     * Gets the unique identifier of the movie.
     * 
     * @return the unique identifier of the movie
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the movie.
     * 
     * @param id the unique identifier of the movie
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name or title of the movie.
     * 
     * @return the name of the movie
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name or title of the movie.
     * 
     * @param name the name of the movie
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the path to the movie's poster image.
     * 
     * @return the path to the poster image
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * Sets the path to the movie's poster image.
     * 
     * @param posterPath the path to the poster image
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * Gets the path to the movie's backdrop image.
     * 
     * @return the path to the backdrop image
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     * Sets the path to the movie's backdrop image.
     * 
     * @param backdropPath the path to the backdrop image
     */
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /**
     * Gets the runtime of the movie in minutes.
     * 
     * @return the runtime of the movie
     */
    public RuntimeDto getRuntime() {
        return runtime;
    }

    /**
     * Sets the runtime of the movie in minutes.
     * 
     * @param runtime the runtime of the movie
     */
    public void setRuntime(RuntimeDto runtime) {
        this.runtime = runtime;
    }

    /**
     * Gets the list of genres associated with the movie.
     * 
     * @return the list of genres
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * Sets the list of genres associated with the movie.
     * 
     * @param genres the list of genres
     */
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    /**
     * Gets a brief synopsis or summary of the movie.
     * 
     * @return the synopsis of the movie
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * Sets a brief synopsis or summary of the movie.
     * 
     * @param synopsis the synopsis of the movie
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * Gets the release year of the movie.
     * 
     * @return the release year of the movie
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Sets the release year of the movie.
     * 
     * @param year the release year of the movie
     */
    public void setYear(Integer year) {
        this.year = year;
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
     * @param releaseDate the release date of the movie
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}