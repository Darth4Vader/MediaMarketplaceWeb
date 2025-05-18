package backend.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.DataUtils;
import backend.dtos.MoviePurchasedDto;
import backend.dtos.orders.UserActiveMoviePurchaseInfo;
import backend.dtos.references.MovieReference;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MoviePurchasedRepository;
import backend.utils.TimezoneUtils;

/**
 * Service class for managing purchased movies.
 * <p>
 * This class provides methods to retrieve information about movies purchased by users,
 * including checking the status of rentals and converting entities to DTOs.
 * </p>
 * <p>
 * It handles the business logic operations related to purchased movies and acts as an intermediary 
 * between the data access layer (repositories) and the presentation layer (controllers).
 * </p>
 */
@Service
public class MoviePurchasedService {

    @Autowired
    private MoviePurchasedRepository moviePurchasedRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private UserAuthenticateService userAuthenticateService;

    /**
     * Retrieves a list of all active movies purchased by the current user.
     * <p>
     * A movie is considered active if it is rented and the rental period has not expired, 
     * or if it is bought (owned by the user).
     * </p>
     *
     * @return A list of MovieReference objects representing active movies.
     */
    public List<MovieReference> getAllActiveMoviesOfUser() {
        // First load all the movies purchased by the user
        User user = tokenService.getCurretUser();
        List<MoviePurchased> purchasedList = moviePurchasedRepository.findByOrderUser(user);
        // Then convert the active ones to movie references.
        List<MovieReference> movieReferences = new ArrayList<>();
        for (MoviePurchased purchased : purchasedList) {
            // Checks if rent is still active or if it is bought.
            if (DataUtils.isUseable(purchased.isRented(), getCurrentRentTime(purchased))) {
                // Adds the movie if it is not already contained in the list.
                Movie movie = purchased.getMovie();
                MovieReference movieReference = MovieService.convertMovieToReference(movie);
                if (!movieReferences.contains(movieReference)) {
                    movieReferences.add(movieReference);
                }
            }
        }
        return movieReferences;
    }

    /**
     * Retrieves a list of active purchases for a specific movie by the current user.
     * <p>
     * A purchase is considered active if it is rented and the rental period has not expired, 
     * or if it is bought (owned by the user).
     * </p>
     *
     * @param movieId The ID of the movie for which active purchases are to be retrieved.
     * @return A list of MoviePurchasedDto objects representing active purchases of the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist or if the user has never purchased the movie.
     */
    public UserActiveMoviePurchaseInfo getActiveListUserMovie(Long movieId) throws EntityNotFoundException {
        // First load all times the user purchased the given movie
        User user = tokenService.getCurretUser();
        Movie movie = movieService.getMovieByID(movieId);
        List<MoviePurchased> purchasedList = getUserPurchaseListOfMovie(user, movie);
        // Then convert the active purchases to MoviePurchasedDto., and return them.
        List<MoviePurchasedDto> moviePurchasedDtos = new ArrayList<>();
        for (MoviePurchased purchased : purchasedList) {
            if (DataUtils.isUseable(purchased.isRented(), getCurrentRentTime(purchased))) {
                moviePurchasedDtos.add(convertMoviePurchasedtoDto(purchased));
            }
        }
        UserActiveMoviePurchaseInfo userActiveMoviePurchaseInfo = new UserActiveMoviePurchaseInfo();
        userActiveMoviePurchaseInfo.setActivePurchases(moviePurchasedDtos);
        return userActiveMoviePurchaseInfo;
    }
    
