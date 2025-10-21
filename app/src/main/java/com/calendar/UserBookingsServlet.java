package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uni.space.finder.DatabaseSetup;
import com.google.gson.Gson;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/user-bookings")
public class UserBookingsServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            // For now, use default user (in real app, get from session/authentication)
            int currentUserId = 1; // Default to Adam Pharrels
            String status = req.getParameter("status"); // "current", "past", or "all"
            
            List<Booking> bookings = getUserBookings(currentUserId, status);
            resp.getWriter().write(gson.toJson(bookings));
            System.out.println("✅ Fetched " + bookings.size() + " bookings for user " + currentUserId);
            
        } catch (Exception e) {
            System.err.println("❌ Error in UserBookingsServlet: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private List<Booking> getUserBookings(int userId, String status) {
        List<Booking> bookings = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        
        query.append("SELECT b.timeID, b.booking_ref, b.room_id, b.user_id, b.start_Time, b.end_Time, b.booking_status, ");
        query.append("b.created_at, b.cancelled_at, r.room_name, r.room_type, r.capacity, ");
        query.append("r.speaker, r.whiteboard, r.monitor, r.hdmi_cable, r.image, u.full_name ");
        query.append("FROM booktime b ");
        query.append("JOIN room r ON b.room_id = r.room_id ");
        query.append("JOIN users u ON b.user_id = u.user_id ");
        query.append("WHERE b.user_id = ? ");
        
        // Add status filter
        if ("current".equals(status)) {
            query.append("AND b.booking_status = 'ACTIVE' AND b.start_Time >= CURRENT_TIMESTAMP ");
        } else if ("past".equals(status)) {
            query.append("AND (b.booking_status = 'COMPLETED' OR b.end_Time < CURRENT_TIMESTAMP) ");
        }
        
        query.append("ORDER BY b.start_Time DESC");
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("timeID"), // bookingID
                    rs.getInt("timeID"), // timeID
                    rs.getString("booking_ref"), // memorable booking reference
                    rs.getInt("room_id"),
                    rs.getInt("user_id"),
                    rs.getString("start_Time"),
                    rs.getString("end_Time"),
                    rs.getString("room_name"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getBoolean("speaker"),
                    rs.getBoolean("whiteboard"),
                    rs.getBoolean("monitor"),
                    rs.getBoolean("hdmi_cable"),
                    rs.getString("image"),
                    rs.getString("booking_status"),
                    rs.getString("created_at"),
                    rs.getString("cancelled_at"),
                    rs.getString("full_name")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user bookings: " + e.getMessage());
        }
        
        return bookings;
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            String bookingIdStr = req.getParameter("bookingId");
            if (bookingIdStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing booking ID\"}");
                return;
            }
            
            int bookingId = Integer.parseInt(bookingIdStr);
            int currentUserId = 1; // Default user
            
            boolean success = cancelBooking(bookingId, currentUserId);
            
            if (success) {
                resp.getWriter().write("{\"success\":true,\"message\":\"Booking cancelled successfully\"}");
                System.out.println("✅ Cancelled booking ID: " + bookingId);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Booking not found or cannot be cancelled\"}");
            }
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid booking ID format\"}");
        } catch (Exception e) {
            System.err.println("❌ Error cancelling booking: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }
    
    private boolean cancelBooking(int bookingId, int userId) {
        String query = "UPDATE booktime SET booking_status = 'CANCELLED', cancelled_at = CURRENT_TIMESTAMP " +
                      "WHERE timeID = ? AND user_id = ? AND booking_status = 'ACTIVE'";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            return false;
        }
    }
}