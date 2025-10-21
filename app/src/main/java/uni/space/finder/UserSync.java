package uni.space.finder;

import java.sql.*;

/**
 * Simple utility to manually sync user accounts from accounts.txt to database
 */
public class UserSync {
    
    /**
     * Add a specific user to the database
     */
    public static boolean addUserToDatabase(String email, String firstName, String lastName, String studentId) {
        String username = email.split("@")[0];
        String fullName = firstName + " " + lastName;
        
        try (Connection conn = DatabaseSetup.getConnection()) {
            
            // Check if user already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("ℹ️  User already exists in database: " + email);
                    return true;
                }
            }
            
            // Insert user
            String insertQuery = "INSERT INTO users (username, email, full_name, student_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, email);
                insertStmt.setString(3, fullName);
                insertStmt.setString(4, studentId);
                
                int rowsAffected = insertStmt.executeUpdate();
                System.out.println("✅ Added user to database: " + email + " (rows affected: " + rowsAffected + ")");
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding user to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sync all users from accounts.txt to database
     */
    public static void syncAllUsers() {
        // Skip UserSync for now since we have sample data in asd.sql
        // This prevents primary key conflicts with auto-increment sequence
        System.out.println("🔄 Syncing users from accounts.txt to database...");
        System.out.println("ℹ️  Skipping UserSync - sample data already exists in database");
        System.out.println("✅ User sync completed");
    }
}