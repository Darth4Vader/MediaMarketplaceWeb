package backend.dtos;

import java.util.List;

/**
 * Data Transfer Object for representing a shopping cart.
 */
public class CartDto {

    /**
     * The list of products in the cart.
     */
    private List<CartProductDto> cartProducts;

    /**
     * The total price of all products in the cart.
     */
    private double totalPrice;

    /**
     * Gets the list of products in the cart.
     * 
     * @return a {@link List} of {@link CartProductDto} representing the products in the cart
     */
    public List<CartProductDto> getCartProducts() {
        return cartProducts;
    }

    /**
     * Sets the list of products in the cart.
     * 
     * @param cartProducts a {@link List} of {@link CartProductDto} to set as the products in the cart
     */
    public void setCartProducts(List<CartProductDto> cartProducts) {
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
}