package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException.UnprocessableEntity;

import backend.dtos.CartDto;
import backend.dtos.CartProductDto;
import backend.dtos.ProductDto;
import backend.dtos.carts.UpdatedCartProductDto;
import backend.dtos.references.CartProductReference;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityUnprocessableException;
import backend.repositories.CartProductRepository;
import backend.repositories.CartRepository;
import backend.utils.PurchaseType;

/**
 * Service class for managing user shopping carts.
 * <p>
 * This class provides methods to manage products within a user's cart, including retrieving,
 * adding, and removing products. It serves as the business layer of the Spring application,
 * handling all logic operations related to shopping cart functionality.
 * </p>
 * <p>
 * It acts as an intermediary between the data access layer (repositories) and
 * the presentation layer (controllers), managing the business logic for cart operations.
 * </p>
 */
@Service
public class CartService {
	
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartProductRepository cartProductRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private TokenService tokenService;
    
    /**
     * Retrieves the current user's shopping cart as a DTO.
     * <p>
     * This method fetches the cart associated with the currently authenticated user and converts
     * it into a {@link CartDto} object, including details of all products in the cart and the
     * total price.
     * </p>
     *
     * @return A {@link CartDto} representing the user's cart.
     * @throws EntityNotFoundException if the user does not have a cart.
     */
    public CartDto getCart() throws EntityNotFoundException {
    	// First we load the current user's cart
    	User user = tokenService.getCurretUser();
        Cart cart = getCartByUser(user);
        // And then we convert it to a cart DTO
        CartDto cartDto = new CartDto();
        List<CartProduct> cartProducts = cart.getCartProducts();
        List<CartProductDto> cartProductsDtos = new ArrayList<>();
        double totalPrice = 0;
        if (cartProducts != null) {
            for (CartProduct cartProduct : cartProducts) {
                if (cartProduct != null) {
                    CartProductDto cartProductDto = convertCartProductToDto(cartProduct);
                    totalPrice += cartProductDto.getPrice();
                    cartProductsDtos.add(cartProductDto);
                }
            }
        }
        cartDto.setCartProducts(cartProductsDtos);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setTotalItems(calculateCartProductTotalItems(cart));
        return cartDto;
    }
    
    /**
     * Adds a product to the user's shopping cart.
     * <p>
     * This method allows a user to add a product with a specified purchase type (buying or renting)
     * to their cart. If the product is already in the cart with the same purchase type, an exception
     * is thrown. If the product is in the cart with a different purchase type, it is updated.
     * </p>
     *
     * @param cartProductReference A {@link CartProductReference} containing product ID and
     *                             buying type.
     * @throws EntityNotFoundException if the product is not found.
     * @throws EntityAlreadyExistsException if the product is already in the cart with the same purchase type.
     * @throws EntityUnprocessableException 
     * @throws  
     */
    @Transactional
    public void addProductToCart(CartProductReference cartProductReference) throws EntityNotFoundException, EntityAlreadyExistsException, EntityUnprocessableException {
    	// check that the request content is valid
    	validateCartProductReference(cartProductReference);
    	// First load or create the user's cart.
    	User user = tokenService.getCurretUser();
        Product product = productService.getProductByID(cartProductReference.getProductId());
        Cart cart;
        try {
        	// Load the cart
        	cart = getCartByUser(user);
        } catch (EntityNotFoundException e) {
        	// This happens when the user does not have a cart, so we will create one for him
        	cart = createCart(user);
        }
        // First check if the product is already inside the cart with the same purchase type
        CartProduct productInCart = getProductInCart(cart, product);
        if (productInCart != null) {
            if (PurchaseType.fromString(cartProductReference.getPurchaseType()) == PurchaseType.fromString(productInCart.getPurchaseType())) {
                throw new EntityAlreadyExistsException("The Product is already in the Cart");
            }
            // If we want to buy instead of rent, or vice versa, then we will remove the current product in cart, and then add it as new with the purchase type
            removeProductFromCart(cart, productInCart);
        }
        // Add product with the purchasing type to cart
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(cart);
        cartProduct.setPurchaseType(cartProductReference.getPurchaseType());
        addProductToCart(cart, cartProduct);
    }
    
