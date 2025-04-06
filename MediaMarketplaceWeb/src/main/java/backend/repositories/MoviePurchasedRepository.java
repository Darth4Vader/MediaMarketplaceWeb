package backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import backend.entities.User;

/**
 * Repository interface for managing {@link MoviePurchased} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link MoviePurchased} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link MoviePurchased}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes custom methods to retrieve {@link MoviePurchased}
 * entities based on related {@link Order} and {@link User} entities.</p>
 */
@Repository
public interface MoviePurchasedRepository extends JpaRepository<MoviePurchased, Long> {

    /**
     * Finds a list of {@link MoviePurchased} entities by the given {@link Order}.
     * 
     * @param order the {@link Order} for which to find {@link MoviePurchased} entities
     * @return a list of {@link MoviePurchased} entities associated with the given order
     */
    List<MoviePurchased> findByOrder(Order order);

    /**
     * Finds a list of {@link MoviePurchased} entities by the given {@link User}.
     * 
     * @param user the {@link User} for which to find {@link MoviePurchased} entities
     * @return a list of {@link MoviePurchased} entities associated with the given user
     */
    List<MoviePurchased> findByOrderUser(User user);

    /**
     * Finds a list of {@link MoviePurchased} entities by the given {@link User} and {@link Movie}.
     * 
     * @param user the {@link User} for which to find {@link MoviePurchased} entities
     * @param movie the {@link Movie} for which to find {@link MoviePurchased} entities
     * @return an {@link Optional} containing a list of {@link MoviePurchased} entities associated with the given user and movie,
     *         or {@link Optional#empty()} if no entities are found
     */
    Optional<List<MoviePurchased>> findAllByOrderUserAndMovie(User user, Movie movie);
}