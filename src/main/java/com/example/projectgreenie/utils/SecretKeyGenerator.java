package com.example.projectgreenie.utils;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256 bits = 32 bytes
        secureRandom.nextBytes(keyBytes);

        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Generated Secret Key: " + base64Key);
    }
}
