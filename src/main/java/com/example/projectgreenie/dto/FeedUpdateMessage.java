package com.example.projectgreenie.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedUpdateMessage {
    private String type; // "post", "comment"
    private Object payload;
}
