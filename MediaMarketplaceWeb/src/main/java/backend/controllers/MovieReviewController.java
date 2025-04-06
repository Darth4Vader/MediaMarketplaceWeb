package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.MovieReviewDto;
import backend.dtos.references.MovieRatingReference;
import backend.dtos.references.MovieReviewReference;
import backend.exceptions.MovieReviewValuesAreIncorrectException;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.UserNotLoggedInException;
import backend.services.MovieReviewService;

/**
 * REST controller for managing movie reviews and ratings.
 * <p>
 * This controller provides endpoints for retrieving and adding reviews and ratings for movies.
 * </p>
 */
@RestController
@RequestMapping("api/main/movie_reviews")
public class MovieReviewController {

    @Autowired
    private MovieReviewService movieReviewService;

    /**
     * Retrieves all reviews for a specified movie.
     * <p>
     * This endpoint returns a list of reviews for a movie identified by the given ID.
     * If the movie is not found, an {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @param movieId The ID of the movie for which to retrieve reviews.
     * @return A list of {@link MovieReviewDto} objects representing the reviews for the movie.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist.
     */
    @GetMapping("/get_all/{movieId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<MovieReviewDto> getAllReviewOfMovie(@PathVariable Long movieId) throws EntityNotFoundException {
        return movieReviewService.getAllReviewOfMovie(movieId);
    }
    
    /**
     * Retrieves the review of a specific movie by a specific user.
     * <p>
     * This endpoint returns the review made by the user for a movie identified by the given IDs.
     * If the movie or user is not found, an {@link EntityNotFoundException} or {@link UserNotLoggedInException}
     * will be thrown as appropriate.
     * </p>
     *
     * @param movieId The ID of the movie for which to retrieve the review.
     * @return A {@link MovieReviewReference} object representing the review of the movie by the user.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist.
     * @throws UserNotLoggedInException If the user is not logged in.
     */
    @GetMapping("/get/{movieId}/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    public MovieReviewReference getMovieReviewOfUser(@PathVariable Long movieId) throws EntityNotFoundException, UserNotLoggedInException {
        return movieReviewService.getMovieReviewOfUser(movieId);
    }
    
    /**
     * Retrieves the average rating for a specific movie.
     * <p>
     * This endpoint returns the average rating of a movie identified by the given ID.
     * </p>
     *
     * @param movieId The ID of the movie for which to retrieve the average rating.
     * @return The average rating of the movie.
     */
    @GetMapping("/get_ratings/{movieId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Integer getMovieRatings(@PathVariable Long movieId) {
        return movieReviewService.getMovieRatings(movieId);
    }

    /**
     * Adds a review for a specific movie by a specific user.
     * <p>
     * This endpoint allows a user to add a review for a movie identified by the given ID.
     * If the review data is incorrect or the movie is not found, an 
     * {@link MovieReviewValuesAreIncorrectException} or {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @param movieReviewDto The {@link MovieReviewReference} object containing the review details.
     * @throws MovieReviewValuesAreIncorrectException If the review values provided are incorrect.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist.
     */
    @PostMapping("/add_review/{movieId}/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void addMovieReviewOfUser(@RequestBody MovieReviewReference movieReviewDto) throws MovieReviewValuesAreIncorrectException, EntityNotFoundException {
        try {
            movieReviewService.addMovieReviewOfUser(movieReviewDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add user review to the movie \"" + movieReviewDto.getMovieId() + "\"", e);
        }
    }

    /**
     * Adds a rating for a specific movie by a specific user.
     * <p>
     * This endpoint allows a user to add a rating for a movie identified by the given ID.
     * If the rating data is incorrect or the movie is not found, an 
     * {@link MovieReviewValuesAreIncorrectException} or {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @param movieRatingDto The {@link MovieRatingReference} object containing the rating details.
     * @throws MovieReviewValuesAreIncorrectException If the rating values provided are incorrect.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist.
     */
    @PostMapping("/add_ratings/{movieId}/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void addMovieRatingOfUser(@RequestBody MovieRatingReference movieRatingDto) 
            throws MovieReviewValuesAreIncorrectException, EntityNotFoundException {
        try {
            movieReviewService.addMovieRatingOfUser(movieRatingDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add user ratings to the movie \"" + movieRatingDto.getMovieId() + "\"", e);
        }
    }
}