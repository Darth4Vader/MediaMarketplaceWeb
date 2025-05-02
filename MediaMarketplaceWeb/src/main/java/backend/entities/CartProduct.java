package backend.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a product within a shopping cart in the database.
 * This entity corresponds to the 'cart_products' table and includes fields that map to its columns.
 * 
 * <p>
 * The CartProduct class represents the association between a product and a cart, including a flag indicating
 * whether the product is being bought. It also manages the cascading behavior when products are deleted.
 * </p>
 * 
 */
@Entity
@Table(name = "cart_products")
public class CartProduct {

    /**
     * The unique identifier for this cart product.
     * This field is the primary key of the 'cart_products' table.
     * 
     * @return the unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The product associated with this cart product.
     * This field maps to the 'product_id' column in the 'cart_products' table.
     * When a Product is removed then removes all of the CartProducts that is mapped to it.
     * 
     * @return the product associated with this cart product
     */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "product_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;
    
    /**
     * The cart that contains this product.
     * This field maps to the 'cart_id' column in the 'cart_products' table.
     * 
     * @return the cart associated with this cart product
     */
    @ManyToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "id", nullable = false)
    private Cart cart;
    
    @JoinColumn(name = "purchase_type", nullable = false)
    private String purchaseType;

    /**
     * Gets the unique identifier for this cart product.
     * 
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the product associated with this cart product.
     * 
     * @return the product associated with this cart product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the cart that contains this product.
     * 
     * @return the cart associated with this cart product
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Sets the unique identifier for this cart product.
     * 
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the product associated with this cart product.
     * 
     * @param product the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Sets the cart that contains this product.
     * 
     * @param cart the cart to set
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    
    public String getPurchaseType() {
		return purchaseType;
    }
    
    public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
    }
}