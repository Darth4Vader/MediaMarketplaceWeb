package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.CartDto;
import backend.dtos.carts.UpdatedCartProductDto;
import backend.dtos.references.CartProductReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.exceptions.EntityUnprocessableException;
import backend.services.CartService;

/**
 * REST controller for managing the cart in the system.
 * <p>
 * This controller provides endpoints for retrieving the cart, adding products to the cart, and removing products from the cart.
 * </p>
 */
@RestController
@RequestMapping("api/users/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Retrieves the current cart.
     * <p>
     * This method fetches the details of the current cart.
     * </p>
     * 
     * @return A {@link CartDto} object representing the current cart.
     * @throws EntityNotFoundException If the cart is not found.
     */
    @GetMapping("")
    public CartDto getCart(Pageable pageable) throws EntityNotFoundException {
        return cartService.getCart(pageable);
    }

    /**
     * Adds a product to the cart.
     * <p>
     * This endpoint adds a product to the cart by providing the product details. If there is an issue with adding the product,
     * such as a database constraint violation, an {@link EntityAdditionException} will be thrown.
     * </p>
     * 
     * @param dto The {@link CartProductReference} containing the details of the product to be added.
     * @return A {@link ResponseEntity} with a success message if the product is added successfully.
     * @throws EntityNotFoundException If the product or cart is not found.
     * @throws EntityAlreadyExistsException If the product already exists in the cart.
     * @throws EntityUnprocessableException 
     * @throws EntityAdditionException If there is a problem adding the product due to data access issues.
     */
    @PostMapping("/")
    public ResponseEntity<?> addProductToCart(@RequestBody CartProductReference dto) 
            throws EntityNotFoundException, EntityAlreadyExistsException, EntityUnprocessableException {
        try {
            cartService.addProductToCart(dto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add the product \"" + dto.getProductId() + "\" to the cart", e);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Removes a product from the cart.
     * <p>
     * This endpoint removes a product from the cart by providing the product details. If there is an issue with removing the product,
     * such as a database constraint violation, an {@link EntityRemovalException} will be thrown.
     * </p>
     * 
     * @param dto The {@link CartProductReference} containing the details of the product to be removed.
     * @return A {@link ResponseEntity} with a success message if the product is removed successfully.
     * @throws EntityNotFoundException If the product or cart is not found.
     * @throws EntityRemovalException If there is a problem removing the product due to data access issues.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeProductFromCart(@NonNull @PathVariable("productId") Long productId) 
            throws EntityNotFoundException {
        try {
            cartService.removeProductFromCart(productId);
        } catch (DataAccessException e) {
            throw new EntityRemovalException("Unable to remove the product \"" + productId + "\" from the cart", e);
        }
        return new ResponseEntity<>("Removed Successfully", HttpStatus.OK);
    }
    
    @PutMapping("/{productId}")
    public UpdatedCartProductDto updateCartProduct(@NonNull @PathVariable("productId") Long productId, @RequestBody CartProductReference dto) 
            throws EntityNotFoundException, EntityUnprocessableException {
        try {
            return cartService.updateCartProduct(productId, dto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to update the product \"" + productId + "\" in the cart", e);
        }
    }
}