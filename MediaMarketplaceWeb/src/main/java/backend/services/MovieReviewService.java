package backend.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.DataUtils;
import backend.dtos.MovieReviewDto;
import backend.dtos.references.MovieRatingReference;
import backend.dtos.references.MovieReviewReference;
import backend.entities.Movie;
import backend.entities.MovieRating;
import backend.entities.MovieReview;
import backend.entities.User;
import backend.exceptions.MovieReviewValuesAreIncorrectException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.enums.MovieReviewTypes;
import backend.repositories.MovieReviewRepository;
import backend.utils.TimezoneUtils;

/**
 * Service class for managing movie reviews.
 * <p>
 * This class provides methods to retrieve, add, and validate reviews and ratings for movies,
 * as well as to convert entities to data transfer objects (DTOs). 
 * </p>
 * <p>
 * It handles the business logic operations related to movie reviews and acts as an intermediary 
 * between the data access layer (repositories) and the presentation layer (controllers).
 * </p>
 */
@Service
public class MovieReviewService {

    @Autowired
    private MovieReviewRepository movieReviewRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TokenService tokenService;

    /**
     * Retrieves all reviews for a specific movie.
     * <p>
     * This method fetches all reviews associated with a particular movie and converts them into
     * {@link MovieReviewDto} objects. If no reviews are found, it throws an {@link EntityNotFoundException}.
     * </p>
     *
     * @param movieId The ID of the movie for which reviews are to be retrieved.
     * @return A list of {@link MovieReviewDto} objects containing reviews for the specified movie.
     * @throws EntityNotFoundException if the movie with the given ID does not exist, 
     *                                  or if no reviews exist for the specified movie.
     */
    public Page<MovieReviewDto> getReviewsOfMovieByPage(Long movieId, Pageable pageable) throws EntityNotFoundException {
        // First load the movie reviews of the given movie.
        Movie movie = movieService.getMovieByID(movieId);
        Page<MovieReview> movieReviewsPage = getMovieReviewOfMovie(movie, pageable);
        // Then convert them to DTOs.
        Page<MovieReviewDto> movieReviewsDtoPage = movieReviewsPage.map(movieReview -> {
        	return convertMovieReviewToDto(movieReview);
		});
        return movieReviewsDtoPage;
    }

    /**
     * Retrieves the review made by the current user for a specific movie.
     * <p>
     * This method fetches the review submitted by the current user for a specific movie. 
     * If the user has not reviewed the movie or if the movie does not exist, an {@link EntityNotFoundException}
     * is thrown.
     * </p>
     *
     * @param movieId The ID of the movie for which the user's review is to be retrieved.
     * @return A {@link MovieReviewReference} object containing the user's review details.
     * @throws EntityNotFoundException if the movie with the given ID does not exist or 
     *                                  if the user has not reviewed the movie.
     */
    public MovieReviewReference getMovieReviewOfUser(Long movieId) throws EntityNotFoundException {
        User user = tokenService.getCurretUser();
        Movie movie = movieService.getMovieByID(movieId);
        return convertMovieReviewToReference(getMovieReviewFromMovieUser(movie, user));
    }

