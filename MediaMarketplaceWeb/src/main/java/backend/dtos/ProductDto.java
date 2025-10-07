package backend.dtos;

import backend.dtos.general.PriceDto;
import backend.dtos.references.MovieReference;

/**
 * Data Transfer Object for representing a product.
 */
public class ProductDto {

    /**
     * The unique identifier for the product.
     */
    private Long id;

    /**
     * The reference to the movie associated with the product.
     */
    private MovieReference movie;

    /**
     * The final purchase price of the product.
     */
    private PriceDto finalBuyPrice;

    /**
     * The final rental price of the product.
     */
    private PriceDto finalRentPrice;

    /**
     * Gets the unique identifier for the product.
     * 
     * @return the unique identifier of the product
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the product.
     * 
     * @param id the unique identifier of the product
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the reference to the movie associated with the product.
     * 
     * @return the movie reference
     */
    public MovieReference getMovie() {
        return movie;
    }

    /**
     * Sets the reference to the movie associated with the product.
     * 
     * @param movie the movie reference
     */
    public void setMovie(MovieReference movie) {
        this.movie = movie;
    }

    /**
     * Gets the final purchase price of the product.
     * 
     * @return the final purchase price
     */
    public PriceDto getFinalBuyPrice() {
        return finalBuyPrice;
    }

    /**
     * Sets the final purchase price of the product.
     * 
     * @param finalBuyPrice the final purchase price
     */
    public void setFinalBuyPrice(PriceDto finalBuyPrice) {
        this.finalBuyPrice = finalBuyPrice;
    }

    /**
     * Gets the final rental price of the product.
     * 
     * @return the final rental price
     */
    public PriceDto getFinalRentPrice() {
        return finalRentPrice;
    }

    /**
     * Sets the final rental price of the product.
     * 
     * @param finalRentPrice the final rental price
     */
    public void setFinalRentPrice(PriceDto finalRentPrice) {
        this.finalRentPrice = finalRentPrice;
    }
}