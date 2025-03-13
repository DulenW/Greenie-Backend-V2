package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.AdminRegisterDTO;
import com.example.projectgreenie.model.Admin;
import com.example.projectgreenie.security.JwtUtil;
import com.example.projectgreenie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
