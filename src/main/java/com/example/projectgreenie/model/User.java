package com.example.projectgreenie.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;
    private String email;
    private String password;
    private String username;
    private String bio;
    private List<String> postList = new ArrayList<>();
    private String profileImgUrl;
    private int pointsCount;
    private List<String> badgesList = new ArrayList<>();
    private List<String> joinedChallenges = new ArrayList<>();
    private String role; // USER or ADMIN
    private String coverImgUrl; // Add this to the bottom

    public User(String fullName, String email, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.username = "";
        this.bio = "";
        this.postList = new ArrayList<>();
        this.profileImgUrl = "https://yourcdn.com/profiles/default.jpg";
        this.pointsCount = 0;
        this.badgesList = new ArrayList<>();
        this.joinedChallenges = new ArrayList<>();
    }

//    // Getters and Setters
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getBio() {
//        return bio;
//    }
//
//    public void setBio(String bio) {
//        this.bio = bio;
//    }
//
//    public List<String> getPostList() {
//        return postList;
//    }
//
//    public void setPostList(List<String> postList) {
//        this.postList = postList;
//    }
//
//    public String getProfileImgUrl() {
//        return profileImgUrl;
//    }
//
//    public void setProfileImgUrl(String profileImgUrl) {
//        this.profileImgUrl = profileImgUrl;
//    }
//
//    public int getPointsCount() {
//        return pointsCount;
//    }
//
//    public void setPointsCount(int pointsCount) {
//        this.pointsCount = pointsCount;
//    }
//
//    public List<String> getBadgesList() {
//        return badgesList;
//    }
//
//    public void setBadgesList(List<String> badgesList) {
//        this.badgesList = badgesList;
//    }
//
//    public List<String> getJoinedChallenges() {
//        return joinedChallenges;
//    }
//
//    public void setJoinedChallenges(List<String> joinedChallenges) {
//        this.joinedChallenges = joinedChallenges;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
}
