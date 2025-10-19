package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import uni.space.finder.DatabaseSetup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/availability")
public class AvailabilityServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        String action = req.getParameter("action");
        String roomId = req.getParameter("roomId");
        String startTime = req.getParameter("startTime");
        String endTime = req.getParameter("endTime");
        
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = DatabaseSetup.getConnection()) {
            
            if ("block".equals(action)) {
                // Block availability by adding a booking
                String sql = "INSERT INTO booktime (room_id, start_Time, end_Time) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, Integer.parseInt(roomId));
                pstmt.setString(2, startTime);
                pstmt.setString(3, endTime);
                
                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    ResultSet keys = pstmt.getGeneratedKeys();
                    if (keys.next()) {
                        result.put("success", true);
                        result.put("bookingId", keys.getInt(1));
                        result.put("message", "Time slot blocked successfully");
                    }
                }
                
                pstmt.close();
                
            } else if ("unblock".equals(action)) {
                // Unblock availability by removing a booking
                String bookingId = req.getParameter("bookingId");
                String sql = "DELETE FROM booktime WHERE timeID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(bookingId));
                
                int rows = pstmt.executeUpdate();
                result.put("success", rows > 0);
                result.put("message", rows > 0 ? "Time slot unblocked" : "Booking not found");
                
                pstmt.close();
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        resp.getWriter().write(gson.toJson(result));
    }
}