    /**
     * Adds or updates a movie review for the current user.
     * <p>
     * This method adds a new review or updates an existing review for the current user. 
     * It validates the review details and saves the review in the repository. If the movie or user 
     * does not exist, or if the review details are incorrect, appropriate exceptions are thrown.
     * </p>
     *
     * @param movieReviewRef The {@link MovieReviewReference} object containing review details.
     * @throws MovieReviewValuesAreIncorrectException if the review details are invalid.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    @Transactional
    public void addMovieReviewOfUser(MovieReviewReference movieReviewRef) 
            throws MovieReviewValuesAreIncorrectException, EntityNotFoundException {
        // First load the current user to verify that a logged user is trying to add a review.
        User user = tokenService.getCurretUser();
        // Check that the format of the review details is correct, otherwise notify the user of the problem.
        checkForExceptionReviews(movieReviewRef);
        Movie movie = movieService.getMovieByID(movieReviewRef.getMovieId());
        MovieReview movieReview;
        boolean isNewReview = false;
        try {
            // Try to load the current review by the user for the movie.
            movieReview = getMovieReviewFromMovieUser(movie, user);
        } catch (EntityNotFoundException e) {
            // If this is the first time the user is reviewing the movie, create a new MovieReview.
            movieReview = new MovieReview();
            movieReview.setUser(user);
            movieReview.setMovie(movie);
            isNewReview = true;
        }
        // get the user old rating, if he has
        Integer oldRating = movieReview.getRating();
        // Save the review.
        setMovieReviewFromDto(movieReview, movieReviewRef);
        movieReviewRepository.save(movieReview);
        // Update the movie rating based on the new or updated review.
        updateMovieRating(movie, oldRating, movieReview, isNewReview);
    }

    /**
     * Adds or updates a movie rating for the current user.
     * <p>
     * This method adds a new rating or updates an existing rating for the current user. 
     * It validates the rating details and saves the rating in the repository. If the movie or user 
     * does not exist, or if the rating details are incorrect, appropriate exceptions are thrown.
     * </p>
     *
     * @param movieRatingReference The {@link MovieRatingReference} object containing rating details.
     * @throws MovieReviewValuesAreIncorrectException if the rating details are invalid.
     * @throws EntityNotFoundException if the movie with the specified ID does not exist.
     */
    @Transactional
    public void addMovieRatingOfUser(MovieRatingReference movieRatingReference) 
            throws MovieReviewValuesAreIncorrectException, EntityNotFoundException {
        // First load the current user to verify that a logged user is trying to add a rating.
        User user = tokenService.getCurretUser();
        // Check that the format of the rating details is correct, otherwise notify the user of the problem.
        checkForExceptionRatings(movieRatingReference);
        Movie movie = movieService.getMovieByID(movieRatingReference.getMovieId());
        MovieReview movieReview;
        boolean isNewReview = false;
        try {
            // Try to load the current review by the user for the movie.
            movieReview = getMovieReviewFromMovieUser(movie, user);
        } catch (EntityNotFoundException e) {
            // If this is the first time the user is rating the movie, create a new MovieReview.
            movieReview = new MovieReview();
            movieReview.setUser(user);
            movieReview.setMovie(movie);
            isNewReview = true;
        }
        // get the user old rating, if he has
        Integer oldRating = movieReview.getRating();
        // Save the review with the updated rating.
        setMovieRatingFromDto(movieReview, movieRatingReference);
        movieReviewRepository.save(movieReview);
        // Update the movie rating based on the new or updated review.
        updateMovieRating(movie, oldRating, movieReview, isNewReview);
    }
    
    @Transactional
    private void updateMovieRating(Movie movie, Integer userOldRating, MovieReview userMovieReview, boolean isNewReview) {
        MovieRating movieRating = movie.getMovieRating();
        Long count = movieRating.getTotalRatings();
        Double currentAvg = movieRating.getAverageRating();
        Integer newRating = userMovieReview.getRating();
        if(isNewReview) {
        	// If this is the first time the user is rating the movie, set the average rating.
			// Update the total ratings count and average rating.
			double newAvg = (currentAvg * count + newRating) / (count + 1);
			movieRating.setTotalRatings(count + 1);
			movieRating.setAverageRating(newAvg);
        }
        else if(userOldRating != null) {
        	// not the first time the user is rating the movie, update the average rating.
            double newAvg = (currentAvg * count - userOldRating + newRating) / count;
            movieRating.setAverageRating(newAvg);
        }
	}

