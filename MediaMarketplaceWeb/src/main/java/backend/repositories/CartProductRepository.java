package backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.CartProduct;

/**
 * Repository interface for managing {@link CartProduct} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * where we perform operations such as saving, deleting, and modifying {@link CartProduct} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link CartProduct}.
 */
@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
	
}
