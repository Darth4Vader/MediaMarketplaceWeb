package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.MoviePurchasedDto;
import backend.dtos.orders.UserActiveMoviePurchaseInfo;
import backend.dtos.references.MovieReference;
import backend.exceptions.EntityNotFoundException;
import backend.services.MoviePurchasedService;

/**
 * REST controller for managing purchased movies.
 * <p>
 * This controller provides endpoints for retrieving information about movies that users have purchased.
 * It includes methods for retrieving all active media products purchased by the user and for getting details
 * about active purchases of a specific movie.
 * </p>
 */
@RestController
@RequestMapping("/users/current/movie-purchased")
public class MoviePurchasedController {

    @Autowired
    private MoviePurchasedService moviePurchasedService;

    /**
     * Retrieves all active media products purchased by the current user.
     * <p>
     * This endpoint returns a list of all movies that the current user has purchased and that are still active.
     * A movie is considered active if it is rented and the rental period has not expired, or if it is owned by the user.
     * </p>
     *
     * @return A list of {@link MovieReference} objects representing active movies purchased by the user.
     */
    @GetMapping("/actives")
    public Page<MovieReference> getAllActiveMediaProductsOfUser(Pageable pageable) {
    	return moviePurchasedService.getAllActiveMoviesOfUser(pageable);
    }

    /**
     * Retrieves a list of active purchases for a specific movie by the current user.
     * <p>
     * This endpoint returns a list of active purchases for the movie identified by the given ID. An active purchase
     * is one where the rental period has not expired or if the movie is owned by the user. If the movie with the specified
     * ID does not exist or if the user has not purchased the movie, an {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @param movieId The ID of the movie for which to retrieve active purchases.
     * @return A list of {@link MoviePurchasedDto} objects representing active purchases of the specified movie.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist or if the user has not purchased the movie.
     */
    @GetMapping("/actives/{movieId}")
    public UserActiveMoviePurchaseInfo getActiveListUserMovie(@PathVariable("movieId") Long movieId) throws EntityNotFoundException {
        return moviePurchasedService.getActiveListUserMovie(movieId);
    }
    
    /**
     * Checks if the current user can watch the specified movie.
     * <p>
     * This endpoint determines if the current user has the right to watch the movie identified by the given ID.
     * It first checks if the user is an admin, as admins can access all movies. If the user is not an admin, the method
     * verifies if the user has an active purchase of the movie. A purchase is considered active if the movie is rented
     * and the rental period has not expired, or if the movie is owned by the user.
     * </p>
     *
     * @param movieId The ID of the movie to check for viewing permission.
     * @return {@code true} if the current user is an admin or has an active purchase of the movie; {@code false} otherwise.
     * @throws EntityNotFoundException If the movie with the specified ID does not exist or if the user has not purchased the movie.
     */
    @GetMapping("/is-active/{movieId}")
    public boolean checkIfCanWatchMovie(@PathVariable Long movieId) throws EntityNotFoundException {
        return moviePurchasedService.checkIfCanWatchMovie(movieId);
    }
}