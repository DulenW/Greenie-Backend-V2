package com.example.projectgreenie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Override
    protected String getDatabaseName() {
        return "greenie_db"; // Change this to your database name in MongoDB Atlas
    }

    @Bean
    public MongoClient mongoClient() {
        String mongoUri = System.getenv("MONGODB_URI");
        return MongoClients.create(mongoUri);
    }

}
