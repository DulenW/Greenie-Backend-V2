package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.PointsApplyRequest;
import com.example.projectgreenie.dto.PointsApplyResponse;
import com.example.projectgreenie.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    private final UserService userService;

    public OrderController(UserService userService) {
        this.userService = userService;
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
}
