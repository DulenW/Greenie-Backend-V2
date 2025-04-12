package com.example.projectgreenie.config;

import com.example.projectgreenie.security.JwtAuthenticationFilter;
import com.example.projectgreenie.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;

    public WebSecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/reset-password",
                                "/api/auth/set-new-password",

                                "/api/posts/create",
                                "/api/posts/all",
                                "/api/posts/{postId}/like",
                                "/api/posts/user-details/{userId}",


                                "/api/test/broadcast",
                                "/ws-feed/**",        // ✅ allow SockJS/WebSocket handshake endpoint
                                "/topic/**",          // ✅ allow message broker topics


                                // Comments and Likes in Posts
                                "/api/posts/{postId}/comments/create",
                                "/api/posts/{postId}/comments/all",
                                "/api/posts/{postId}/likes/all",
                                "/api/posts/{postId}/comments/count",
                                "/api/posts/{postId}/unlike",
                                "/api/posts/{postId}/{commentId}/comments/delete",
                                "/api/posts/user-details/{userId}",
                                "/api/posts/{postId}/react",
                                "/api/posts/{postId}/reactions",
                                "/api/posts/{postId}/likes/count",

                                // Admin endpoints
                                "/api/admin/register",
                                "/api/admin/login",
                                "/api/admin/all",
                                "/api/admin/{adminId}",
                                "/api/admin/delete/{adminId}"
                        ).permitAll()
                        .requestMatchers("/api/posts/**").authenticated()
                        .requestMatchers(
                                "/api/users/{id}",
                                "/api/users/{userId}/points",
                                "/api/order/apply-points",
                                "/api/order/place",
                                "/api/users/all",

                                // Shop endpoints
                                "/api/products/**",
                                "/api/cart/**",
                                "/shop/**",

                                "/api/leaderboard",
                                "/api/order/all",
                                "/api/order/{orderId}"
                        ).permitAll()

                        // USER: Challenges API
                        .requestMatchers("/api/challenges/create").authenticated()
                        .requestMatchers("/api/challenges/all").permitAll()
                        .requestMatchers("/api/challenges/{challengeId}").permitAll()
                        .requestMatchers("/api/challenges/status/{status}").permitAll()
                        .requestMatchers("/api/challenges/**").permitAll() // Fallback for other GETs

                        // ADMIN: Challenges API
                        .requestMatchers("/api/admin/challenges/create").authenticated()
                        .requestMatchers("/api/admin/challenges/all").permitAll()
                        .requestMatchers("/api/admin/challenges/{challengeId}").permitAll()
                        .requestMatchers("/api/admin/challenges/approve/{challengeId}").permitAll()
                        .requestMatchers("/api/admin/challenges/status/{status}").permitAll()
                        .requestMatchers("/api/admin/challenges/delete/{challengeId}").permitAll()
                        .requestMatchers("/api/admin/challenges/**").permitAll() // fallback

                        // Proof API
                        .requestMatchers("/api/proof/").authenticated()
                        .requestMatchers("/admin/proof/all", "/admin/proof/{proofID}").permitAll()
                        .requestMatchers("/api/proof/submit/{id}", "/api/proof/all", "/api/proof/{id}").permitAll()

                        // Feed Post
                        .requestMatchers("/api/posts").permitAll()
                        .requestMatchers("/api/posts/{postId}/like").permitAll()
                        .requestMatchers("/api/posts/{postId}/comments/create").permitAll()
                        .requestMatchers("/api/posts/{postId}/comments/all").permitAll()
                        .requestMatchers("/api/posts/{postId}/likes/all").permitAll()
                        .requestMatchers("/api/posts/{postId}/like").permitAll()
                        .requestMatchers("/api/posts/{postId}/comments/count").permitAll()
                        .requestMatchers("/api/posts/{postId}/unlike").permitAll()
                        .requestMatchers("/api/posts/{postId}/{commentId}/comments/delete").permitAll()
                        .requestMatchers("/api/posts/user-details/{userId}").permitAll()
                        .requestMatchers("/api/test/broadcast").permitAll()


                        // Add admin specific security
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:5174"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
