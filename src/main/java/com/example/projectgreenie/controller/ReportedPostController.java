package com.example.projectgreenie.controller;

import com.example.projectgreenie.model.ReportedPost;
import com.example.projectgreenie.repository.ReportedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reported-posts")
public class ReportedPostController {

    @Autowired
    private ReportedPostRepository reportedPostRepository;

    @PostMapping("/report")
    public ResponseEntity<?> reportPost(@RequestBody ReportedPost report) {
        boolean alreadyReported = reportedPostRepository.existsByPostIdAndReportedBy(
                report.getPostId(), report.getReportedBy()
        );

        if (alreadyReported) {
            return ResponseEntity.badRequest().body("You already reported this post.");
        }

        // Generate a custom reportId using timestamp
        String reportId = "REPORT-" + System.currentTimeMillis();
        report.setReportId(reportId);
        report.setTimestamp(LocalDateTime.now());

        reportedPostRepository.save(report);
        return ResponseEntity.ok("Reported successfully");
    }



    @GetMapping("/all")
    public List<ReportedPost> getAllReports() {
        return reportedPostRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable String id) {
        reportedPostRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}