    /**
     * Calculates the average rating of a specific movie.
     * <p>
     * This method computes the average rating for a movie based on all user ratings. If no ratings are found,
     * it returns null. The average rating is calculated as the total sum of ratings divided by the number of ratings.
     * </p>
     *
     * @param movieId The ID of the movie for which the average rating is to be calculated.
     * @return The average rating of the movie, or null if no ratings exist.
     */
    public Integer getMovieRatings(Long movieId) {
        try {
            // Load the movie and get all its reviews.
            Movie movie = movieService.getMovieByID(movieId);
            Pageable pageable = PageRequest.of(0, 5000);
            Page<MovieReview> page = getMovieReviewOfMovie(movie, pageable);
            // Calculate the average rating.
            double sum = 0;
            sum += page.getContent().stream().mapToDouble(MovieReview::getRating).sum();
            while(page.hasNext()) {
				pageable = page.nextPageable();
				page = getMovieReviewOfMovie(movie, pageable);
				sum += page.getContent().stream().mapToDouble(MovieReview::getRating).sum();
            }
            return (int) (sum / page.getTotalElements());
        } catch (EntityNotFoundException e) {
            // If there are no reviews for the movie, return null.
            return null;
        }
    }

    /**
     * Retrieves all reviews for a specified movie.
     * <p>
     * This method fetches all reviews associated with a given movie entity. 
     * If no reviews are found, it throws an {@link EntityNotFoundException}.
     * </p>
     *
     * @param movie The {@link Movie} entity for which reviews are to be retrieved.
     * @return A list of {@link MovieReview} entities for the specified movie.
     * @throws EntityNotFoundException if no reviews exist for the specified movie.
     */
    private Page<MovieReview> getMovieReviewOfMovie(Movie movie, Pageable pageable) throws EntityNotFoundException {
    	return movieReviewRepository.findByMovie(movie, pageable)
                .orElseThrow(() -> new EntityNotFoundException("There are no reviews of the given movie"));
    }

    /**
     * Retrieves the review made by a specific user for a specific movie.
     * <p>
     * This method fetches the review submitted by a specific user for a given movie entity. 
     * If the user has not reviewed the movie, an {@link EntityNotFoundException} is thrown.
     * </p>
     *
     * @param movie The {@link Movie} entity.
     * @param user The {@link User} entity.
     * @return The {@link MovieReview} entity corresponding to the movie and user.
     * @throws EntityNotFoundException if the user has not reviewed the movie.
     */
    private MovieReview getMovieReviewFromMovieUser(Movie movie, User user) throws EntityNotFoundException {
        return movieReviewRepository.findByMovieAndUser(movie, user)
                .orElseThrow(() -> new EntityNotFoundException("The user did not review the movie"));
    }
    
    /**
     * Sets the rating from a {@link MovieRatingReference} to a {@link MovieReview} entity.
     * 
     * @param movieReview The {@link MovieReview} entity to update.
     * @param movieRatingReference The {@link MovieRatingReference} containing the rating.
     */
    private void setMovieRatingFromDto(MovieReview movieReview, MovieRatingReference movieRatingReference) {
        movieReview.setRating(movieRatingReference.getRating());
    }

    /**
     * Updates a {@link MovieReview} entity based on the values from a {@link MovieReviewReference}.
     * 
     * @param movieReview The {@link MovieReview} entity to update.
     * @param movieReviewRef The {@link MovieReviewReference} containing the new values.
     */
    private void setMovieReviewFromDto(MovieReview movieReview, MovieReviewReference movieReviewRef) {
        setMovieRatingFromDto(movieReview, movieReviewRef);
        movieReview.setReviewTitle(movieReviewRef.getTitle());
        movieReview.setReview(movieReviewRef.getContent());
    }
    
    /**
     * Converts a {@link MovieReview} entity to a {@link MovieReviewDto}.
     * 
     * @param movieReview The {@link MovieReview} entity to convert.
     * @return A {@link MovieReviewDto} object representing the converted entity.
     */
    private MovieReviewDto convertMovieReviewToDto(MovieReview movieReview) {
        MovieReviewDto movieReviewDto = new MovieReviewDto();
        MovieReviewReference movieReviewRef = convertMovieReviewToReference(movieReview);
        movieReviewDto.setMovieReview(movieReviewRef);
        User user = movieReview.getUser();
        movieReviewDto.setUsername(user.getUsername());
        return movieReviewDto;
    }

