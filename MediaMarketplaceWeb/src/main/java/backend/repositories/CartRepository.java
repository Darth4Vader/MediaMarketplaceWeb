package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.entities.Cart;
import backend.entities.User;

/**
 * Repository interface for managing {@link Cart} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * where we perform operations such as saving, deleting, and modifying {@link Cart} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Cart}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes a method to find a cart by the associated user.</p>
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a {@link Cart} by the associated {@link User}.
     * 
     * @param user the {@link User} whose cart is to be found
     * @return an {@link Optional} containing the found {@link Cart}, or {@link Optional#empty()} if no cart is found
     */
    Optional<Cart> findByUser(@Param("user_id") User user);
}