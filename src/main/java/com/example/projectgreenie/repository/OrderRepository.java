package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    boolean existsByOrderId(String orderId);
}
