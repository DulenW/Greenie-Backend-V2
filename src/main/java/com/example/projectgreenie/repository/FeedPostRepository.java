package com.example.projectgreenie.repository;

import com.example.projectgreenie.model.FeedPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedPostRepository extends MongoRepository<FeedPost, String> {
    Optional<FeedPost> findByPostId(String postId);

    boolean existsByPostId(String postId);
    
    @Query("{ 'postId': { $regex: ?0 } }")
    List<FeedPost> findByPostIdStartingWith(String prefix);

}
