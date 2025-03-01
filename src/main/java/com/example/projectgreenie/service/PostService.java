package com.example.projectgreenie.service;

import com.example.projectgreenie.entity.Post;
import com.example.projectgreenie.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(String userId, String content, String image) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImage(image);

        return postRepository.save(post);
    }
}
