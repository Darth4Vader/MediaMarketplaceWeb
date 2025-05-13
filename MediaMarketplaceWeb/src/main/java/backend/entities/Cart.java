package backend.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a shopping cart in the database.
 * This entity corresponds to the 'carts' table and includes fields that map to its columns.
 * 
 * <p>
 * The Cart class manages a collection of CartProduct instances and is associated with a specific user.
 * It is designed to store and manage products that a user intends to purchase.
 * </p>
 * 
 */
@Entity
@Table(name = "carts")
public class Cart {
	
	/**
	 * The unique identifier for this cart.
	 * This field is the primary key of the 'carts' table.
	 * 
	 * @return the unique identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * The list of products in the cart.
	 * This field maps to the 'cartProducts' relationship and is fetched eagerly.
	 * The list is automatically managed by the cascading and orphan removal settings.
	 * 
	 * @return the list of products in the cart
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartProduct> cartProducts;
	
	/**
	 * The user who owns this cart.
	 * This field maps to the 'user_id' column in the 'carts' table.
	 * It is fetched eagerly and must be unique.
	 * 
	 * @return the user associated with this cart
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true, unique = true)
	private User user;

	/**
	 * Default constructor for the Cart class.
	 * Initializes the cartProducts list.
	 */
	public Cart() {
		cartProducts = new ArrayList<>();
	}

	/**
	 * Constructs a Cart instance with the specified user.
	 * Initializes the cartProducts list.
	 * 
	 * @param user the user associated with this cart
	 */
	public Cart(User user) {
		this.cartProducts = new ArrayList<>();
		this.user = user;
	}

	/**
	 * Gets the unique identifier for this cart.
	 * 
	 * @return the unique identifier
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Gets the list of products in the cart.
	 * 
	 * @return the list of products in the cart
	 */
	public List<CartProduct> getCartProducts() {
		return cartProducts;
	}

	/**
	 * Gets the user who owns this cart.
	 * 
	 * @return the user associated with this cart
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Adds a product to the cart.
	 * This method also sets the cart reference in the CartProduct instance.
	 * 
	 * @param cartProduct the CartProduct to add to the cart
	 */
	public void addToCartProducts(CartProduct cartProduct) {
		this.cartProducts.add(cartProduct);
		cartProduct.setCart(this);
	}

	/**
	 * Sets the user who owns this cart.
	 * 
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}