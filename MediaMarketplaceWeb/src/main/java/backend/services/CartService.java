package backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.dtos.CartDto;
import backend.dtos.CartProductDto;
import backend.dtos.ProductDto;
import backend.dtos.carts.UpdatedCartProductDto;
import backend.dtos.references.CartProductReference;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.BadRequestException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.UserNotLoggedInException;
import backend.repositories.CartProductRepository;
import backend.repositories.CartRepository;
import backend.sort.entities.CartProductSort;
import backend.utils.PurchaseType;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.servlet.http.HttpSession;

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
    public CartDto getCart(Pageable pageable, HttpSession session) {
    	// first we load the cart of the current session
        Cart cart = getCartOfSession(session);
        // load the cart products for the requested page
        Page<CartProduct> cartProductsPage = searchCartProductsResult(cart, pageable);
        // And then we convert it to a cart DTO
        CartDto cartDto = new CartDto();
        Page<CartProductDto> cartProductsDtoPage = cartProductsPage.map(cartProduct -> {
        	return convertCartProductToDto(cartProduct);
		});
        cartDto.setCartProducts(cartProductsDtoPage);
        cartDto.setTotalPrice(calculateCartTotalPrice(cart));
        cartDto.setTotalItems(calculateCartProductTotalItems(cart));
        return cartDto;
    }
    
    public Page<CartProduct> searchCartProductsResult(Cart cart, Pageable pageable) {
    	Sort sort = pageable.getSort();
    	List<Order> customSortOrders = new ArrayList<>();
    	for(Order order : sort) {
    		String property = order.getProperty();
    		CartProductSort movieSort = CartProductSort.fromValue(property);
    		if(movieSort != null) {
    			customSortOrders.add(order);
    		}
		}
    	if(customSortOrders.size() > 0) {
			// If there are custom sort orders, we apply them.
    		Sort defaultSort = Sort.by(sort.stream()
					    				.filter(order -> !customSortOrders.contains(order))
					    				.toList());
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
		}
    	Specification<CartProduct> specification = createCartProductSortSpecification(cart, Sort.by(customSortOrders));
    	Page<CartProduct> cartProductsPage = getCartProductOfCart(pageable, specification);
		return cartProductsPage;
    }
    
	public Specification<CartProduct> createCartProductSortSpecification(Cart cart, Sort sort) {
	    Specification<CartProduct> spec = (root, query, cb) -> {
	        List<jakarta.persistence.criteria.Order> orderBy = new ArrayList<>();
            if(sort != null) {
				for(Order order : sort) {
					String property = order.getProperty();
					CartProductSort cartProductSort = CartProductSort.fromValue(property);
					if(cartProductSort == CartProductSort.PRICE) {
						Join<CartProduct, Product> productJoin = root.join("product", JoinType.INNER);
						Expression<?> price = cb.selectCase()
							.when(cb.equal(root.get("purchaseType"), PurchaseType.BUY.getType()), productJoin.get("buyPrice"))
							.when(cb.equal(root.get("purchaseType"), PurchaseType.RENT.getType()), productJoin.get("rentPrice"))
							.otherwise(0.0);
						query.groupBy(root.get("id"));
						orderBy.add(order.isAscending() ? cb.asc(price) : cb.desc(price));
					}
					else if(cartProductSort == CartProductSort.DISCOUNT ) {
						Join<CartProduct, Product> productJoin = root.join("product", JoinType.INNER);
						Expression<?> discount = cb.selectCase()
							.when(cb.equal(root.get("purchaseType"), PurchaseType.BUY.getType()), productJoin.get("buyDiscount"))
							.when(cb.equal(root.get("purchaseType"), PurchaseType.RENT.getType()), productJoin.get("rentDiscount"))
							.otherwise(0.0);
						query.groupBy(root.get("id"));
						orderBy.add(order.isAscending() ? cb.asc(discount) : cb.desc(discount));
					}
				}
            }
            
            if(orderBy.size() > 0) {
				query.orderBy(orderBy);
            }
            
	        return cb.and(cb.equal(root.get("cart"), cart));
	    };
	    return spec;
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
     * @throws BadRequestException 
     * @throws  
     */
    @Transactional
    public void addProductToCart(CartProductReference cartProductReference, HttpSession session) throws EntityNotFoundException, EntityAlreadyExistsException, BadRequestException {
    	// check that the request content is valid
    	validateCartProductReference(cartProductReference);
    	// First load or create the user's cart.
        Product product = productService.getProductByID(cartProductReference.getProductId());
    	// load the cart of the current session
        Cart cart = getCartOfSession(session);
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
        cartProduct.setSelected(true);
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
    public void removeProductFromCart(Long productId, HttpSession session) throws EntityNotFoundException {
    	// first we load the cart of the current session
        Cart cart = getCartOfSession(session);
        Product product = productService.getProductByID(productId);
        // Now remove the product from the cart.
        removeProductFromCart(cart, product);
    }
    
    @Transactional
    public UpdatedCartProductDto updateCartProduct(Long productId, CartProductReference cartProductReference, HttpSession session) throws EntityNotFoundException, BadRequestException {
    	// check that the request content is valid
    	String newPurchaseType = cartProductReference.getPurchaseType();
    	if(newPurchaseType != null)
    		validateCartProductPurchaseType(newPurchaseType);
    	// load the cart of the current session and the product
        Cart cart = getCartOfSession(session);
        Product product = productService.getProductByID(productId);
        CartProduct cartProduct = getProductInCart(cart, product);
        if (cartProduct != null) {
			// Update the cart product as needed
			// For example, you can change the buying type or other properties
        	if(newPurchaseType != null)
        		cartProduct.setPurchaseType(newPurchaseType);
        	Boolean isSelected = cartProductReference.isSelected();
        	if(isSelected != null)
        		cartProduct.setSelected(isSelected);
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
    
    private void validateCartProductReference(CartProductReference cartProductReference) throws BadRequestException {
		String type = cartProductReference.getPurchaseType();
    	validateCartProductPurchaseType(type);
	}
    
    private void validateCartProductPurchaseType(String type) throws BadRequestException {
    	if (PurchaseType.fromString(type) == null) {
			throw new BadRequestException(type + " is not a valid purchase type");
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
    
    @Transactional
    public Cart getCartOfSession(HttpSession session) {
    	// first check if the session have a cart, otherwise create one
    	//String sessionId = session.getId();
    	Cart sessionCart = null;
    	Cart userCart = null;
    	User user = null;
    	try {
    		user = tokenService.getCurretUser();
        	// now check if user already have a cart, if so then we will merge it with the session cart
        	if(user != null) {
        		Optional<Cart> userCartOpt = cartRepository.findByUser(user);
        		if(userCartOpt.isPresent()) {
        			userCart = userCartOpt.get();
    			}
        	}
    	}
    	catch(UserNotLoggedInException e) {}
    	Long cartId = (Long) session.getAttribute("Cart");
    	if(cartId != null) {
    		Optional<Cart> cartOptional = cartRepository.findById(cartId);
    		sessionCart = cartOptional.orElse(null);
    	}
    	Cart cart = null;
    	if(sessionCart == null) {
    		// if user have cart, then let's use it
    		if(userCart != null) {
				cart = userCart;
			}
			else {
				// if user does not have a cart, then we will create one
				cart = createCart(user);
			}
    	}
    	else {
    		// have session cart and user logged
    		if(user != null) {
    			// check if the user cart is the same as the session cart
    			// otherwise let's merge
    			if(userCart != null) {
					if(!userCart.getId().equals(sessionCart.getId())) {
						// merge the two carts
						// user cart remains, session cart is removed, and merged into the user cart
						List<CartProduct> sessionCartProducts = sessionCart.getCartProducts();
						List<CartProduct> removeCartProducts = new ArrayList<>();
						for (CartProduct cartProduct : sessionCartProducts) {
							// check if the product is already in the user cart
							CartProduct productInUserCart = getProductInCart(userCart, cartProduct.getProduct());
							if (productInUserCart == null) {
								// if not, add it to the user cart
								userCart.addToCartProducts(cartProduct);
							}
							else {
								// remove the cart product from the session cart
								removeCartProducts.add(cartProduct);
							}
						}
						// after we merged the cart products, we need to remove them from session cart
						for (CartProduct cartProduct : removeCartProducts) {
							removeProductFromCart(sessionCart, cartProduct);
						}
						sessionCart.setCartProducts(null);
						sessionCart.setUser(null);
						// now remove the session cart
						removeCartFromUser(sessionCart);
						cart = userCart;
					}
					else {
						// if the user cart is the same as the session cart, then we can use it
						cart = userCart;
					}
    			}
    			else {
    				// otherwise user does not have a cart, let's give him session cart
    				cart = sessionCart;
    				sessionCart.setUser(user);
    			}
    		}
    		else {
    			// user is not logged, let's check if the session cart is not connected to any user
    			User sessionCartUser = sessionCart.getUser();
    			if(sessionCartUser != null) {
    				// if it is connected to a user, create empty cart
    				cart = createCart(user);
    			}
    			else {
    				cart = sessionCart;
    			}
    		}
    	}
    	session.setAttribute("Cart", cart.getId());
    	return cart;
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
    	cart.addToCartProducts(cartProduct);
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
        // if there are no products, then return
    	if(cartProducts == null) return;
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
    
    private Page<CartProduct> getCartProductOfCart(Pageable pageable, Specification<CartProduct> spec) {
    	return cartProductRepository.findAll(spec, pageable);
    }
    
    public CartProductDto convertCartProductToDto(CartProduct cartProduct) {
		CartProductDto cartProductDto = new CartProductDto();
		Product product = cartProduct.getProduct();
		ProductDto productDto = productService.convertProductToDto(product);
		cartProductDto.setProduct(productDto);
		String purchaseType = cartProduct.getPurchaseType();
		cartProductDto.setPurchaseType(purchaseType);
		boolean isSelected = cartProduct.isSelected();
		cartProductDto.setSelected(isSelected);
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