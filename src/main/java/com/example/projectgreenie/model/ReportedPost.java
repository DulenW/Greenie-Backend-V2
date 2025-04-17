package com.example.projectgreenie.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "reported_posts")
public class ReportedPost {

    @Id
    private String reportId;
    private String postId;
    private String reportedBy;
    private String reason;
    private LocalDateTime timestamp;

}
