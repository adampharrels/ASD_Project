package com.calendar;

/**
 * BookingVerificationServlet - Handles booking verification and status checks
 * 
 * This servlet provides booking verification functionality including:
 * - Retrieving and validating user-specific bookings
 * - Displaying system-wide booking activity
 * - Verifying booking authenticity and status
 * 
 * Database tables used:
 * - booktime: Primary booking records
 * - room: Room details
 * - users: User information
 * 
 * Features:
 * - User-specific booking verification
 * - System-wide booking activity monitoring
 * - Session-based authentication
 * - Detailed booking status reporting
 * 
 * Contributors:
 * - Nathan (clive897) - Initial booking verification system
 * - Adam Nguyen (adampharrels) - Fixes and confirmation page integration
 * 
 * Created: September 2023
 * Last Updated: October 2025
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uni.space.finder.DatabaseSetup;
import com.google.gson.Gson;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/verify-bookings")
public class BookingVerificationServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        
        try {
            // Check if user is logged in
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                resp.getWriter().write(gson.toJson(errorResponse));
                return;
            }
            
            String userEmail = (String) session.getAttribute("email");
            System.out.println("🔍 Verifying bookings for user: " + userEmail);
            
            // Get user's bookings from database
            List<Map<String, Object>> bookings = getUserBookingsFromDatabase(userEmail);
            
            // Also get all bookings to show system-wide activity
            List<Map<String, Object>> allBookings = getAllBookingsFromDatabase();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userEmail", userEmail);
            response.put("userBookings", bookings);
            response.put("userBookingCount", bookings.size());
            response.put("allBookings", allBookings);
            response.put("totalBookingCount", allBookings.size());
            response.put("message", "Found " + bookings.size() + " bookings for user and " + allBookings.size() + " total bookings in database");
            
            System.out.println("✅ Verification complete: " + bookings.size() + " user bookings, " + allBookings.size() + " total bookings");
            resp.getWriter().write(gson.toJson(response));
            
        } catch (Exception e) {
            System.err.println("❌ Error verifying bookings: " + e.getMessage());
            e.printStackTrace();
            
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to verify bookings: " + e.getMessage());
            resp.getWriter().write(gson.toJson(errorResponse));
        }
    }
    
    private List<Map<String, Object>> getUserBookingsFromDatabase(String userEmail) throws SQLException {
        List<Map<String, Object>> bookings = new ArrayList<>();
        
        String query = "SELECT b.timeid, b.booking_ref, b.start_time, b.end_time, b.booking_status, " +
                      "r.room_name, u.email, u.full_name " +
                      "FROM booktime b " +
                      "JOIN room r ON b.room_id = r.room_id " +
                      "JOIN users u ON b.user_id = u.user_id " +
                      "WHERE u.email = ? " +
                      "ORDER BY b.start_time DESC";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> booking = new HashMap<>();
                booking.put("bookingId", rs.getInt("timeid"));
                booking.put("bookingRef", rs.getString("booking_ref"));
                booking.put("roomName", rs.getString("room_name"));
                booking.put("startTime", rs.getString("start_time"));
                booking.put("endTime", rs.getString("end_time"));
                booking.put("status", rs.getString("booking_status"));
                booking.put("userEmail", rs.getString("email"));
                booking.put("userName", rs.getString("full_name"));
                bookings.add(booking);
            }
        }
        
        return bookings;
    }
    
    private List<Map<String, Object>> getAllBookingsFromDatabase() throws SQLException {
        List<Map<String, Object>> bookings = new ArrayList<>();
        
        String query = "SELECT b.timeid, b.booking_ref, b.start_time, b.end_time, b.booking_status, " +
                      "r.room_name, u.email, u.full_name " +
                      "FROM booktime b " +
                      "JOIN room r ON b.room_id = r.room_id " +
                      "JOIN users u ON b.user_id = u.user_id " +
                      "ORDER BY b.start_time DESC " +
                      "LIMIT 20";  // Limit to recent bookings
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> booking = new HashMap<>();
                booking.put("bookingId", rs.getInt("timeid"));
                booking.put("bookingRef", rs.getString("booking_ref"));
                booking.put("roomName", rs.getString("room_name"));
                booking.put("startTime", rs.getString("start_time"));
                booking.put("endTime", rs.getString("end_time"));
                booking.put("status", rs.getString("booking_status"));
                booking.put("userEmail", rs.getString("email"));
                booking.put("userName", rs.getString("full_name"));
                bookings.add(booking);
            }
        }
        
        return bookings;
    }
}