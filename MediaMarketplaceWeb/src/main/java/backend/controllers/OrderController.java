package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.OrderDto;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.PurchaseOrderException;
import backend.services.OrderService;

/**
 * REST controller for managing orders.
 * <p>
 * This controller provides endpoints for retrieving and placing orders for the user.
 * </p>
 */
@RestController
@RequestMapping("/api/users/current/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Retrieves all orders for the user.
     * <p>
     * This endpoint returns a list of orders associated with the currently logged-in user.
     * </p>
     *
     * @return A list of {@link OrderDto} objects representing the user's orders.
     */
    @GetMapping("")
    public Page<OrderDto> getUserOrders(Pageable pageable) {
        return orderService.getUserOrders(pageable);
    }

    /**
     * Places a new order for the user.
     * <p>
     * This endpoint creates a new order for the currently logged-in user.
     * If an issue occurs during the transaction, a {@link PurchaseOrderException} will be thrown.
     * If an entity related to the order is not found, an {@link EntityNotFoundException} will be thrown.
     * </p>
     *
     * @return The ID of the newly created order.
     * @throws PurchaseOrderException If there is an issue during the order placement.
     * @throws EntityNotFoundException If a required entity for the order is not found.
     */
    @PostMapping("/place-order")
    public Long placeOrder() throws PurchaseOrderException, EntityNotFoundException {
        try {
            return orderService.placeOrder();
        } catch (DataAccessException e) {
            throw new PurchaseOrderException("Unable to purchase the movie", e);
        }
    }
}