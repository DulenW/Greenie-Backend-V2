package com.example.projectgreenie.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class OrderRequestDTO {
    private String orderId;
    private String userId;
    private List<com.example.projectgreenie.dto.OrderItemDTO> cartItems;
    private double subtotal;
    private int pointsApplied;
    private double totalAmount;
    private com.example.projectgreenie.dto.ShippingAddressDTO shippingAddress;
    private Instant createdAt;
}




