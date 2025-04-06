package backend.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.dtos.MoviePurchasedDto;
import backend.dtos.OrderDto;
import backend.entities.Cart;
import backend.entities.CartProduct;
import backend.entities.Movie;
import backend.entities.MoviePurchased;
import backend.entities.Order;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.PurchaseOrderException;
import backend.repositories.OrderRepository;

/**
 * Service class for managing orders.
 * <p>
 * This class handles business logic related to user orders, including placing new orders,
 * retrieving orders made by the user, and converting order entities to DTOs. 
 * </p>
 * <p>
 * It acts as an
 * intermediary between the data access layer (repositories) and the presentation layer (controllers),
 * ensuring that business rules are enforced and operations are performed correctly.
 * </p>
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenService tokenService;

    /**
     * Retrieves a list of orders made by the current user.
     * <p>
     * This method fetches the currently authenticated user, retrieves their orders, and
     * converts them to DTOs for use in the presentation layer. If the user has not made any orders,
     * an empty list is returned.
     * </p>
     * 
     * @return A list of {@link OrderDto} objects representing the orders made by the current user.
     */
    public List<OrderDto> getUserOrders() {
        User user = tokenService.getCurretUser();
        List<OrderDto> orderDtos = new ArrayList<>();
        try {
            List<Order> orders = getOrdersMadeByUser(user);
            if (orders != null) {
                for (Order order : orders) {
                    if (order != null) {
                        OrderDto orderDto = convertOrderToDto(order);
                        orderDtos.add(orderDto);
                    }
                }
            }
        } catch (EntityNotFoundException e) {
            // If the user didn't make an order, then we will return an empty list.
        }
        return orderDtos;
    }

    /**
     * The rent time in minutes.
     * <p>
     * This constant defines the default duration for renting a movie. It is used when creating
     * rental items from the cart.
     * </p>
     */
    private static final int RENT_TIME = 3;

    /**
     * Places a new order based on the current user's cart.
     * <p>
     * This method retrieves the current user's cart, calculates the total price, and converts
     * cart products to purchased items. It then creates an order, associates it with the user,
     * and saves it to the database. If the cart is empty, a {@link PurchaseOrderException} is thrown.
     * </p>
     * 
     * @return The ID of the newly created order.
     * @throws PurchaseOrderException if the cart is empty or any error occurs during order placement.
     * @throws EntityNotFoundException if any required entities (e.g., cart, products) are not found.
     */
    @Transactional
    public Long placeOrder() throws PurchaseOrderException, EntityNotFoundException {
        // First, load the User Cart and the CartProducts.
        User user = tokenService.getCurretUser();
        Cart cart = cartService.getCartByUser(user);
        List<CartProduct> cartProducts = cart.getCartProducts();
        double totalPrice = 0;
        Order order = new Order();

        // If the cart is empty, throw an exception.
        if (cartProducts.isEmpty()) {
            throw new PurchaseOrderException("The Cart is empty");
        }

        // Convert CartProducts to MoviePurchased items and calculate the total price.
        for (CartProduct cartProduct : cartProducts) {
            Product product = cartProduct.getProduct();
            boolean isBuy = cartProduct.isBuying();
            double price = CartService.calculateCartProductPrice(product, isBuy);
            Movie movie = product.getMovie();

            // Create the movie purchased item from the cart product.
            MoviePurchased orderItem = new MoviePurchased();
            orderItem.setMovie(movie);
            orderItem.setPurchasePrice(price);
            orderItem.setRented(!isBuy);
            
            // Check if it is a rent or a purchase.
            if (!isBuy) {
                orderItem.setRentTime(Duration.ofMinutes(RENT_TIME)); // Setting default rent time
            }

            totalPrice += price;
            order.addToPurchasedItems(orderItem);
        }

        order.setTotalPrice(totalPrice);
        order.setUser(user);
        cartService.removeCartFromUser(cart);
        Order createdOrder = orderRepository.save(order);
        return createdOrder.getId();
    }

    /**
     * Converts an Order entity to an OrderDto.
     * <p>
     * This method transforms an {@link Order} entity into an {@link OrderDto} object for use
     * in the presentation layer. It includes details of the order and its purchased items.
     * </p>
     * 
     * @param order The {@link Order} entity to convert.
     * @return An {@link OrderDto} object containing details of the order.
     */
    private OrderDto convertOrderToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPurchasedDate(order.getPurchasedDate());
        orderDto.setTotalPrice(order.getTotalPrice());
        List<MoviePurchased> moviePurchasedList = order.getPurchasedItems();
        List<MoviePurchasedDto> moviePurchasedDtoList = new ArrayList<>();
        // Convert all the movie purchased items into DTOs.
        if (moviePurchasedList != null) {
            for (MoviePurchased moviePurchased : moviePurchasedList) {
                if (moviePurchased != null) {
                    MoviePurchasedDto moviePurchasedDto = MoviePurchasedService.convertMoviePurchasedtoDto(moviePurchased);
                    moviePurchasedDtoList.add(moviePurchasedDto);
                }
            }
        }

        orderDto.setPurchasedItems(moviePurchasedDtoList);
        return orderDto;
    }

    /**
     * Retrieves a list of orders made by a specific user.
     * <p>
     * This method fetches all orders associated with the provided user. If no orders are found,
     * an {@link EntityNotFoundException} is thrown.
     * </p>
     * 
     * @param user The {@link User} whose orders are to be retrieved.
     * @return A list of {@link Order} entities associated with the user.
     * @throws EntityNotFoundException if no orders are found for the user.
     */
    private List<Order> getOrdersMadeByUser(User user) throws EntityNotFoundException {
        return orderRepository.findByUser(user).orElseThrow(() -> new EntityNotFoundException("No orders found for the user"));
    }
}