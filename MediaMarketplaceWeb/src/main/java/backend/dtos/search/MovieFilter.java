package backend.dtos.search;

import java.util.List;

public class MovieFilter /*extends PageableDto*/ {
	
    /**
     * The name of the movie to search for.
     */
    private String name;

    /**
     * The list of genres to filter by.
     */
    private List<String> genres;

    /**
     * The minimum year for filtering movies.
     */
    private Integer yearAbove;

    /**
     * The maximum year for filtering movies.
     */
    private Integer yearBelow;

    /**
     * The minimum rating for filtering movies.
     */
    private Double ratingAbove;

    /**
     * The maximum rating for filtering movies.
     */
    private Double ratingBelow;

    /**
     * Default constructor.
     */
	public MovieFilter() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Constructor with movie name.
     * 
     * @param name the name of the movie to search for.
     */
    public MovieFilter(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the movie to search for.
     * 
     * @return the name of the movie.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of genres to filter by.
     * 
     * @return the list of genres.
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * Gets the minimum year for filtering movies.
     * 
     * @return the minimum year.
     */
    public Integer getYearAbove() {
        return yearAbove;
    }

    /**
     * Gets the maximum year for filtering movies.
     * 
     * @return the maximum year.
     */
    public Integer getYearBelow() {
        return yearBelow;
    }

    /**
     * Gets the minimum rating for filtering movies.
     * 
     * @return the minimum rating.
     */
    public Double getRatingAbove() {
        return ratingAbove;
    }

    /**
     * Gets the maximum rating for filtering movies.
     * 
     * @return the maximum rating.
     */
    public Double getRatingBelow() {
        return ratingBelow;
    }

    /**
     * Sets the name of the movie to search for.
     * 
     * @param name the name of the movie.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the list of genres to filter by.
     * 
     * @param genres the list of genres.
     */
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    /**
     * Sets the minimum year for filtering movies.
     * 
     * @param yearAbove the minimum year.
     */
    public void setYearAbove(Integer yearAbove) {
        this.yearAbove = yearAbove;
    }

    /**
     * Sets the maximum year for filtering movies.
     * 
     * @param yearBelow the maximum year.
     */
    public void setYearBelow(Integer yearBelow) {
        this.yearBelow = yearBelow;
    }

    /**
     * Sets the minimum rating for filtering movies.
     * 
     * @param ratingAbove the minimum rating.
     */
    public void setRatingAbove(Double ratingAbove) {
        this.ratingAbove = ratingAbove;
    }

    /**
     * Sets the maximum rating for filtering movies.
     * 
     * @param ratingBelow the maximum rating.
     */
    public void setRatingBelow(Double ratingBelow) {
        this.ratingBelow = ratingBelow;
    }

}
