package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.FeedPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedPostRepository extends MongoRepository<FeedPost, String> {
    boolean existsByPostId(String postId);
    
    @Query("{ 'postId': { $regex: ?0 } }")
    List<FeedPost> findByPostIdStartingWith(String prefix);
}
