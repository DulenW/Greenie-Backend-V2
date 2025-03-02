package com.example.projectgreenie.repository;

import com.example.projectgreenie.entity.FeedPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedPostRepository extends MongoRepository<FeedPost, String> {
}
