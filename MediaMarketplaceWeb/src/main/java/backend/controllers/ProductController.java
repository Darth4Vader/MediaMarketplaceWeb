package backend.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import backend.dtos.ProductDto;
import backend.dtos.references.ProductReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.ProductService;
import jakarta.validation.Valid;

/**
 * REST controller for managing products.
 * <p>
 * This controller provides endpoints for adding, updating, removing, and retrieving products.
 * </p>
 */
@RestController
@RequestMapping("/main/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Retrieves all products.
     * <p>
     * This endpoint returns a list of all products available in the system.
     * </p>
     *
     * @return A list of {@link ProductDto} objects representing all products.
     */
    @GetMapping("/")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }
    
    /**
     * Retrieves the product associated with a movie.
     * <p>
     * This endpoint returns the {@link ProductDto} for the product associated with the specified movie ID.
     * </p>
     *
     * @param movieId The ID of the movie for which the product details are to be retrieved.
     * @return The {@link ProductDto} object representing the product associated with the movie.
     * @throws EntityNotFoundException If the movie with the provided ID cannot be found.
     */
    @GetMapping("")
    public ProductDto getProductOfMovie(@RequestParam("movieId") Long movieId) throws EntityNotFoundException {
        return productService.getProductOfMovie(movieId);
    }

    /**
     * Adds a new product.
     * <p>
     * This endpoint adds a new product using the provided {@link ProductReference}.
     * If an issue occurs during the transaction, an {@link EntityAdditionException} will be thrown.
     * </p>
     *
     * @param productDto The {@link ProductReference} object containing the details of the product to be added.
     * @return The ID of the newly created product.
     * @throws EntityNotFoundException If the movie associated with the product cannot be found.
     */
    @PostMapping("/")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductReference productDto) throws EntityNotFoundException {
        try {
        	// create the product and get the ID
        	final Long id = productService.addProduct(productDto);
        	// build the URI for the created person location
        	URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(id)
                    .toUri();
        	return ResponseEntity.created(location).build();
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to add the product for the movie with id \"" + productDto.getMovieId() + "\"", e);
        }
    }
    
    @GetMapping("/{id}")
    public ProductDto getProduct(@Valid @PathVariable("id") Long productId) throws EntityNotFoundException {
    	return productService.getProduct(productId);
    }

    /**
     * Updates an existing product.
     * <p>
     * This endpoint updates the details of an existing product using the provided {@link ProductReference}.
     * If an issue occurs during the transaction, an {@link EntityAdditionException} will be thrown.
     * </p>
     *
     * @param productDto The {@link ProductReference} object containing the updated details of the product.
     * @return A {@link ResponseEntity} with a success message and HTTP status 200 (OK).
     * @throws EntityNotFoundException If the product or the movie associated with it cannot be found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody ProductReference productDto) throws EntityNotFoundException {
        try {
            productService.updateProduct(productDto);
        } catch (DataAccessException e) {
            throw new EntityAdditionException("Unable to update the product \"" + productDto.getId() + "\" for the movie with id \"" + productDto.getMovieId() + "\"", e);
        }
        return new ResponseEntity<>("Updated Successfully", HttpStatus.OK);
    }

    /**
     * Removes a product.
     * <p>
     * This endpoint removes a product identified by its ID.
     * If an issue occurs during the transaction, an {@link EntityRemovalException} will be thrown.
     * </p>
     *
     * @param productId The ID of the product to be removed.
     * @return A {@link ResponseEntity} with a success message and HTTP status 200 (OK).
     * @throws EntityNotFoundException If the product with the provided ID cannot be found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeProduct(@Valid @PathVariable("id") Long productId) throws EntityNotFoundException {
        try {
            productService.removeProduct(productId);
        } catch (DataAccessException e) {
            throw new EntityRemovalException("Unable to remove the product \"" + productId + "\"", e);
        }
        return new ResponseEntity<>("Removed Successfully", HttpStatus.OK);
    }
    
    /**
     * Retrieves the product reference associated with a movie.
     * <p>
     * This endpoint returns the {@link ProductReference} for the product associated with the specified movie ID.
     * </p>
     *
     * @param movieId The ID of the movie for which the product reference details are to be retrieved.
     * @return The {@link ProductReference} object representing the product reference associated with the movie.
     * @throws EntityNotFoundException If the movie with the provided ID cannot be found.
     */
    @GetMapping("/references")
    public ProductReference getProductReferenceOfMovie(@RequestParam("movieId") Long movieId) throws EntityNotFoundException {
        return productService.getProductReferenceOfMovie(movieId);
    }
}