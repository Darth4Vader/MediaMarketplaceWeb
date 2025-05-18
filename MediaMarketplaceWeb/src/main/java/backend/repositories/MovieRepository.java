package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Movie;

/**
 * Repository interface for managing {@link Movie} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link Movie} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Movie}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes custom methods to retrieve {@link Movie}
 * entities based on specific attributes like name and media ID.</p>
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    /**
     * Finds a {@link Movie} by its name.
     * 
     * @param name the name of the movie
     * @return an {@link Optional} containing the found {@link Movie}, or {@link Optional#empty()} if no movie is found
     */
    Optional<Movie> findByName(String name);

    /**
     * Finds a {@link Movie} by its media ID.
     * 
     * @param mediaID the media ID of the movie
     * @return an {@link Optional} containing the found {@link Movie}, or {@link Optional#empty()} if no movie is found
     */
    Optional<Movie> findByMediaID(@Param("media_id") String mediaID);
}
