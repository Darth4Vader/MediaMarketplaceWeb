package backend.dtos.carts;

import backend.dtos.CartProductDto;
import backend.dtos.general.PriceDto;

public class UpdatedCartProductDto {
	
	private CartProductDto cartProduct;
	
	private int totalItems;
	
	private PriceDto totalPrice;

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
}