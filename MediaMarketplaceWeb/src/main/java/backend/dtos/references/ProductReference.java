package backend.dtos.references;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for referencing a product.
 */
public class ProductReference {

    /**
     * The unique identifier for the product.
     * This field must not be blank.
     */
    @NotBlank
    private Long id;

    /**
     * The unique identifier for the movie associated with the product.
     * This field must not be blank.
     */
    @NotBlank
    private Long movieId;

    /**
     * The price at which the product can be purchased.
     */
    private BigDecimal buyPrice;

    /**
     * The price at which the product can be rented.
     */
    private BigDecimal rentPrice;

    /**
     * The discount applicable to the purchase price of the product.
     */
    private BigDecimal buyDiscount;

    /**
     * The discount applicable to the rental price of the product.
     */
    private BigDecimal rentDiscount;
    
    private String currencyCode;

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
     * Gets the unique identifier for the movie associated with the product.
     * 
     * @return the unique identifier of the associated movie
     */
    public Long getMovieId() {
        return movieId;
    }

    /**
     * Sets the unique identifier for the movie associated with the product.
     * 
     * @param movieId the unique identifier of the associated movie
     */
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    /**
     * Gets the price at which the product can be purchased.
     * 
     * @return the purchase price of the product
     */
    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    /**
     * Sets the price at which the product can be purchased.
     * 
     * @param buyPrice the purchase price of the product
     */
    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    /**
     * Gets the price at which the product can be rented.
     * 
     * @return the rental price of the product
     */
    public BigDecimal getRentPrice() {
        return rentPrice;
    }

    /**
     * Sets the price at which the product can be rented.
     * 
     * @param rentPrice the rental price of the product
     */
    public void setRentPrice(BigDecimal rentPrice) {
        this.rentPrice = rentPrice;
    }

    /**
     * Gets the discount applicable to the purchase price of the product.
     * 
     * @return the purchase discount of the product
     */
    public BigDecimal getBuyDiscount() {
        return buyDiscount;
    }

    /**
     * Sets the discount applicable to the purchase price of the product.
     * 
     * @param buyDiscount the purchase discount of the product
     */
    public void setBuyDiscount(BigDecimal buyDiscount) {
        this.buyDiscount = buyDiscount;
    }

    /**
     * Gets the discount applicable to the rental price of the product.
     * 
     * @return the rental discount of the product
     */
    public BigDecimal getRentDiscount() {
        return rentDiscount;
    }

    /**
     * Sets the discount applicable to the rental price of the product.
     * 
     * @param rentDiscount the rental discount of the product
     */
    public void setRentDiscount(BigDecimal rentDiscount) {
        this.rentDiscount = rentDiscount;
    }

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
}