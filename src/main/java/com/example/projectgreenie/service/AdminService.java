package com.example.projectgreenie.service;

import com.example.projectgreenie.dto.AdminRegisterDTO;
import com.example.projectgreenie.model.Admin;
import com.example.projectgreenie.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
