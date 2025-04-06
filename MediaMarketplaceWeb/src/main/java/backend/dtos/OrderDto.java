package backend.dtos;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for representing an order.
 */
public class OrderDto {

    /**
     * The unique identifier for the order.
     */
    private Long id;

    /**
     * The total price of the order.
     */
    private double totalPrice;

    /**
     * The date and time when the order was purchased.
     */
    private LocalDateTime purchasedDate;

    /**
     * The list of purchased items in the order.
     */
    private List<MoviePurchasedDto> purchasedItems;

    /**
     * Gets the unique identifier for the order.
     * 
     * @return the unique identifier of the order
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the order.
     * 
     * @param id the unique identifier of the order
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the total price of the order.
     * 
     * @return the total price of the order
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price of the order.
     * 
     * @param totalPrice the total price of the order
     */
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Gets the date and time when the order was purchased.
     * 
     * @return the date and time of purchase
     */
    public LocalDateTime getPurchasedDate() {
        return purchasedDate;
    }

    /**
     * Sets the date and time when the order was purchased.
     * 
     * @param purchasedDate the date and time of purchase
     */
    public void setPurchasedDate(LocalDateTime purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    /**
     * Gets the list of purchased items in the order.
     * 
     * @return the list of purchased items
     */
    public List<MoviePurchasedDto> getPurchasedItems() {
        return purchasedItems;
    }

    /**
     * Sets the list of purchased items in the order.
     * 
     * @param purchasedItems the list of purchased items
     */
    public void setPurchasedItems(List<MoviePurchasedDto> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }
}