package backend.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents an order in the system.
 * This entity corresponds to the 'orders' table and includes details about the order's total price,
 * date of purchase, the items purchased, and the associated user.
 * 
 * <p>
 * The Order class is used to track purchases made by users, including the total price of the order,
 * the date when the order was created, and a list of movies purchased as part of the order.
 * </p>
 * 
 */
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {
	
    /**
     * The unique identifier for this order.
     * This field is the primary key of the 'orders' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The total price of the order.
     * This field maps to the 'total_price' column in the 'orders' table.
     * 
     * @return the total price of the order
     */
    @Column(name = "total_price")
    private double totalPrice;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "purchased_currency_code")
    private CurrencyKind purchasedCurrency;
    
    /**
     * The date and time when the order was created.
     * This field maps to the 'purchased_date' column in the 'orders' table and is automatically set by the auditing system.
     * 
     * @return the date and time of the order's creation
     */
    @CreatedDate
    @Column(name = "purchased_date", nullable = false)
    private LocalDateTime purchasedDate;
    
    /**
     * The list of items purchased in this order.
     * This field represents a one-to-many relationship with the MoviePurchased entity.
     * 
     * @return the list of purchased items
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL)
    private List<MoviePurchased> purchasedItems;
    
    /**
     * The user who placed this order.
     * This field represents a many-to-one relationship with the User entity.
     * 
     * @return the user who placed the order
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * Default constructor for the Order entity.
     * Initializes the list of purchased items.
     */
    public Order() {
        this.purchasedItems = new ArrayList<>();
    }

    /**
     * Gets the unique identifier for this order.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this order.
     * 
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the total price of the order.
     * 
     * @return the total price
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price of the order.
     * 
     * @param totalPrice the total price to set
     */
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Gets the date and time when the order was created.
     * 
     * @return the date and time of creation
     */
    public LocalDateTime getPurchasedDate() {
        return purchasedDate;
    }

    /**
     * Sets the date and time when the order was created.
     * 
     * @param purchasedDate the date and time to set
     */
    public void setPurchasedDate(LocalDateTime purchasedDate) {
        this.purchasedDate = purchasedDate;
        // Update the purchase date for all purchased items
        for (MoviePurchased purchased : this.purchasedItems) {
            purchased.setPurchaseDate(purchasedDate);
        }
    }

    /**
     * Gets the list of items purchased in this order.
     * 
     * @return the list of purchased items
     */
    public List<MoviePurchased> getPurchasedItems() {
        return purchasedItems;
    }

    /**
     * Adds a movie purchase item to the list of purchased items.
     * 
     * @param purchasedItem the MoviePurchased item to add
     */
    public void addToPurchasedItems(MoviePurchased purchasedItem) {
        this.purchasedItems.add(purchasedItem);
        purchasedItem.setOrder(this);
    }

    /**
     * Gets the user who placed this order.
     * 
     * @return the user who placed the order
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who placed this order.
     * 
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
	public CurrencyKind getPurchasedCurrency() {
		return purchasedCurrency;
	}
	
	public void setPurchasedCurrency(CurrencyKind purchasedCurrency) {
		this.purchasedCurrency = purchasedCurrency;
	}
}