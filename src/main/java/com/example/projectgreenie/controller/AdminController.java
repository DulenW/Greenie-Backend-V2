package com.example.projectgreenie.controller;

import com.example.projectgreenie.dto.AdminRegisterDTO;
import com.example.projectgreenie.model.Admin;
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
}