    /**
     * Converts a {@link MovieReview} entity to a {@link MovieReviewReference}.
     * 
     * @param movieReview The {@link MovieReview} entity to convert.
     * @return A {@link MovieReviewReference} object representing the converted entity.
     */
    private MovieReviewReference convertMovieReviewToReference(MovieReview movieReview) {
        MovieReviewReference movieReviewRef = new MovieReviewReference();
        movieReviewRef.setMovieId(movieReview.getMovie().getId());
        movieReviewRef.setContent(movieReview.getReview());
        movieReviewRef.setCreatedDate(TimezoneUtils.convertToRequestTimezone(movieReview.getCreatedDate()));
        movieReviewRef.setTitle(movieReview.getReviewTitle());
        movieReviewRef.setRating(movieReview.getRating());
        return movieReviewRef;
    }

    /**
     * Validates the rating details in a {@link MovieRatingReference} object.
     * 
     * @param movieRatingReference The {@link MovieRatingReference} containing rating details.
     * @throws MovieReviewValuesAreIncorrectException if the rating details are invalid.
     */
    private void checkForExceptionRatings(MovieRatingReference movieRatingReference) throws MovieReviewValuesAreIncorrectException {
        Map<MovieReviewTypes, String> map = new HashMap<>();
        exceptionMapRatings(movieRatingReference, map);
        if (!map.isEmpty())
            throw new MovieReviewValuesAreIncorrectException(map);
    }

    /**
     * Validates the review details in a {@link MovieReviewReference} object.
     * 
     * @param movieReviewRef The {@link MovieReviewReference} containing review details.
     * @throws MovieReviewValuesAreIncorrectException if the review details are invalid.
     */
    private void checkForExceptionReviews(MovieReviewReference movieReviewRef) throws MovieReviewValuesAreIncorrectException {
        Map<MovieReviewTypes, String> map = new HashMap<>();
        exceptionMapReview(movieReviewRef, map);
        if (!map.isEmpty())
            throw new MovieReviewValuesAreIncorrectException(map);
    }
    
    /** Minimum value for a rating */
    private static final int RATING_MIN = 1;
    /** Maximum value for a rating */
    private static final int RATING_MAX = 100;
    /** Maximum length for a review title */
    private static final int REVIEW_TITLE_MAX_LENGTH = 255;
    /** Maximum length for a review */
    private static final int REVIEW_MAX_LENGTH = 1000;

    /**
     * Populates a map with validation errors related to movie ratings.
     * 
     * @param movieRatingReference The {@link MovieRatingReference} containing rating details.
     * @param map The map to populate with validation error messages.
     */
    private void exceptionMapRatings(MovieRatingReference movieRatingReference, Map<MovieReviewTypes, String> map) {
        Integer rating = movieRatingReference.getRating();
        if (rating == null)
            map.put(MovieReviewTypes.RATING, "Required field, the user must rate the movie");
        else if (rating <= RATING_MIN || rating > RATING_MAX)
            map.put(MovieReviewTypes.RATING, String.format("The rating number must be between %d-%d", RATING_MIN, RATING_MAX));
    }

    /**
     * Populates a map with validation errors related to movie reviews.
     * 
     * @param movieReviewRef The {@link MovieReviewReference} containing review details.
     * @param map The map to populate with validation error messages.
     */
    private void exceptionMapReview(MovieReviewReference movieReviewRef, Map<MovieReviewTypes, String> map) {
        exceptionMapRatings(movieReviewRef, map);
        String reviewTitle = movieReviewRef.getTitle();
        String review = movieReviewRef.getContent();
        if (DataUtils.isBlank(reviewTitle))
            map.put(MovieReviewTypes.TITLE, "Required field, the title must be written");
        else if (reviewTitle.length() > REVIEW_TITLE_MAX_LENGTH)
            map.put(MovieReviewTypes.REVIEW, String.format("The Review Title length must be less than %d", REVIEW_TITLE_MAX_LENGTH));
        if (DataUtils.isNotBlank(review) && review.length() > REVIEW_MAX_LENGTH)
            map.put(MovieReviewTypes.REVIEW, String.format("The Review Content length must be less than %d", REVIEW_MAX_LENGTH));
    }
}