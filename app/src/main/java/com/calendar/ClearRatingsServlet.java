package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.google.gson.Gson;
import uni.space.finder.DatabaseSetup;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet to clear all ratings from the database.
 * This is useful for testing and resetting the rating system.
 * Endpoint: DELETE /api/ratings/clear
 */
@WebServlet("/api/ratings/clear")
public class ClearRatingsServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // Add CORS headers
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int deletedCount = clearAllRatings();
            
            response.put("success", true);
            response.put("message", "All ratings cleared successfully");
            response.put("deletedCount", deletedCount);
            
            System.out.println("✅ Cleared " + deletedCount + " ratings from database");
            
        } catch (Exception e) {
            System.err.println("❌ Error clearing ratings: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "Failed to clear ratings: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Handle CORS preflight
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * Delete all ratings from the database.
     * @return The number of ratings deleted
     */
    private int clearAllRatings() throws SQLException {
        String deleteQuery = "DELETE FROM ratings";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            
            int deletedCount = pstmt.executeUpdate();
            return deletedCount;
            
        } catch (SQLException e) {
            System.err.println("❌ Database error while clearing ratings: " + e.getMessage());
            throw e;
        }
    }
}
