package backend.dtos.carts;

import backend.dtos.CartProductDto;

public class UpdatedCartProductDto {
	
	private CartProductDto cartProduct;
	
	private int totalItems;
	
	private double totalPrice;

	public UpdatedCartProductDto() {
		
	}

	public CartProductDto getCartProduct() {
		return cartProduct;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setCartProduct(CartProductDto cartProduct) {
		this.cartProduct = cartProduct;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
}