package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Director;

/**
 * Repository interface for managing {@link Director} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * where we perform operations such as saving, deleting, and modifying {@link Director} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Director}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes a method to find a director
 * by the movie ID and the person's ID.</p>
 */
@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {

    /**
     * Finds a {@link Director} by movie ID and person ID.
     * 
     * @param movieId the ID of the movie
     * @param personId the ID of the person
     * @return an {@link Optional} containing the found {@link Director}, or {@link Optional#empty()} if no director is found
     */
    Optional<Director> findByMovieIdAndPersonId(Long movieId, Long personId);
}