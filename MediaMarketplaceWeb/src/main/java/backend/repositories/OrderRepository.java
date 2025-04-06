package backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.Order;
import backend.entities.User;

/**
 * Repository interface for managing {@link Order} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * responsible for performing operations such as saving, deleting, and modifying {@link Order} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link Order}.
 * 
 * <p>In addition to standard CRUD operations, this repository includes custom methods to retrieve {@link Order}
 * entities based on the associated user.</p>
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all {@link Order} entities associated with a specific {@link User}.
     * 
     * @param user the {@link User} for whom to find orders
     * @return an {@link Optional} containing a list of {@link Order} entities, or {@link Optional#empty()} if no orders are found
     */
    Optional<List<Order>> findByUser(User user);
}