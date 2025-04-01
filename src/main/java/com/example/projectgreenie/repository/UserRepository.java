package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRole(String email, String role); // ✅ Added method to find admins
}
