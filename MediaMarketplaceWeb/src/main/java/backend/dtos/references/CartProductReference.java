package backend.dtos.references;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for referencing a product in a cart and its buying status, using their id.
 */
public class CartProductReference {

    /**
     * The unique identifier for the product.
     * This field cannot be null.
     */
    @NotNull
    private Long productId;

    /**
     * Indicates whether the product is being bought.
     */
    private boolean isBuying;

    /**
     * Gets the unique identifier for the product.
     * 
     * @return the unique identifier of the product
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets the unique identifier for the product.
     * 
     * @param productId the unique identifier of the product
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * Checks if the product is being bought.
     * 
     * @return true if the product is being bought, false if the product is being rented.
     */
    public boolean isBuying() {
        return isBuying;
    }

    /**
     * Sets whether the product is being bought.
     * 
     * @param isBuying true if the product is being bought, false if the product is being rented.
     */
    public void setBuying(boolean isBuying) {
        this.isBuying = isBuying;
    }
}