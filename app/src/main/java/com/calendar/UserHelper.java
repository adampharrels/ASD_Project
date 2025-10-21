package com.calendar;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import uni.space.finder.DatabaseSetup;
import java.sql.*;

/**
 * Helper class to get the current logged-in user's database ID
 */
public class UserHelper {
    
    /**
     * Get current user ID from session and database
     */
    public static int getCurrentUserId(HttpServletRequest req) {
        try {
            // Get user email from session
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                System.err.println("❌ No session or email found");
                return -1;
            }
            
            String email = (String) session.getAttribute("email");
            System.out.println("🔍 Looking up user ID for email: " + email);
            
            // Get user ID from database
            String query = "SELECT user_id FROM users WHERE email = ?";
            
            try (Connection conn = DatabaseSetup.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    System.out.println("✅ Found user ID: " + userId + " for email: " + email);
                    return userId;
                } else {
                    System.err.println("❌ User not found in database: " + email);
                    // Try to create user in database from session info
                    return createUserFromSession(email);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting current user ID: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Create user in database from session email
     */
    private static int createUserFromSession(String email) {
        try {
            String username = email.split("@")[0];
            String fullName = "User"; // Default name
            
            String insertQuery = "INSERT IGNORE INTO users (username, email, full_name, student_id) VALUES (?, ?, ?, ?)";
            String selectQuery = "SELECT user_id FROM users WHERE email = ?";
            
            try (Connection conn = DatabaseSetup.getConnection()) {
                // Insert user
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, email);
                    insertStmt.setString(3, fullName);
                    insertStmt.setString(4, "temp"); // temporary student ID
                    
                    int rowsAffected = insertStmt.executeUpdate();
                    System.out.println("🆕 Created user in database: " + email + " (rows: " + rowsAffected + ")");
                }
                
                // Get the user ID
                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                    selectStmt.setString(1, email);
                    ResultSet rs = selectStmt.executeQuery();
                    
                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        System.out.println("✅ Retrieved new user ID: " + userId + " for email: " + email);
                        return userId;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating user from session: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
}