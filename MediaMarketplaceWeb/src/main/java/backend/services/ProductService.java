package backend.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dtos.ProductDto;
import backend.dtos.references.ProductReference;
import backend.entities.CurrencyKind;
import backend.entities.Movie;
import backend.entities.Product;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ProductRepository;

/**
 * Service class for managing product entities related to movies.
 * <p>
 * This class handles the business logic for products, including operations to retrieve, add, and update
 * product records. It also provides methods to convert between Product entities and ProductDto/Reference objects.
 * </p>
 *  <p>
 * It acts as an intermediary between the data access layer (repositories) and
 * the presentation layer (controllers), handling business logic operations related to products.
 * </p>
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private CurrencyService currencyService;

    /**
     * Retrieves a list of all products in the database.
     * <p>
     * This method returns a list of ProductDto objects representing all products.
     * </p>
     * 
     * @return A list of {@link ProductDto} objects representing all products in the database.
     */
    public List<ProductDto> getAllProducts() {
        // Load the products
        List<Product> products = productRepository.findAll();
        // Convert them to DTOs
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = convertProductToDto(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    /**
     * Adds a new product to the database.
     * <p>
     * This method is restricted to admin users and adds a new Product entity based on the provided ProductReference.
     * If the movie associated with the product does not exist, an {@link EntityNotFoundException} is thrown.
     * </p>
     * 
     * @param productReference The {@link ProductReference} containing information about the product to be added.
     * @return The ID of the newly created product.
     * @throws EntityNotFoundException if the movie associated with the product does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public Long addProduct(ProductReference productReference) throws EntityNotFoundException {
        Movie movie = movieService.getMovieByID(productReference.getMovieId());
        Product product = getProductFromDto(productReference, movie);
        
        // Set the product currency to the user's current currency
        CurrencyKind userCurrency = currencyService.getCurrentUserPreferredCurrency();
        product.setCurrency(userCurrency);
        
        Product resultProduct = productRepository.save(product);
        return resultProduct.getId();
    }

    /**
     * Updates an existing product in the database.
     * <p>
     * This method is restricted to admin users and updates the Product entity with the information provided in
     * the ProductReference. If the product does not exist, an {@link EntityNotFoundException} is thrown.
     * </p>
     * 
     * @param productReference The {@link ProductReference} containing updated information about the product.
     * @throws EntityNotFoundException if the product to be updated does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void updateProduct(ProductReference productReference) throws EntityNotFoundException {
        Product product = getProductByID(productReference.getId());
        updateProductFromReference(product, productReference);
        
        // Update currency as well on price update
        CurrencyKind userCurrency = currencyService.getCurrentUserPreferredCurrency();
        product.setCurrency(userCurrency);
        
        productRepository.save(product);
    }
    
    /**
     * Removes an existing product from the database.
     * <p>
     * This method is restricted to admin users and deletes the Product entity identified by the provided product ID.
     * If the product does not exist, an {@link EntityNotFoundException} is thrown.
     * </p>
     * 
     * @param productId The ID of the product to be removed.
     * @throws EntityNotFoundException if the product to be removed does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void removeProduct(Long productId) throws EntityNotFoundException {
        // Retrieve the product entity from the database using the provided product ID.
        Product product = getProductByID(productId);
        // Delete the product entity from the database.
        productRepository.delete(product);
    }
    
    /**
     * Retrieves a product associated with a specific movie.
     * 
     * @param movieId The ID of the movie for which the product is to be retrieved.
     * @return The {@link ProductDto} representing the product associated with the specified movie.
     * @throws EntityNotFoundException if no product exists for the specified movie.
     */
    public ProductDto getProductOfMovie(Long movieId) throws EntityNotFoundException {
        return convertProductToDto(getProductByMovieId(movieId));
    }
    
    /**
     * Retrieves a product associated with a specific movie.
     * 
     * @param movieId The ID of the movie for which the product is to be retrieved.
     * @return The {@link ProductDto} representing the product associated with the specified movie.
     * @throws EntityNotFoundException if no product exists for the specified movie.
     */
    public ProductDto getProduct(Long productId) throws EntityNotFoundException {
        return convertProductToDto(getProductByID(productId));
    }
    
    /**
     * Retrieves a product reference associated with a specific movie.
     * 
     * @param movieId The ID of the movie for which the product reference is to be retrieved.
     * @return The {@link ProductReference} representing the product associated with the specified movie.
     * @throws EntityNotFoundException if no product exists for the specified movie.
     */
    public ProductReference getProductReferenceOfMovie(Long movieId) throws EntityNotFoundException {
        return convertProductToReference(getProductByMovieId(movieId));
    }
    
    /**
     * Retrieves a product by its ID.
     * 
     * @param id The ID of the product to retrieve.
     * @return The {@link Product} entity with the specified ID.
     * @throws EntityNotFoundException if the product with the specified ID does not exist.
     */
    public Product getProductByID(Long id) throws EntityNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The Product with ID: \"" + id + "\" does not exist"));
    }
    
    /**
     * Retrieves a product associated with a specific movie by its movie ID.
     * 
     * @param movieId The ID of the movie for which the product is to be retrieved.
     * @return The {@link Product} entity associated with the specified movie.
     * @throws EntityNotFoundException if no product exists for the specified movie.
     */
    private Product getProductByMovieId(Long movieId) throws EntityNotFoundException {
        return productRepository.findByMovieId(movieId)
                .orElseThrow(() -> new EntityNotFoundException("The Movie with ID: \"" + movieId + "\" does not have a product"));
    }
    
    /**
     * Creates a Product entity from the provided ProductReference and associated Movie.
     * 
     * @param productDto The {@link ProductReference} containing product details.
     * @param movie The {@link Movie} associated with the product.
     * @return A {@link Product} entity populated with the provided details.
     */
    public Product getProductFromDto(ProductReference productDto, Movie movie) {
        Product product = new Product();
        product.setMovie(movie);
        updateProductFromReference(product, productDto);
        return product;
    }
    
    /**
     * Updates a Product entity with details from a ProductReference.
     * 
     * @param product The {@link Product} entity to update.
     * @param productDto The {@link ProductReference} containing updated details.
     */
    public static void updateProductFromReference(Product product, ProductReference productDto) {
        product.setBuyPrice(productDto.getBuyPrice());
        product.setRentPrice(productDto.getRentPrice());
        product.setBuyDiscount(productDto.getBuyDiscount());
        product.setRentDiscount(productDto.getRentDiscount());
    }

    /**
     * Converts a Product entity to a ProductReference.
     * 
     * @param product The {@link Product} entity to convert.
     * @return A {@link ProductReference} containing the details of the product.
     */
    public static ProductReference convertProductToReference(Product product) {
        ProductReference productReference = new ProductReference();
        productReference.setId(product.getId());
        productReference.setBuyPrice(product.getBuyPrice());
        productReference.setRentPrice(product.getRentPrice());
        productReference.setBuyDiscount(product.getBuyDiscount());
        productReference.setRentDiscount(product.getRentDiscount());
        return productReference;
    }

    /**
     * Converts a Product entity to a ProductDto.
     * 
     * @param product The {@link Product} entity to convert.
     * @return A {@link ProductDto} containing the details of the product.
     */
    public ProductDto convertProductToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setMovie(movieService.convertMovieToReference(product.getMovie()));
        productDto.setFinalBuyPrice(calculateBuyPrice(product));
        productDto.setFinalRentPrice(calculateRentPrice(product));
        return productDto;
    }

    /**
     * Calculates the final buy price of a product after applying the discount.
     * 
     * @param product The {@link Product} entity for which the buy price is calculated.
     * @return The final buy price of the product.
     */
    public static double calculateBuyPrice(Product product) {
        return calculatePrice(product.getBuyPrice(), product.getBuyDiscount());
    }

    /**
     * Calculates the final rent price of a product after applying the discount.
     * 
     * @param product The {@link Product} entity for which the rent price is calculated.
     * @return The final rent price of the product.
     */
    public static double calculateRentPrice(Product product) {
        return calculatePrice(product.getRentPrice(), product.getRentDiscount());
    }
    
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    /**
     * Calculates the final price after applying the discount.
     * 
     * @param price The original price of the product.
     * @param discount The discount to be applied.
     * @return The final price after applying the discount.
     */
    private static double calculatePrice(double price, BigDecimal discount) {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) == 0) {
            return price;
        }
        return ONE_HUNDRED.subtract(discount)
                .divide(ONE_HUNDRED)
                .multiply(new BigDecimal(price, new MathContext(2, RoundingMode.HALF_EVEN)))
                .doubleValue();
    }
}