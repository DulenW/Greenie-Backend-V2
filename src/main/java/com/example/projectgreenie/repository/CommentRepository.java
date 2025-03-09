package com.example.projectgreenie.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import javax.xml.stream.events.Comment;
import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    @Query("{ 'postId' : ?0 }")
    List<Comment> findByPostId(String postId);
}