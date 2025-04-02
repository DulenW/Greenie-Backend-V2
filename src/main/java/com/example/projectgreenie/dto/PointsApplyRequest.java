package com.example.projectgreenie.dto;

import lombok.Data;

@Data
public class PointsApplyRequest {
    private String userId;
    private double cartTotal;
    private int pointsToRedeem;
}