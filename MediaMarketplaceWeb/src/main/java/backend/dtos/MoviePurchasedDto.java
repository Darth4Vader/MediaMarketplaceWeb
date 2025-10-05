package backend.dtos;

import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.dtos.references.MovieReference;

/**
 * Data Transfer Object for representing a movie purchase.
 */
public class MoviePurchasedDto {

    /**
     * The unique identifier for the purchased movie.
     */
    private Long id;

    /**
     * The purchase price of the movie.
     */
    private double purchasePrice;
    
    private String currencyCode;

    /**
     * The date and time when the movie was purchased.
     */
    private LocalDateTime purchaseDate;
    
    /**
     * Indicates whether the movie is rented.
     */
    private boolean isRented;

    /**
     * The duration for which the movie is rented, if applicable.
     */
    private Duration rentTime;

    /**
     * The date and time since the movie was rented, if applicable.
     */
    private LocalDateTime rentTimeSincePurchase;

    /**
     * Reference to the movie that was purchased.
     */
    private MovieReference movie;
    
    private boolean isUseable;

    /**
     * Gets the unique identifier for the purchased movie.
     * 
     * @return the unique identifier of the movie purchase
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the purchased movie.
     * 
     * @param id the unique identifier of the movie purchase
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the purchase price of the movie.
     * 
     * @return the purchase price of the movie
     */
    public double getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Sets the purchase price of the movie.
     * 
     * @param purchasePrice the purchase price of the movie
     */
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * Gets the date and time when the movie was purchased.
     * 
     * @return the purchase date and time of the movie
     */
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the date and time when the movie was purchased.
     * 
     * @param purchaseDate the purchase date and time of the movie
     */
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Checks if the movie is rented.
     * 
     * @return true if the movie is rented, false otherwise
     */
    public boolean isRented() {
        return isRented;
    }

    /**
     * Sets whether the movie is rented.
     * 
     * @param isRented true if the movie is rented, false otherwise
     */
    @JsonProperty("isRented")
    public void setRented(boolean isRented) {
        this.isRented = isRented;
    }

    /**
     * Gets the duration for which the movie is rented.
     * 
     * @return the duration of the rental period
     */
    public Duration getRentTime() {
        return rentTime;
    }

    /**
     * Sets the duration for which the movie is rented.
     * 
     * @param rentTime the duration of the rental period
     */
    public void setRentTime(Duration rentTime) {
        this.rentTime = rentTime;
    }

    /**
     * Gets the date and time since the movie was rented.
     * 
     * @return the date and time since the movie was rented
     */
    public LocalDateTime getRentTimeSincePurchase() {
        return rentTimeSincePurchase;
    }

    /**
     * Sets the date and time since the movie was rented.
     * 
     * @param rentTimeSincePurchase the date and time since the movie was rented
     */
    public void setRentTimeSincePurchase(LocalDateTime rentTimeSincePurchase) {
        this.rentTimeSincePurchase = rentTimeSincePurchase;
    }

    /**
     * Gets the reference to the movie that was purchased.
     * 
     * @return the reference to the movie
     */
    public MovieReference getMovie() {
        return movie;
    }

    /**
     * Sets the reference to the movie that was purchased.
     * 
     * @param movie the reference to the movie
     */
    public void setMovie(MovieReference movie) {
        this.movie = movie;
    }

	public boolean isUseable() {
		return isUseable;
	}

	@JsonProperty("isUseable")
	public void setUseable(boolean isUseable) {
		this.isUseable = isUseable;
	}
    
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
}