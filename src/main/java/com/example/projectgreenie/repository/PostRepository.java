package com.example.projectgreenie.repository;

import com.example.projectgreenie.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {


}
