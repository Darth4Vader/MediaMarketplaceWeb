package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import backend.entities.Genre;

/**
 * Repository interface for managing {@link Genre} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link Genre} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Genre}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes a method to find a genre
 * by its name.</p>
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long>, JpaSpecificationExecutor<Genre> {

    /**
     * Finds a {@link Genre} by its name.
     * 
     * @param name the name of the genre
     * @return an {@link Optional} containing the found {@link Genre}, or {@link Optional#empty()} if no genre is found with the given name
     */
    Optional<Genre> findByName(String name);
}