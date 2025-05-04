package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.AdminRegisterDTO;
import com.example.projectgreenie.model.Admin;
import com.example.projectgreenie.model.FeedPost;
import com.example.projectgreenie.model.Order;
import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "https://test.greenie.dizzpy.dev"})
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRegisterDTO registerDTO) {
        try {
            Admin admin = adminService.registerAdmin(registerDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Admin registered successfully",
                    "adminId", admin.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            Admin admin = adminService.authenticateAdmin(email, password);
            String token = jwtUtil.generateToken(email);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "adminId", admin.getAdminId(),
                    "name", admin.getName(),
                    "role", admin.getRole(),
                    "message", "Admin logged in successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmins() {
        try {
            List<Admin> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch admins: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{adminId}")
    public ResponseEntity<?> removeAdmin(@PathVariable String adminId) {
        try {
            if (adminId == null || adminId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Admin ID is required"
                ));
            }

            adminService.removeAdmin(adminId);
            return ResponseEntity.ok(Map.of(
                    "message", "Admin removed successfully",
                    "deletedAdminId", adminId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Long> stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch dashboard stats: " + e.getMessage()
            ));
        }
    }


    @GetMapping("/dashboard/recent-orders")
    public ResponseEntity<?> getRecentOrders(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Order> orders = adminService.getRecentOrders(limit);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch recent orders: " + e.getMessage()
            ));
        }
    }


    @GetMapping("/dashboard/recent-posts")
    public ResponseEntity<?> getRecentPosts(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<FeedPost> posts = adminService.getRecentPosts(limit);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to fetch recent posts: " + e.getMessage()
            ));
        }
    }
}