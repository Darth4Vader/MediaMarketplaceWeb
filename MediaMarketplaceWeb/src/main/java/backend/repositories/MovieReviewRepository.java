package backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Movie;
import backend.entities.MovieReview;
import backend.entities.User;

/**
 * Repository interface for managing {@link MovieReview} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link MovieReview} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link MovieReview}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes custom methods to retrieve {@link MovieReview}
 * entities based on various attributes such as the associated movie and user.</p>
 */
@Repository
public interface MovieReviewRepository extends JpaRepository<MovieReview, Long> {

    /**
     * Finds all {@link MovieReview} entities associated with a specific {@link Movie}.
     * 
     * @param movie the {@link Movie} for which to find reviews
     * @return an {@link Optional} containing a list of {@link MovieReview} entities, or {@link Optional#empty()} if no reviews are found
     */
    Optional<List<MovieReview>> findAllByMovie(Movie movie);

    /**
     * Finds all {@link MovieReview} entities associated with a specific movie ID.
     * 
     * @param movieId the ID of the movie for which to find reviews
     * @return an {@link Optional} containing a list of {@link MovieReview} entities, or {@link Optional#empty()} if no reviews are found
     */
    Optional<List<MovieReview>> findAllByMovieId(Long movieId);

    /**
     * Finds a {@link MovieReview} for a specific movie ID and user ID.
     * 
     * @param movieId the ID of the movie
     * @param userId the ID of the user
     * @return an {@link Optional} containing the {@link MovieReview}, or {@link Optional#empty()} if no review is found
     */
    Optional<MovieReview> findByMovieIdAndUserId(Long movieId, Long userId);

    /**
     * Finds a {@link MovieReview} for a specific {@link Movie} and {@link User}.
     * 
     * @param movie the {@link Movie} for which to find the review
     * @param user the {@link User} who wrote the review
     * @return an {@link Optional} containing the {@link MovieReview}, or {@link Optional#empty()} if no review is found
     */
    Optional<MovieReview> findByMovieAndUser(Movie movie, User user);
    
    Optional<Page<MovieReview>> findByMovie(Movie movie, Pageable pageable);
}