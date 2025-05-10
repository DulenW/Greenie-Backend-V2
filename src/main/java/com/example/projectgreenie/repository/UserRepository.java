package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndRole(String email, String role);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);

}