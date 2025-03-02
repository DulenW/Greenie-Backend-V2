package com.example.projectgreenie.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private int productID;
    private String productName;
    private double price;
    private int numberOfPoints;
    private int quantity;
    private String shortDescription;
    private String fullDescription;
    private String imgURL;
}