    /**
     * Removes a product from the user's shopping cart.
     * <p>
     * This method allows a user to remove a specified product from their cart.
     * </p>
     *
     * @param cartProductReference A {@link CartProductReference} containing product ID to 
     *                             be removed.
     * @throws EntityNotFoundException if the product is not found or the user does not have a cart.
     */
    @Transactional
    public void removeProductFromCart(Long productId) throws EntityNotFoundException {
    	// Load the cart and the product
    	User user = tokenService.getCurretUser();
        Cart cart = getCartByUser(user);
        Product product = productService.getProductByID(productId);
        // Now remove the product from the cart.
        removeProductFromCart(cart, product);
    }
    
    @Transactional
    public UpdatedCartProductDto updateCartProduct(Long productId, CartProductReference cartProductReference) throws EntityNotFoundException, EntityUnprocessableException {
    	// check that the request content is valid
    	validateCartProductReference(cartProductReference);
    	// Load the cart and the product
    	User user = tokenService.getCurretUser();
        Cart cart = getCartByUser(user);
        Product product = productService.getProductByID(productId);
        CartProduct cartProduct = getProductInCart(cart, product);
        if (cartProduct != null) {
			// Update the cart product as needed
			// For example, you can change the buying type or other properties
			cartProduct.setPurchaseType(cartProductReference.getPurchaseType());
			CartProduct updatedCartProduct = cartProductRepository.save(cartProduct);
			UpdatedCartProductDto dto = new UpdatedCartProductDto();
			dto.setCartProduct(convertCartProductToDto(updatedCartProduct));
			dto.setTotalItems(calculateCartProductTotalItems(cart));
			dto.setTotalPrice(calculateCartTotalPrice(cart));
			return dto;
		} else {
			throw new EntityNotFoundException("Product not found in the cart");
		}
	}
    
    /**
     * Creates a new cart for the specified user.
     * <p>
     * This method initializes a new cart for a user who does not yet have one.
     * </p>
     *
     * @param user The user for whom the cart will be created.
     * @return The created {@link Cart}.
     */
    @Transactional
    public Cart createCart(User user) {
        Cart cart = new Cart(user);
        return cartRepository.save(cart);
    }
    
    private void validateCartProductReference(CartProductReference cartProductReference) throws UnprocessableEntity, EntityUnprocessableException {
		String type = cartProductReference.getPurchaseType();
    	if (PurchaseType.fromString(type) == null) {
			throw new EntityUnprocessableException(type + " is not a valid purchase type");
		}
	}
    
