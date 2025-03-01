package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    Product findByProductID(int productID);
}
