package uni.space.finder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.gson.Gson;
import uni.space.finder.DatabaseSetup;
import uni.space.finder.UserSync;

@WebServlet("/api/signup")
public class SignupServlet extends HttpServlet {
    // Add CORS headers to all responses
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        BufferedReader reader = req.getReader();
        Gson gson = new Gson();
        SignupData data = gson.fromJson(reader, SignupData.class);
        reader.close();

        // Basic validation
        if (data == null || data.email == null || data.password == null || !data.email.endsWith("@student.uts.edu.au")) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Invalid input\"}");
            return;
        }

        boolean created = Account.createAccount(data.email, data.password, data.first, data.last, data.sid);
        if (created) {
            // Also add user to database for booking system
            try {
                addUserToDatabase(data.email, data.first, data.last, data.sid);
                resp.getWriter().write("{\"success\":true}");
            } catch (Exception e) {
                System.err.println("Database sync failed: " + e.getMessage());
                resp.getWriter().write("{\"success\":true,\"message\":\"Account created successfully\"}");
            }
        } else {
            resp.getWriter().write("{\"success\":false,\"message\":\"Account already exists\"}");
        }
    }



    /**
     * Simple method to add user to database 
     */
    private void addUserToDatabase(String email, String firstName, String lastName, String studentId) throws SQLException {
        String username = email.split("@")[0];
        String fullName = firstName + " " + lastName;
        
        try (Connection conn = DatabaseSetup.getConnection()) {
            String query = "INSERT INTO users (username, email, full_name, student_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, fullName);
                stmt.setString(4, studentId);
                
                int rows = stmt.executeUpdate();
                System.out.println("✅ Added user to database: " + email + " (rows: " + rows + ")");
            }
        }
    }

    private static class SignupData {
        String first;
        String last;
        String email;
        String sid;
        String password;
    }
}
