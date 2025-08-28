package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.User;

/**
 * Repository interface for managing {@link User} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link User} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link User}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes a custom method to retrieve a {@link User}
 * entity based on the email.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a {@link User} entity by its email.
     * 
     * @param email the email of the user
     * @return an {@link Optional} containing the found {@link User} entity, or {@link Optional#empty()} if no user is found
     */
    Optional<User> findByEmail(@Param("email") String email);
}