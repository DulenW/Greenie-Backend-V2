package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.ReportedPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportedPostRepository extends MongoRepository<ReportedPost, String> {
    boolean existsByPostIdAndReportedBy(String postId, String reportedBy);
}
