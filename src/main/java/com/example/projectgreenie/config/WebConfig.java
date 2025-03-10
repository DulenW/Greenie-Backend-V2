package com.example.projectgreenie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/")
                .allowedOrigins("https://test.greenie.dizzpy.dev","http://localhost:5175") // Frontend URL (React app)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);

    }

//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new LongToLocalDateTimeConverter());
//    }

}