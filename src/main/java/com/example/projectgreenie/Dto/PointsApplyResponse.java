package com.example.projectgreenie.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointsApplyResponse {
    private double newTotalAmount;
    private int pointsApplied;
    private int pointsRemaining;
}
