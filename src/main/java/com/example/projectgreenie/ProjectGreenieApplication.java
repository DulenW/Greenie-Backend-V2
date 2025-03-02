package com.example.projectgreenie;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example") // adjust package as needed
public class ProjectGreenieApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ProjectGreenieApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("Project Greenie Application Started!");
    }

}

