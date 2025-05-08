package backend.dtos;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Data Transfer Object for representing a shopping cart.
 */
public class CartDto {

    /**
     * The list of products in the cart.
     */
    private Page<CartProductDto> cartProducts;

    /**
     * The total price of all products in the cart.
     */
    private double totalPrice;
    
    private int totalItems;

    /**
     * Gets the list of products in the cart.
     * 
     * @return a {@link List} of {@link CartProductDto} representing the products in the cart
     */
    public Page<CartProductDto> getCartProducts() {
        return cartProducts;
    }

    /**
     * Sets the list of products in the cart.
     * 
     * @param cartProducts a {@link List} of {@link CartProductDto} to set as the products in the cart
     */
    public void setCartProducts(Page<CartProductDto> cartProducts) {
        this.cartProducts = cartProducts;
    }

    /**
     * Gets the total price of all products in the cart.
     * 
     * @return the total price of the products in the cart
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price of all products in the cart.
     * 
     * @param totalPrice the total price to set for the products in the cart
     */
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
	public int getTotalItems() {
		return totalItems;
	}
	
	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
}