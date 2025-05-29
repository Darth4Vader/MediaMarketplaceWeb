package backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import backend.entities.Cart;
import backend.entities.CartProduct;

/**
 * Repository interface for managing {@link CartProduct} entities.
 * 
 * This repository is part of the data manipulation layer of the Spring application,
 * where we perform operations such as saving, deleting, and modifying {@link CartProduct} entities.
 * It extends {@link JpaRepository}, which provides basic CRUD operations for {@link CartProduct}.
 */
@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long>, JpaSpecificationExecutor<CartProduct> {

	
	Optional<Page<CartProduct>> findByCart(Cart cart, Pageable pageable);
}
