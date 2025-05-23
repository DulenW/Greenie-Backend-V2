package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.Product;
import com.example.projectgreenie.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.isEmpty() 
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pid/{productID}")
    public ResponseEntity<Product> getProductByProductID(@PathVariable int productID) {
        Product product = productRepository.findByProductID(productID);
        return product != null 
            ? ResponseEntity.ok(product)
            : ResponseEntity.notFound().build();
    }

    private int getNextAvailableProductID() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return 1;
        }
        return products.stream()
                .mapToInt(Product::getProductID)
                .max()
                .orElse(0) + 1;
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        try {
            // Always generate a new product ID
            product.setProductID(getNextAvailableProductID());

            // Save the product
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{productID}")
    public ResponseEntity<?> deleteProduct(@PathVariable int productID) {
        try {
            Product product = productRepository.findByProductID(productID);
            if (product != null) {
                productRepository.delete(product);
                return ResponseEntity.ok().body("Product deleted successfully");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update/{productID}")
    public ResponseEntity<Product> updateProduct(@PathVariable int productID, @RequestBody Product updatedProduct) {
        try {
            Product existingProduct = productRepository.findByProductID(productID);
            if (existingProduct != null) {
                // Keep the original ID and update other fields
                updatedProduct.setId(existingProduct.getId());
                updatedProduct.setProductID(productID);
                return ResponseEntity.ok(productRepository.save(updatedProduct));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
