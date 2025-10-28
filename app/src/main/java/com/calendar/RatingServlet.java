package com.calendar;

/**
 * RatingServlet - Manages room booking ratings and feedback
 * 
 * This servlet handles all rating-related operations including:
 * - Creating new ratings for bookings
 * - Retrieving ratings for a specific booking
 * - Managing rating metadata (scores, comments, timestamps)
 * 
 * Database tables used:
 * - ratings: Stores rating records with booking references
 * - booktime: Referenced for booking validation
 * 
 * Key Features:
 * - Rating submission with optional comments
 * - Chronological rating retrieval
 * - Input validation and error handling
 * 
 * Contributors:
 * - Nathan (clive897) - Initial rating system implementation
 * - Adam Nguyen (adampharrels) - Booking ID integration, logging improvements
 * 
 * Created: October 2023
 * Last Updated: October 2025
 */

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.google.gson.Gson;
import uni.space.finder.DatabaseSetup;

@WebServlet(name = "RatingServlet", urlPatterns = {"/api/ratings"})
public class RatingServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String bookingId = req.getParameter("bookingId");
        if (bookingId == null) {
            resp.getWriter().write("[]");
            return;
        }
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, rating, comment, created_at FROM ratings WHERE booking_id = ? ORDER BY created_at DESC")) {
            ps.setLong(1, Long.parseLong(bookingId));
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("rating", rs.getInt("rating"));
                m.put("comment", rs.getString("comment"));
                m.put("createdAt", rs.getTimestamp("created_at").toInstant().toString());
                list.add(m);
            }
            resp.getWriter().write(gson.toJson(list));
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"db_error\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("🔍 RatingServlet: Received POST request");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        try (BufferedReader br = req.getReader()) {
            String requestBody = br.lines().collect(java.util.stream.Collectors.joining());
            System.out.println("📝 Rating request body: " + requestBody);
            
            Map<?, ?> data = gson.fromJson(requestBody, Map.class);
            Number bookingIdN = (Number) data.get("bookingId");
            Number ratingN = (Number) data.get("rating");
            String comment = data.get("comment") == null ? null : data.get("comment").toString();
            
            System.out.println("🔍 Parsed data - bookingId: " + bookingIdN + ", rating: " + ratingN + ", comment: " + comment);
            
            if (bookingIdN == null || ratingN == null) {
                System.err.println("❌ Missing fields - bookingId: " + bookingIdN + ", rating: " + ratingN);
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"missing_fields\"}");
                return;
            }
            long bookingId = bookingIdN.longValue();
            int rating = ratingN.intValue();

            try (Connection conn = DatabaseSetup.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO ratings (booking_id, rating, comment) VALUES (?, ?, ?)")) {
                ps.setLong(1, bookingId);
                ps.setInt(2, rating);
                ps.setString(3, comment);
                int rows = ps.executeUpdate();
                System.out.println("✅ Rating saved successfully! Rows affected: " + rows);
                resp.getWriter().write("{\"success\":true}");
            } catch (SQLException e) {
                System.err.println("❌ Database error saving rating: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("❌ Error in RatingServlet: " + e.getMessage());
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"server_error\",\"message\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}