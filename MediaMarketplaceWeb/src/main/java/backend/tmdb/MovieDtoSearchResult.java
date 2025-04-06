package backend.tmdb;

import java.util.List;

import backend.dtos.CreateMovieDto;

/**
 * Represents the result of a movie search operation, including the search text,
 * a list of matching movies, and pagination information.
 */
public class MovieDtoSearchResult {

    /**
     * The text used in the search query.
     */
    private String searchText;

    /**
     * The list of movies that match the search criteria.
     */
    private List<CreateMovieDto> resultList;

    /**
     * The current page number in the pagination.
     */
    private int currentPage;

    /**
     * The total number of pages available based on the search result.
     */
    private int totalPages;

    /**
     * Gets the list of movies that match the search criteria.
     *
     * @return the list of matching movies
     */
    public List<CreateMovieDto> getResultList() {
        return resultList;
    }

    /**
     * Sets the list of movies that match the search criteria.
     *
     * @param resultList the list of matching movies to set
     */
    public void setResultList(List<CreateMovieDto> resultList) {
        this.resultList = resultList;
    }

    /**
     * Gets the current page number in the pagination.
     *
     * @return the current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Sets the current page number in the pagination.
     *
     * @param currentPage the current page number to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Gets the total number of pages available based on the search result.
     *
     * @return the total number of pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the total number of pages available based on the search result.
     *
     * @param totalPages the total number of pages to set
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Gets the text used in the search query.
     *
     * @return the search text
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Sets the text used in the search query.
     *
     * @param searchText the search text to set
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}