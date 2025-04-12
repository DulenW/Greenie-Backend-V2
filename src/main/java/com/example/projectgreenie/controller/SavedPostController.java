package com.example.projectgreenie.controller;

import com.example.projectgreenie.service.SavedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-posts")
public class SavedPostController {

    @Autowired
    private SavedPostService savedPostService;

    @PostMapping("/save")
    public ResponseEntity<?> savePost(@RequestBody SavedPostRequest request) {
        savedPostService.savePost(request.getPostId(), request.getUserId());
        return ResponseEntity.ok("Post saved successfully");
    }

    @DeleteMapping("/unsave")
    public ResponseEntity<?> unsavePost(@RequestBody SavedPostRequest request) {
        savedPostService.unsavePost(request.getPostId(), request.getUserId());
        return ResponseEntity.ok("Post unsaved successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<String>> getSavedPosts(@PathVariable String userId) {
        return ResponseEntity.ok(savedPostService.getSavedPostIds(userId));
    }
}
