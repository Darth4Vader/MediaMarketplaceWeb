package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Person;

/**
 * Repository interface for managing {@link Person} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link Person} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Person}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes a custom method to retrieve a {@link Person}
 * entity based on their IMDB ID.</p>
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Finds a {@link Person} entity by their Media TMDB ID.
     * 
     * @param personImdbID the Media TMDB ID of the person
     * @return an {@link Optional} containing the found {@link Person} entity, or {@link Optional#empty()} if no person is found
     */
    Optional<Person> findByMediaId(@Param("media_id") String mediaId);
}
