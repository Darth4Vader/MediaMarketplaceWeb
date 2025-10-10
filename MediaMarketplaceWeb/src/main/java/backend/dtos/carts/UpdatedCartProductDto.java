package backend.dtos.carts;

import backend.dtos.CartProductDto;
import backend.dtos.general.CountryDto;
import backend.dtos.general.PriceDto;

public class UpdatedCartProductDto {
	
	private CartProductDto cartProduct;
	
	private int totalItems;
	
	private PriceDto totalPrice;
	
	private CountryDto country;

	public CartProductDto getCartProduct() {
		return cartProduct;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public PriceDto getTotalPrice() {
		return totalPrice;
	}

	public void setCartProduct(CartProductDto cartProduct) {
		this.cartProduct = cartProduct;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public void setTotalPrice(PriceDto totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public CountryDto getCountry() {
		return country;
	}
	
	public void setCountry(CountryDto country) {
		this.country = country;
	}
}