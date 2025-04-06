package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Product;

/**
 * Repository interface for managing {@link Product} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link Product} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Product}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes a custom method to retrieve a {@link Product}
 * entity based on the associated movie ID.</p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds a {@link Product} entity by its associated movie ID.
     * 
     * @param movieId the ID of the movie associated with the product
     * @return an {@link Optional} containing the found {@link Product} entity, or {@link Optional#empty()} if no product is found
     */
    Optional<Product> findByMovieId(@Param("movie_id") Long movieId);
}
