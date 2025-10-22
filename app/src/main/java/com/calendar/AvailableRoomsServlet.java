package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import uni.space.finder.DatabaseSetup;

@WebServlet("/api/available-rooms")
public class AvailableRoomsServlet extends HttpServlet {
    private Gson gson = new Gson();
    private static boolean databaseInitialized = false;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Ensure database is initialized
        if (!databaseInitialized) {
            System.out.println("🔄 Initializing database for AvailableRoomsServlet...");
            DatabaseSetup.initDatabase();
            databaseInitialized = true;
        }
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            List<Map<String, Object>> rooms = getAllAvailableRooms();
            
            Gson gson = new Gson();
            String json = gson.toJson(rooms);
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(json);
            
            System.out.println("✅ Found " + rooms.size() + " rooms available right now");
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching available rooms: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private List<Map<String, Object>> getAllAvailableRooms() {
        List<Map<String, Object>> availableRooms = new ArrayList<>();
        
        // Get current time only (no time window restriction)
        LocalDateTime now = LocalDateTime.now();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = now.format(formatter);
        
        System.out.println("🔍 Checking all rooms available right now: " + currentTime);
        
        // First, let's check what rooms exist in total
        String countQuery = "SELECT COUNT(*) as total FROM room";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countQuery);
             ResultSet countRs = countStmt.executeQuery()) {
            if (countRs.next()) {
                System.out.println("📊 Total rooms in database: " + countRs.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("Error counting rooms: " + e.getMessage());
        }
        
        // Check what bookings exist right now
        String bookingQuery = "SELECT * FROM booktime WHERE start_Time <= ? AND end_Time > ?";
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery)) {
            bookingStmt.setString(1, currentTime);
            bookingStmt.setString(2, currentTime);
            try (ResultSet bookingRs = bookingStmt.executeQuery()) {
                int bookingCount = 0;
                while (bookingRs.next()) {
                    bookingCount++;
                    System.out.println("📅 Current booking: Room " + bookingRs.getInt("room_id") + 
                                     " from " + bookingRs.getString("start_Time") + 
                                     " to " + bookingRs.getString("end_Time"));
                }
                System.out.println("📊 Found " + bookingCount + " active bookings right now");
            }
        } catch (SQLException e) {
            System.err.println("Error checking bookings: " + e.getMessage());
        }
        
        // Query to find rooms that are NOT currently booked
        String query = """
            SELECT DISTINCT r.room_id, r.room_name, r.room_type, r.capacity, 
                   r.speaker, r.whiteboard, r.monitor, r.hdmi_cable, r.image
            FROM room r 
            WHERE r.room_id NOT IN (
                SELECT DISTINCT b.room_id 
                FROM booktime b 
                WHERE b.start_Time <= ? AND b.end_Time > ?
            )
            ORDER BY r.room_name
            """;
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Set parameters for current time check
            pstmt.setString(1, currentTime);     // booking starts before or at current time
            pstmt.setString(2, currentTime);     // but ends after current time
            
            System.out.println("🔍 Executing availability query for current time:");
            System.out.println("  Current time: " + currentTime);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                int roomCount = 0;
                while (rs.next()) {
                    roomCount++;
                    Map<String, Object> room = new HashMap<>();
                    room.put("roomId", rs.getInt("room_id"));
                    room.put("roomName", rs.getString("room_name"));
                    room.put("roomType", rs.getString("room_type"));
                    room.put("capacity", rs.getInt("capacity"));
                    room.put("speaker", rs.getBoolean("speaker"));
                    room.put("whiteboard", rs.getBoolean("whiteboard"));
                    room.put("monitor", rs.getBoolean("monitor"));
                    room.put("hdmiCable", rs.getBoolean("hdmi_cable"));
                    room.put("image", rs.getString("image"));
                    
                    // Add computed fields
                    room.put("location", getLocationFromRoomName(rs.getString("room_name")));
                    room.put("equipment", getEquipmentList(rs));
                    room.put("availableFor", "Available now"); // Since they're available right now
                    room.put("rating", 4.9); // Default rating
                    
                    availableRooms.add(room);
                    System.out.println("✅ Available room: " + rs.getString("room_name"));
                }
                System.out.println("📊 Query returned " + roomCount + " available rooms");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching available rooms: " + e.getMessage());
            e.printStackTrace();
        }
        
        // If no rooms found with complex query, try a simple fallback
        if (availableRooms.isEmpty()) {
            System.out.println("🔄 No rooms found with availability query, trying fallback...");
            String fallbackQuery = "SELECT * FROM room LIMIT 5"; // Show some rooms as fallback
            try (Connection conn = DatabaseSetup.getConnection();
                 PreparedStatement fallbackStmt = conn.prepareStatement(fallbackQuery);
                 ResultSet rs = fallbackStmt.executeQuery()) {
                
                while (rs.next()) {
                    Map<String, Object> room = new HashMap<>();
                    room.put("roomId", rs.getInt("room_id"));
                    room.put("roomName", rs.getString("room_name"));
                    room.put("roomType", rs.getString("room_type"));
                    room.put("capacity", rs.getInt("capacity"));
                    room.put("speaker", rs.getBoolean("speaker"));
                    room.put("whiteboard", rs.getBoolean("whiteboard"));
                    room.put("monitor", rs.getBoolean("monitor"));
                    room.put("hdmiCable", rs.getBoolean("hdmi_cable"));
                    room.put("image", rs.getString("image"));
                    
                    room.put("location", getLocationFromRoomName(rs.getString("room_name")));
                    room.put("equipment", getEquipmentList(rs));
                    room.put("availableFor", "Available (fallback)");
                    room.put("rating", 4.9);
                    
                    availableRooms.add(room);
                    System.out.println("🔄 Fallback room: " + rs.getString("room_name"));
                }
            } catch (SQLException e) {
                System.err.println("❌ Error in fallback query: " + e.getMessage());
            }
        }
        
        return availableRooms;
    }
    
    private String getLocationFromRoomName(String roomName) {
        if (roomName != null && roomName.length() >= 2) {
            // Extract building code from room name (e.g., "CB06.06.112" -> "CB Building")
            String buildingCode = roomName.substring(0, 2);
            return buildingCode + " Building";
        }
        return "Unknown Location";
    }
    
    private List<String> getEquipmentList(ResultSet rs) throws SQLException {
        List<String> equipment = new ArrayList<>();
        
        if (rs.getBoolean("speaker")) equipment.add("Speaker");
        if (rs.getBoolean("whiteboard")) equipment.add("Whiteboard");
        if (rs.getBoolean("monitor")) equipment.add("Monitor");
        if (rs.getBoolean("hdmi_cable")) equipment.add("HDMI Cable");
        
        // Add some default equipment for better display
        if (equipment.isEmpty()) {
            equipment.add("Basic Equipment");
        }
        
        return equipment;
    }
}