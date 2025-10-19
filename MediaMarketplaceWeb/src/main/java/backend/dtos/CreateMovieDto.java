package backend.dtos;

import java.util.List;

import backend.dtos.movies.KeywordCreateRequest;

/**
 * Data Transfer Object for creating a movie with associated details.
 */
public class CreateMovieDto {

    /**
     * The TMDb (The Movie Database) identifier for the movie.
     */
    private String tmdbID;

    /**
     * The movie details encapsulated in a {@link MovieDto} object.
     */
    private MovieDto movieDto;
    
    private List<KeywordCreateRequest> keywords;

    /**
     * Gets the TMDb ID for the movie.
     * 
     * @return the TMDb ID as a {@link String}
     */
    public String getMediaID() {
        return tmdbID;
    }

    /**
     * Sets the TMDb ID for the movie.
     * 
     * @param tmdbID the TMDb ID to set
     */
    public void setMediaID(String tmdbID) {
        this.tmdbID = tmdbID;
    }

    /**
     * Gets the movie details.
     * 
     * @return the {@link MovieDto} representing the movie details
     */
    public MovieDto getMovieDto() {
        return movieDto;
    }

    /**
     * Sets the movie details.
     * 
     * @param movieDto the {@link MovieDto} to set
     */
    public void setMovieDto(MovieDto movieDto) {
        this.movieDto = movieDto;
    }
    
    public List<KeywordCreateRequest> getKeywords() {
		return keywords;
	}
    
	public void setKeywords(List<KeywordCreateRequest> keywords) {
		this.keywords = keywords;
	}
}