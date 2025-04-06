package backend.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a record of a movie purchase or rental in the database.
 * This entity corresponds to the 'movie_purchased' table and includes details about the purchase price,
 * purchase date, and rental information for a movie.
 * 
 * <p>
 * The MoviePurchased class manages the relationship between movies and orders, tracking the purchase details
 * and rental status, the information about the specific movie the user purchased in the specific order.
 * </p>
 * 
 */
@Entity
@Table(name = "movie_purchased")
@EntityListeners(AuditingEntityListener.class)
public class MoviePurchased {

    /**
     * The unique identifier for this movie purchase record.
     * This field is the primary key of the 'movie_purchased' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The price at which the movie was purchased.
     * This field maps to the 'purchase_price' column in the 'movie_purchased' table.
     * 
     * @return the purchase price
     */
    @Column(name = "purchase_price", nullable = false)
    private double purchasePrice;
    
    /**
     * The date and time when the movie was purchased.
     * This field maps to the 'purchased_date' column in the 'movie_purchased' table.
     * This field is automatically set by the auditing system.
     * 
     * @return the purchase date and time
     */
    @CreatedDate
    @Column(name = "purchased_date", nullable = false)
    private LocalDateTime purchaseDate;
    
    /**
     * Indicates whether the movie is rented or not.
     * 
     * @return {@code true} if the movie is rented, {@code false} otherwise
     */
    @Column(name = "is_rented")
    private boolean isRented;
    
    /**
     * The duration for which the movie is rented.
     * This field maps to the 'rent_time' column in the 'movie_purchased' table.
     * 
     * @return the rental duration
     */
    @Column(name = "rent_time")
    private Duration rentTime;
    
    /**
     * The movie associated with this purchase record.
     * This field represents a many-to-one relationship with the Movie entity.
     * 
     * @return the associated movie
     */
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;
    
    /**
     * The order associated with this movie purchase.
     * This field represents a many-to-one relationship with the Order entity.
     * 
     * @return the associated order
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Gets the unique identifier for this movie purchase record.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this movie purchase record.
     * 
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the price at which the movie was purchased.
     * 
     * @return the purchase price
     */
    public double getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Sets the price at which the movie was purchased.
     * 
     * @param purchasePrice the purchase price to set
     */
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * Gets the date and time when the movie was purchased.
     * 
     * @return the purchase date and time
     */
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    
    /**
     * Sets the date and time when the movie was purchased.
     * 
     * @param purchaseDate the purchase date and time to set
     * @throws NullPointerException if the purchase date is {@code null}
     */
    public void setPurchaseDate(@Nonnull LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Indicates whether the movie is rented or not.
     * 
     * @return {@code true} if the movie is rented, {@code false} otherwise
     */
    public boolean isRented() {
        return isRented;
    }

    /**
     * Sets the rental status of the movie.
     * 
     * @param isRented {@code true} if the movie is rented, {@code false} otherwise
     */
    public void setRented(boolean isRented) {
        this.isRented = isRented;
    }

    /**
     * Gets the duration for which the movie is rented.
     * 
     * @return the rental duration
     */
    public Duration getRentTime() {
        return rentTime;
    }

    /**
     * Sets the duration for which the movie is rented.
     * 
     * @param rentTime the rental duration to set
     */
    public void setRentTime(Duration rentTime) {
        this.rentTime = rentTime;
    }

    /**
     * Gets the movie associated with this purchase record.
     * 
     * @return the associated movie
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Sets the movie associated with this purchase record.
     * 
     * @param movie the movie to set
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * Gets the order associated with this movie purchase.
     * 
     * @return the associated order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the order associated with this movie purchase.
     * 
     * @param order the order to set
     */
    public void setOrder(Order order) {
        this.order = order;
    }
}