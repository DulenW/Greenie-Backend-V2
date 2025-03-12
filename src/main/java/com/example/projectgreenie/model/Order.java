package com.example.projectgreenie.model;

import com.example.projectgreenie.dto.OrderItemDTO;
import com.example.projectgreenie.dto.ShippingAddressDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String orderId;
    private String userId;
    private List<OrderItemDTO> cartItems;
    private double subtotal;
    private int pointsApplied;
    private double totalAmount;
    private ShippingAddressDTO shippingAddress;
    private Instant createdAt;
    private String status;
}
