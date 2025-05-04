package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.AdminRegisterDTO;
import com.example.projectgreenie.model.Admin;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.model.Order;
import com.example.projectgreenie.model.Product;
import com.example.projectgreenie.model.User;
import com.example.projectgreenie.repository.AdminRepository;
import com.example.projectgreenie.repository.FeedPostRepository;
import com.example.projectgreenie.repository.OrderRepository;
import com.example.projectgreenie.repository.ProductRepository;
import com.example.projectgreenie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedPostRepository feedPostRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private String generateUniqueAdminId() {
        String adminId;
        do {
            Random random = new Random();
            String firstPart = String.format("%04X", random.nextInt(0xFFFF));
            String secondPart = String.format("%04X", random.nextInt(0xFFFF));
            adminId = "ADMIN-" + firstPart + "-" + secondPart;
        } while (adminRepository.existsByAdminId(adminId));
        return adminId;
    }

    public Admin registerAdmin(AdminRegisterDTO registerDTO) {
        if (adminRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Admin admin = Admin.builder()
                .id(UUID.randomUUID().toString())
                .adminId(generateUniqueAdminId())
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(registerDTO.getRole())
                .build();

        return adminRepository.save(admin);
    }

    public Admin authenticateAdmin(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return admin;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public void removeAdmin(String adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Prevent removing the last admin
        long adminCount = adminRepository.count();
        if (adminCount <= 1) {
            throw new RuntimeException("Cannot remove the last admin");
        }

        adminRepository.delete(admin);
    }


    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();


        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);


        long totalPosts = feedPostRepository.count();
        stats.put("totalPosts", totalPosts);


        long totalProducts = productRepository.count();
        stats.put("activeProducts", totalProducts);


        long totalMembers = userRepository.count();
        stats.put("activeMembers", totalMembers);

        return stats;
    }


    public List<Order> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable).getContent();
    }


    public List<FeedPost> getRecentPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return feedPostRepository.findAll(pageable).getContent();
    }
}