package com.calendar;

import java.sql.*;
import java.util.*;
import uni.space.finder.DatabaseSetup;

public class CalendarService {
    
    public CalendarService() {
        // Initialize H2 database when service is created
        DatabaseSetup.initDatabase();
    }

    public boolean isDatabaseConnected() {
        try (Connection conn = DatabaseSetup.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.timeID, b.room_id, b.start_Time, b.end_Time, r.room_name, r.room_type, " +
                      "r.capacity, r.speaker, r.whiteboard, r.monitor, r.hdmi_cable, r.image " +
                      "FROM booktime b JOIN room r ON b.room_id = r.room_id";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("bookingID"),
                    rs.getInt("timeID"),
                    rs.getInt("room_id"),
                    rs.getString("start_Time"),
                    rs.getString("end_Time"),
                    rs.getString("room_name"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getBoolean("speaker"),
                    rs.getBoolean("whiteboard"),
                    rs.getBoolean("monitor"),
                    rs.getBoolean("hdmi_cable"),
                    rs.getString("image")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
        }
        
        return bookings;
    }

    public List<Map<String, Object>> getAllRooms() {
        List<Map<String, Object>> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_name, room_type, capacity, speaker, whiteboard, monitor, hdmi_cable, image FROM room";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
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
                
                // Add location information based on room name
                String location = "Building " + rs.getString("room_name").substring(0, 4); // Extract building code
                room.put("location", location);
                
                // Create equipment list based on available facilities
                List<String> equipment = new ArrayList<>();
                if (rs.getBoolean("speaker")) equipment.add("Speaker System");
                if (rs.getBoolean("whiteboard")) equipment.add("Whiteboard");
                if (rs.getBoolean("monitor")) equipment.add("Monitor/Display");
                if (rs.getBoolean("hdmi_cable")) equipment.add("HDMI Cable");
                
                if (equipment.isEmpty()) {
                    equipment.add("Basic furniture");
                }
                room.put("equipment", equipment);
                
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
        
        return rooms;
    }
}