    /**
     * Checks if the current user is authorized to watch the specified movie.
     * <p>
     * This method first checks if the current user is an admin. Admins are granted permission to watch all movies,
     * regardless of their purchase status. If the user is not an admin, the method then checks if the user has purchased
     * the specified movie and if the purchase is still active (i.e., if the rental period has not expired or if the movie 
     * is owned by the user).
     * </p>
     *
     * @param movieId The ID of the movie to check for viewing permission.
     * @return {@code true} if the current user is an admin or has an active purchase of the movie; {@code false} otherwise.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist or if the user has not purchased the movie.
     */
    public boolean checkIfCanWatchMovie(Long movieId) throws EntityNotFoundException {
        // First we will check that the movie exists
    	Movie movie = movieService.getMovieByID(movieId);
    	// Now we check if the current user is an admin, because admins can watch all of the movies.
        try {
            userAuthenticateService.checkIfCurrentUserIsAdmin();
            return true;
        } catch (Throwable e) {
            // If not logged in, then not definitively an admin
        }
        
        User user = tokenService.getCurretUser();
        List<MoviePurchased> purchasedList = getUserPurchaseListOfMovie(user, movie);
        
        // Then convert the active purchases to MoviePurchasedDto., and return them.
        for (MoviePurchased purchased : purchasedList) {
            if (DataUtils.isUseable(purchased.isRented(), getCurrentRentTime(purchased))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Converts a MoviePurchased entity to a MoviePurchasedDto.
     *
     * @param moviePurchased The MoviePurchased entity to convert.
     * @return A MoviePurchasedDto object representing the converted entity.
     */
    public static MoviePurchasedDto convertMoviePurchasedtoDto(MoviePurchased moviePurchased) {
        MoviePurchasedDto moviePurchasedDto = new MoviePurchasedDto();
        moviePurchasedDto.setId(moviePurchased.getId());
        moviePurchasedDto.setMovie(MovieService.convertMovieToReference(moviePurchased.getMovie()));
        moviePurchasedDto.setPurchasePrice(moviePurchased.getPurchasePrice());
        boolean isRented = moviePurchased.isRented();
        moviePurchasedDto.setRented(isRented);
        LocalDateTime purchaseDate = moviePurchased.getPurchaseDate();
        moviePurchasedDto.setPurchaseDate(TimezoneUtils.convertToRequestTimezone(purchaseDate));
        Duration rentTime = moviePurchased.getRentTime();
        moviePurchasedDto.setRentTime(rentTime);
        moviePurchasedDto.setRentTimeSincePurchase(TimezoneUtils.convertToRequestTimezone(getCurrentRentTime(isRented, purchaseDate, rentTime)));
        moviePurchasedDto.setUseable(DataUtils.isUseable(moviePurchasedDto));
        return moviePurchasedDto;
    }

    /**
     * Retrieves the list of purchases for a specific movie made by a user.
     *
     * @param user  The user whose purchase list is to be retrieved.
     * @param movie The movie for which purchases are to be retrieved.
     * @return A list of MoviePurchased entities representing the user's purchases of the specified movie.
     * @throws EntityNotFoundException if the user has never purchased the movie.
     */
    private List<MoviePurchased> getUserPurchaseListOfMovie(User user, Movie movie) throws EntityNotFoundException {
        return moviePurchasedRepository.findAllByOrderUserAndMovie(user, movie)
                .filter(e -> !e.isEmpty())
                .orElseThrow(() -> new EntityNotFoundException("The user never purchased the movie"));
    }

    /**
     * Calculates the current rental expiration time for a given MoviePurchased entity.
     * This method delegates the calculation to a helper method that considers the rental status.
     *
     * @param moviePurchased The MoviePurchased entity.
     * @return The expiration time as a LocalDateTime, or null if not rented.
     */
    private static LocalDateTime getCurrentRentTime(MoviePurchased moviePurchased) {
        // Calls the helper method to calculate the current rental expiration time
        return getCurrentRentTime(moviePurchased.isRented(), moviePurchased.getPurchaseDate(), moviePurchased.getRentTime());
    }

    /**
     * Calculates the expiration time of a rental based on rental status and purchase date.
     *
     * @param isRented    Indicates if the movie is currently rented.
     * @param purchaseDate The date of purchase.
     * @param rentTime    The duration of the rental.
     * @return The expiration time as a LocalDateTime or null if not rented.
     */
    private static LocalDateTime getCurrentRentTime(boolean isRented, LocalDateTime purchaseDate, Duration rentTime) {
        if (!isRented) {
            return null;
        }
        return purchaseDate.plusSeconds(rentTime.getSeconds());
    }
}