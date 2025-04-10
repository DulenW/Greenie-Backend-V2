package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.PointsApplyRequest;
import com.example.projectgreenie.dto.PointsApplyResponse;
import com.example.projectgreenie.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import com.example.projectgreenie.model.Order;
import com.example.projectgreenie.repository.OrderRepository;
import com.example.projectgreenie.repository.ProductRepository;
import com.example.projectgreenie.model.Product;
import com.example.projectgreenie.dto.OrderItemDTO;
import com.example.projectgreenie.dto.OrderRequestDTO;
import java.time.Instant;
import java.util.Optional;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(UserService userService, OrderRepository orderRepository, ProductRepository productRepository) {
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/apply-points")
    public ResponseEntity<?> applyPoints(@RequestBody PointsApplyRequest request) {
        log.info("Processing points redemption request: userId={}, cartTotal={}, pointsToRedeem={}",
                request.getUserId(), request.getCartTotal(), request.getPointsToRedeem());

        // Basic validation
        if (request.getCartTotal() <= 0) {
            return ResponseEntity.badRequest().body("Cart total must be greater than 0");
        }

        if (request.getPointsToRedeem() <= 0) {
            return ResponseEntity.badRequest().body("Points to redeem must be greater than 0");
        }

        // Get user's current points
        return userService.getUserPoints(request.getUserId())
                .map(availablePoints -> {
                    // Step 1: Check if user has enough points
                    if (availablePoints < request.getPointsToRedeem()) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Insufficient points");
                        errorResponse.put("availablePoints", availablePoints);
                        errorResponse.put("requestedPoints", request.getPointsToRedeem());
                        return ResponseEntity.badRequest().body(errorResponse);
                    }

                    // Step 2: Check if points don't exceed cart total (1 point = 1 Rs)
                    if (request.getPointsToRedeem() > request.getCartTotal()) {
                        return ResponseEntity.badRequest()
                                .body("Points to redeem cannot exceed cart total");
                    }

                    try {
                        // Step 3: Calculate new totals
                        double pointsValue = request.getPointsToRedeem(); // 1 point = 1 Rs
                        double newTotal = request.getCartTotal() - pointsValue;
                        int remainingPoints = availablePoints - request.getPointsToRedeem();

                        // Step 4: Update user's points in database
                        if (!userService.updateUserPoints(request.getUserId(), remainingPoints)) {
                            return ResponseEntity.internalServerError()
                                    .body("Failed to update points balance");
                        }

                        // Step 5: Build success response
                        PointsApplyResponse response = PointsApplyResponse.builder()
                                .newTotalAmount(newTotal)
                                .pointsApplied(request.getPointsToRedeem())
                                .pointsRemaining(remainingPoints)
                                .build();

                        log.info("Points redemption successful: originalTotal={}, pointsUsed={}, newTotal={}, remainingPoints={}",
                                request.getCartTotal(), request.getPointsToRedeem(), newTotal, remainingPoints);

                        return ResponseEntity.ok(response);

                    } catch (Exception e) {
                        log.error("Error processing points redemption", e);
                        return ResponseEntity.internalServerError()
                                .body("Error processing points redemption");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO request) {
        log.info("Received order request: {}", request);

        // Validate order ID uniqueness
        if (orderRepository.existsByOrderId(request.getOrderId())) {
            return ResponseEntity.badRequest().body("Order ID already exists");
        }

        // Validate user exists and get current points
        Optional<Integer> currentPointsOpt = userService.getUserPoints(request.getUserId());
        if (currentPointsOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Validate if user has enough points
        int currentPoints = currentPointsOpt.get();
        if (currentPoints < request.getPointsApplied()) {
            return ResponseEntity.badRequest().body("Insufficient points balance");
        }

        // Validate total amount calculation
        double expectedTotal = request.getSubtotal() - request.getPointsApplied();
        if (Math.abs(expectedTotal - request.getTotalAmount()) > 0.01) {
            return ResponseEntity.badRequest().body("Invalid total amount calculation");
        }

        // Check product quantities and update stock
        for (OrderItemDTO item : request.getCartItems()) {
            Product product = productRepository.findByProductID(Integer.parseInt(item.getProductId()));

            if (product == null) {
                return ResponseEntity.badRequest()
                        .body("Product not found: " + item.getProductId());
            }

            if (product.getQuantity() < item.getQuantity()) {
                return ResponseEntity.badRequest()
                        .body("Insufficient stock for product: " + product.getProductName());
            }

            // Update product quantity
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product); // Save immediately after updating
        }

        try {
            // Create and save order
            Order order = new Order();
            order.setOrderId(request.getOrderId());
            order.setUserId(request.getUserId());
            order.setCartItems(request.getCartItems());
            order.setSubtotal(request.getSubtotal());
            order.setPointsApplied(request.getPointsApplied());
            order.setTotalAmount(request.getTotalAmount());
            order.setShippingAddress(request.getShippingAddress());
            order.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : Instant.now());
            order.setStatus("PENDING");

            Order savedOrder = orderRepository.save(order);

            // Update user's points balance
            int remainingPoints = currentPoints - request.getPointsApplied();
            if (!userService.updateUserPoints(request.getUserId(), remainingPoints)) {
                log.error("Failed to update user points balance");
                // Roll back product quantities
                rollbackProductQuantities(request.getCartItems());
                orderRepository.delete(savedOrder);
                return ResponseEntity.internalServerError()
                        .body("Error updating points balance");
            }

            log.info("Order saved successfully: {}, Points deducted: {}, Remaining points: {}",
                    savedOrder.getOrderId(), request.getPointsApplied(), remainingPoints);

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            log.error("Error saving order", e);
            // Roll back product quantities on error
            rollbackProductQuantities(request.getCartItems());
            return ResponseEntity.internalServerError()
                    .body("Error processing order: " + e.getMessage());
        }
    }

    private void rollbackProductQuantities(List<OrderItemDTO> items) {
        try {
            for (OrderItemDTO item : items) {
                Product product = productRepository.findByProductID(Integer.parseInt(item.getProductId()));
                if (product != null) {
                    product.setQuantity(product.getQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        } catch (Exception e) {
            log.error("Error rolling back product quantities", e);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return orders.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching all orders", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderByOrderId(@PathVariable String orderId) {
        try {
            return orderRepository.findByOrderId(orderId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching order with ID: " + orderId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}