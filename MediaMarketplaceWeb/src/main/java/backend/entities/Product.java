package backend.entities;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Represents a product in the system related to movies.
 * This entity maps to the 'products' table and contains information about 
 * movie-related products, including pricing and discounts.
 * 
 * <p>
 * The Product class includes details about buying and renting prices, discounts,
 * and links to the associated movie.
 * </p>
 * 
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * The unique identifier for this product.
     * This field is the primary key of the 'products' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The price for buying this product.
     * This field maps to the 'buy_price' column in the 'products' table.
     * 
     * @return the buy price
     */
    @Column(nullable = false)
    private double buyPrice;
    
    /**
     * The price for renting this product.
     * This field maps to the 'rent_price' column in the 'products' table.
     * 
     * @return the rent price
     */
    @Column(nullable = false)
    private double rentPrice;
    
    /**
     * The discount applicable when buying this product.
     * This field maps to the 'buy_discount' column in the 'products' table.
     * It is represented as a BigDecimal with a precision of 5 and scale of 2, in the format (***.**)
     * 
     * @return the buy discount
     */
    @Column(precision=5, scale=2)
    private BigDecimal buyDiscount;
    
    /**
     * The discount applicable when renting this product.
     * This field maps to the 'rent_discount' column in the 'products' table.
     * It is represented as a BigDecimal with a precision of 5 and scale of 2, in the format (***.**)
     * 
     * @return the rent discount
     */
    @Column(precision=5, scale=2)
    private BigDecimal rentDiscount;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "currency_id")
    private CurrencyKind currency;
    
    /**
     * The movie associated with this product.
     * This field represents a one-to-one relationship with the Movie entity.
     * 
     * @return the associated movie
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    /**
     * Gets the unique identifier for this product.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the price for buying this product.
     * 
     * @return the buy price
     */
    public double getBuyPrice() {
        return buyPrice;
    }

    /**
     * Gets the price for renting this product.
     * 
     * @return the rent price
     */
    public double getRentPrice() {
        return rentPrice;
    }

    /**
     * Gets the discount applicable when buying this product.
     * 
     * @return the buy discount
     */
    public BigDecimal getBuyDiscount() {
        return buyDiscount;
    }

    /**
     * Gets the discount applicable when renting this product.
     * 
     * @return the rent discount
     */
    public BigDecimal getRentDiscount() {
        return rentDiscount;
    }
    
    public CurrencyKind getCurrency() {
		return currency;
    }

    /**
     * Gets the movie associated with this product.
     * 
     * @return the associated movie
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Sets the price for buying this product.
     * 
     * @param buyPrice the buy price to set
     */
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    /**
     * Sets the price for renting this product.
     * 
     * @param rentPrice the rent price to set
     */
    public void setRentPrice(double rentPrice) {
        this.rentPrice = rentPrice;
    }

    /**
     * Sets the discount applicable when buying this product.
     * 
     * @param buyDiscount the buy discount to set as a double
     */
    public void setBuyDiscount(double buyDiscount) {
        setBuyDiscount(new BigDecimal(buyDiscount));
    }

    /**
     * Sets the discount applicable when buying this product.
     * 
     * @param buyDiscount the buy discount to set as a BigDecimal
     */
    public void setBuyDiscount(BigDecimal buyDiscount) {
        this.buyDiscount = buyDiscount;
    }

    /**
     * Sets the discount applicable when renting this product.
     * 
     * @param rentDiscount the rent discount to set as a double
     */
    public void setRentDiscount(double rentDiscount) {
        setRentDiscount(new BigDecimal(rentDiscount));
    }

    /**
     * Sets the discount applicable when renting this product.
     * 
     * @param rentDiscount the rent discount to set as a BigDecimal
     */
    public void setRentDiscount(BigDecimal rentDiscount) {
        this.rentDiscount = rentDiscount;
    }
    
    public void setCurrency(CurrencyKind currency) {
    	this.currency = currency;
    }

    /**
     * Sets the movie associated with this product.
     * 
     * @param movie the associated movie to set
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    
    /**
     * Compares this product to the specified object for equality.
     * <p>
     * Two products are considered equal if they have the same unique identifier.
     * </p>
     * 
     * @param obj the object to compare this product to
     * @return true if this product is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        return Objects.equals(id, other.id);
    }

    /**
     * Returns a hash code value for this product.
     * <p>
     * The hash code is computed based on the unique identifier of the product.
     * </p>
     * 
     * @return the hash code value for this product
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}