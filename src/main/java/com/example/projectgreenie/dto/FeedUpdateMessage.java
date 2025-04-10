package com.example.projectgreenie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedUpdateMessage {
    private String type; // "post", "comment"
    private Object payload;
}
