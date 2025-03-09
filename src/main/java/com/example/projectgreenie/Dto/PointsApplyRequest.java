package com.example.projectgreenie.dto;

import lombok.Data;

@Data
public class PointsApplyRequest {
    private String userId;
    private double cartTotal; // renamed from totalAmount for clarity
    private int pointsToRedeem;
}
