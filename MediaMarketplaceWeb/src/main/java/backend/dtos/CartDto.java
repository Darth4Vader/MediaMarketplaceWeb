package backend.dtos;

import java.util.List;

import org.springframework.data.domain.Page;

import backend.dtos.general.CountryDto;
import backend.dtos.general.PriceDto;

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
    private PriceDto totalPrice;
    
    private int totalItems;
    
    private CountryDto country;

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
    public PriceDto getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price of all products in the cart.
     * 
     * @param totalPrice the total price to set for the products in the cart
     */
    public void setTotalPrice(PriceDto totalPrice) {
        this.totalPrice = totalPrice;
    }
    
	public int getTotalItems() {
		return totalItems;
	}
	
	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public CountryDto getCountry() {
		return country;
	}

	public void setCountry(CountryDto country) {
		this.country = country;
	}
}