    /**
     * Retrieves the cart associated with the specified user.
     *
     * @param user The user whose cart is to be retrieved.
     * @return The user's {@link Cart}.
     * @throws EntityNotFoundException if the user does not have a cart.
     */
    public Cart getCartByUser(User user) throws EntityNotFoundException {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("The user does not have a cart"));
    }
    
    /**
     * Retrieves the product from the user's cart.
     *
     * @param cart The user's cart.
     * @param product The product to search for.
     * @return The {@link CartProduct} if found, null otherwise.
     */
    private CartProduct getProductInCart(Cart cart, Product product) {
        List<CartProduct> cartProducts = cart.getCartProducts();
        return getProductInCart(cartProducts, product);
    }
    
    /**
     * Searches for a product in the list of cart products.
     *
     * @param cartProducts The list of cart products.
     * @param product The product to search for.
     * @return The {@link CartProduct} if found, null otherwise.
     */
    private CartProduct getProductInCart(List<CartProduct> cartProducts, Product product) {
        for (CartProduct cartProduct : cartProducts) {
            if (cartProduct.getProduct().equals(product)) {
                return cartProduct;
            }
        }
        return null;
    }
    
    /**
     * Adds a product to the specified user's cart.
     * 
     * @param cart The user's cart.
     * @param cartProduct The product to add to the cart.
     */
    @Transactional
    private void addProductToCart(Cart cart, CartProduct cartProduct) {
    	// First we save the cart product into the database
    	cartProductRepository.save(cartProduct);
    	// Then we add the CartProduct into the Cart
    	List<CartProduct> cartProducts = cart.getCartProducts();
        cartProducts.add(cartProduct);
    }
    
    /**
     * Removes a specific product from the user's cart.
     *
     * @param cart The user's cart from which the product will be removed.
     * @param product The product to be removed from the cart.
     * @throws EntityNotFoundException if the product is not found in the cart.
     */
    @Transactional
    private void removeProductFromCart(Cart cart, Product product) throws EntityNotFoundException {
        List<CartProduct> cartProducts = cart.getCartProducts();
        // Search if the product is in the cart, have a CartProduct
        CartProduct productInCart = getProductInCart(cartProducts, product);
        if (productInCart != null) {
            // If the product is in the cart then remove it
            removeProductFromCart(cartProducts, productInCart);
        } else {
            // If the product is not in the cart
            throw new EntityNotFoundException("Product is not in the Cart");
        }
    }
    
    /**
     * Removes a product from the specified user's cart.
     *
     * @param cart The user's cart.
     * @param cartProduct The product to be removed.
     */
    @Transactional
    private void removeProductFromCart(Cart cart, CartProduct cartProduct) {
    	removeProductFromCart(cart.getCartProducts(), cartProduct);
    }
    
    /**
     * Removes a specific CartProduct from the list and database.
     *
     * @param cartProducts The list of CartProducts in the cart.
     * @param cartProduct The CartProduct to remove.
     */
    @Transactional
    private void removeProductFromCart(List<CartProduct> cartProducts, CartProduct cartProduct) {
        // Remove the CartProduct from the list
        cartProducts.remove(cartProduct);
        // Then remove it from the database
        cartProductRepository.delete(cartProduct);
    }
    
    /**
     * Deletes the specified cart from the user.
     * This method removes the entire cart associated with the user.
     *
     * @param cart The cart to be deleted.
     */
    @Transactional
    public void removeCartFromUser(Cart cart) {
		cartRepository.delete(cart);
    }
    
    public static CartProductDto convertCartProductToDto(CartProduct cartProduct) {
		CartProductDto cartProductDto = new CartProductDto();
		Product product = cartProduct.getProduct();
		ProductDto productDto = ProductService.convertProductToDto(product);
		cartProductDto.setProduct(productDto);
		String purchaseType = cartProduct.getPurchaseType();
		cartProductDto.setPurchaseType(purchaseType);
		double price = calculateCartProductPrice(product, purchaseType);
		cartProductDto.setPrice(price);
		return cartProductDto;
	}
    
    public static int calculateCartProductTotalItems(Cart cart) {
		int totalItems = 0;
		List<CartProduct> cartProducts = cart.getCartProducts();
		if (cartProducts != null) {
			totalItems = cartProducts.size();
		}
		return totalItems;
	}
    
    public static double calculateCartTotalPrice(Cart cart) {
        double totalPrice = 0;
        List<CartProduct> cartProducts = cart.getCartProducts();
        if (cartProducts != null) {
            for (CartProduct cartProduct : cartProducts) {
                if (cartProduct != null) {
                    Product product = cartProduct.getProduct();
                    String purchaseType = cartProduct.getPurchaseType();
                    double price = calculateCartProductPrice(product, purchaseType);
                    totalPrice += price;
                }
            }
        }
        return totalPrice;
    }
    
    /**
     * Calculates the price of a CartProduct.
     *
     * @param product The product.
     * @param isBuying Whether the product is being bought or rented.
     * @return The price of the product based on the buying status.
     */
    public static double calculateCartProductPrice(Product product, String purchaseType) {
        return switch(PurchaseType.fromString(purchaseType)) {
			case BUY -> product.getBuyPrice();
			case RENT -> product.getRentPrice();
			default -> 0;
		};
    }
}