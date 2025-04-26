package backend.dtos;

/**
 * Data Transfer Object for representing a product in a shopping cart.
 */
public class CartProductDto {

    /**
     * The product that is being represented in the cart.
     */
    private ProductDto product;

    /**
     * Indicates whether the product is being bought or rented.
     */
    private String purchaseType;

    /**
     * The price of the product.
     */
    private double price;

    /**
     * Gets the product in the cart.
     * 
     * @return the {@link ProductDto} representing the product
     */
    public ProductDto getProduct() {
        return product;
    }

    /**
     * Sets the product in the cart.
     * 
     * @param product the {@link ProductDto} representing the product
     */
    public void setProduct(ProductDto product) {
        this.product = product;
    }
    
    public String getPurchaseType() {
		return purchaseType;
    }
    
    public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
    }

    /**
     * Gets the price of the product.
     * 
     * @return the price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the product.
     * 
     * @param price the price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }
}