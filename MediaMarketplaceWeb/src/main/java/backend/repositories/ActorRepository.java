package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Actor;

/**
 * Repository interface for managing {@link Actor} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * where we perform operations such as saving, deleting, and modifying {@link Actor} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations, and includes
 * additional methods to retrieve {@link Actor} entities based on specific criteria.
 */
@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    /**
     * Finds an {@link Actor} by movie ID and person ID.
     *
     * @param movieId the ID of the movie
     * @param personId the ID of the person
     * @return an {@link Optional} containing the found {@link Actor}, or {@link Optional#empty()} if no actor is found
     */
    Optional<Actor> findByMovieIdAndPersonId(Long movieId, Long personId);
}