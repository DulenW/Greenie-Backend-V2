package com.example.projectgreenie;

import com.example.projectgreenie.entity.Post;
import com.example.projectgreenie.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectGreenieApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ProjectGreenieApplication.class, args);
    }

    @Autowired
    private PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        Post post = new Post();
        post.setUserId("user123");
        post.setContent("This is a sample post");
        post.setImage("sample image");

        postRepository.save(post);
        System.out.println("Sample data inserted: " + post);
    }
}
