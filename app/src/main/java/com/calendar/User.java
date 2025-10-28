package com.calendar;

/**
 * User - Data model class representing a system user
 * 
 * This class defines the core user data structure including:
 * - Basic user information (ID, username, email)
 * - Profile details (full name, student ID)
 * - Account metadata (creation timestamp)
 * 
 * Database mapping:
 * - Maps to the 'users' table in the database
 * - Used for user authentication and profile management
 * 
 * Contributors:
 * - Adam Nguyen (adampharrels) - User model design and implementation
 * 
 * Created: September 2023
 * Last Updated: October 2025
 */

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String email;
    private String fullName;
    private String studentId;
    private Timestamp createdAt;

    public User(int userId, String username, String email, String fullName, String studentId, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.studentId = studentId;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}