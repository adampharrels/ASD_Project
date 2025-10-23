package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uni.space.finder.DatabaseSetup;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name="BookingServlet", urlPatterns={"/api/booking"})
public class BookingServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final List<Booking> bookings = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger bookingIdCounter = new AtomicInteger(0);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        synchronized (bookings) {
            response.getWriter().write(gson.toJson(bookings));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        try {
            // Read JSON data from request body
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                requestBody.append(line);
            }
            
            // Parse JSON
            JsonObject jsonData = gson.fromJson(requestBody.toString(), JsonObject.class);
            
            // Get data from JSON
            String roomId = jsonData.has("roomId") ? jsonData.get("roomId").getAsString() : null;
            String date = jsonData.has("date") ? jsonData.get("date").getAsString() : null;
            String startTime = jsonData.has("startTime") ? jsonData.get("startTime").getAsString() : null;
            String endTime = jsonData.has("endTime") ? jsonData.get("endTime").getAsString() : null;
            
            // Get current user from session
            int currentUserId = getCurrentUserId(req);
            if (currentUserId == -1) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"User not logged in or not found in database\"}");
                return;
            }
            
            // Debug: Print received parameters
            System.out.println("🔍 Booking request received:");
            System.out.println("  Room ID: " + roomId);
            System.out.println("  Date: " + date);
            System.out.println("  Start Time: " + startTime);
            System.out.println("  End Time: " + endTime);
            
            // Validate input with detailed error messages
            if (roomId == null || roomId.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Room selection is required\"}");
                return;
            }
            if (date == null || date.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Date selection is required\"}");
                return;
            }
            if (startTime == null || startTime.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Start time selection is required\"}");
                return;
            }
            if (endTime == null || endTime.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"End time selection is required\"}");
                return;
            }
            
            // Parse roomId as integer
            int roomIdInt = Integer.parseInt(roomId);
            
            // Format times for database (timestamp format)
            String startDateTimeStr = date + " " + startTime + ":00";
            String endDateTimeStr = date + " " + endTime + ":00";
            
            // Get room name from room ID for booking reference
            String roomName = getRoomNameById(roomIdInt);
            if (roomName == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Room not found: " + roomName + "\"}");
                return;
            }
            
            // Check if room is available
            if (!isRoomAvailable(roomIdInt, startDateTimeStr, endDateTimeStr)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Room is not available at the selected time\"}");
                return;
            }
            
            // Get user info for booking reference generation
            String userFullName = getUserFullName(currentUserId);
            
            // Generate memorable booking reference
            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String bookingRef = BookingIdGenerator.generateBookingRef(roomName, startDateTime, userFullName);
            
            // Create booking in database with memorable reference
            int bookingId = createBookingWithRef(roomIdInt, currentUserId, startDateTimeStr, endDateTimeStr, bookingRef);
            
            if (bookingId > 0) {
                JsonObject response = new JsonObject();
                response.addProperty("success", true);
                response.addProperty("message", "Room booked successfully for " + userFullName + "!");
                response.addProperty("bookingId", bookingId);
                response.addProperty("bookingRef", bookingRef); // Add memorable reference
                response.addProperty("bookingRefDisplay", BookingIdGenerator.formatBookingRefForDisplay(bookingRef));
                response.addProperty("room", roomName);
                response.addProperty("date", date);
                response.addProperty("startTime", startTime);
                response.addProperty("endTime", endTime);
                response.addProperty("userFullName", userFullName);
                
                resp.getWriter().write(gson.toJson(response));
                System.out.println("✅ Room booking created: " + roomName + " on " + startDateTimeStr + " (Ref: " + bookingRef + ") for user: " + userFullName);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\":\"Failed to create booking in database\"}");
            }
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid duration format\"}");
        } catch (Exception e) {
            System.err.println("❌ Error in BookingServlet: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }
    
    private int getRoomIdByName(String roomName) {
        // Extract just the room number part (e.g., "CB06.06.112" from "CB06.06.112 - Group Study Room (8 people)")
        String roomNumber = roomName.split(" - ")[0].trim();
        
        String query = "SELECT room_id FROM room WHERE room_name = ?";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("room_id");
                System.out.println("✅ Found room ID " + id + " for room: " + roomNumber);
                return id;
            } else {
                System.out.println("❌ Room not found in database: " + roomNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error getting room ID: " + e.getMessage());
        }
        
        return -1; // Room not found
    }
    
    private String getRoomNameById(int roomId) {
        String query = "SELECT room_name FROM room WHERE room_id = ?";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String roomName = rs.getString("room_name");
                System.out.println("✅ Found room name " + roomName + " for room ID: " + roomId);
                return roomName;
            } else {
                System.out.println("❌ Room not found in database for ID: " + roomId);
            }
        } catch (SQLException e) {
            System.err.println("Error getting room name: " + e.getMessage());
        }
        
        return null; // Room not found
    }
    
    private boolean isRoomAvailable(int roomId, String startTime, String endTime) {
            String query = "SELECT COUNT(*) FROM booktime WHERE room_id = ? AND booking_status = 'ACTIVE' AND " +
                      "((start_time <= ? AND end_time > ?) OR " +
                      "(start_time < ? AND end_time >= ?) OR " +
                      "(start_time >= ? AND start_time < ?))";        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, roomId);
            pstmt.setString(2, startTime);
            pstmt.setString(3, startTime);
            pstmt.setString(4, endTime);
            pstmt.setString(5, endTime);
            pstmt.setString(6, startTime);
            pstmt.setString(7, endTime);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int conflicts = rs.getInt(1);
                boolean available = conflicts == 0;
                System.out.println("🔍 Room availability check: " + (available ? "Available" : conflicts + " conflicts found"));
                return available;
            }
        } catch (SQLException e) {
            System.err.println("Error checking room availability: " + e.getMessage());
        }
        
        return false; // Assume not available on error
    }
    
    private int createBookingWithRef(int roomId, int userId, String startTime, String endTime, String bookingRef) {
        String query = "INSERT INTO booktime (booking_ref, room_id, user_id, start_time, end_time, booking_status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, bookingRef);
            pstmt.setInt(2, roomId);
            pstmt.setInt(3, userId);
            pstmt.setString(4, startTime);
            pstmt.setString(5, endTime);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    int bookingId = keys.getInt(1);
                    System.out.println("✅ Created booking with memorable reference: " + bookingRef + " (ID: " + bookingId + ") for user: " + userId);
                    return bookingId;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1; // Failed to create booking
    }
    
    private String getUserFullName(int userId) {
        String query = "SELECT full_name FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user full name: " + e.getMessage());
        }
        
        return "Unknown User";
    }
    
    /**
     * Get current user ID from session and database
     */
    private int getCurrentUserId(HttpServletRequest req) {
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
                    // Auto-create user in database if they can log in but aren't in DB
                    return createUserFromSession(req);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting current user ID: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Create user in database from session data (auto-sync for logged-in users)
     */
    private int createUserFromSession(HttpServletRequest req) {
        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                System.err.println("❌ No session found when trying to create user");
                return -1;
            }
            
            String email = (String) session.getAttribute("email");
            String fullName = (String) session.getAttribute("fullName");
            String studentId = (String) session.getAttribute("studentId");
            
            String username = email.split("@")[0];
            
            // Use defaults if not in session
            if (fullName == null || fullName.trim().isEmpty()) {
                String firstName = (String) session.getAttribute("firstName");
                String lastName = (String) session.getAttribute("lastName");
                if (firstName != null && lastName != null) {
                    fullName = firstName + " " + lastName;
                } else {
                    fullName = username; // Use email username as fallback
                }
            }
            
            System.out.println("🆕 Creating user in database: " + email + " (Full name: " + fullName + ", Student ID: " + studentId + ")");
            
            // Insert user into database - handle duplicate student_id gracefully
            String insertQuery = "INSERT INTO users (username, email, full_name, student_id) VALUES (?, ?, ?, ?)";
            String selectQuery = "SELECT user_id FROM users WHERE email = ?";
            String checkStudentIdQuery = "SELECT COUNT(*) FROM users WHERE student_id = ?";
            
            try (Connection conn = DatabaseSetup.getConnection()) {
                // Check if student_id is already taken
                boolean studentIdExists = false;
                if (studentId != null && !studentId.trim().isEmpty()) {
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkStudentIdQuery)) {
                        checkStmt.setString(1, studentId);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            studentIdExists = true;
                            System.out.println("⚠️  Student ID " + studentId + " already exists, using NULL for this user");
                        }
                    }
                }
                
                // Insert user with NULL student_id if it's a duplicate or not available
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, email);
                    insertStmt.setString(3, fullName);
                    if (studentId != null && !studentId.trim().isEmpty() && !studentIdExists) {
                        insertStmt.setString(4, studentId);
                    } else {
                        insertStmt.setNull(4, java.sql.Types.VARCHAR); // Use NULL for duplicate or missing student IDs
                    }
                    
                    int rowsAffected = insertStmt.executeUpdate();
                    System.out.println("✅ Added user to database: " + email + " (rows: " + rowsAffected + ")");
                }
                
                // Get the user ID
                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                    selectStmt.setString(1, email);
                    ResultSet rs = selectStmt.executeQuery();
                    
                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        System.out.println("✅ Retrieved user ID: " + userId + " for email: " + email);
                        return userId;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error auto-creating user: " + e.getMessage());
            
            // Check if it's a unique constraint violation - user might already exist by email or username
            if (e.getMessage() != null && e.getMessage().contains("Unique index")) {
                System.out.println("⚠️  User might already exist, trying to retrieve...");
                try {
                    HttpSession session = req.getSession(false);
                    String email = (String) session.getAttribute("email");
                    String selectQuery = "SELECT user_id FROM users WHERE email = ?";
                    
                    try (Connection conn = DatabaseSetup.getConnection();
                         PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                        selectStmt.setString(1, email);
                        ResultSet rs = selectStmt.executeQuery();
                        
                        if (rs.next()) {
                            int userId = rs.getInt("user_id");
                            System.out.println("✅ Found existing user ID: " + userId + " for " + email);
                            return userId;
                        }
                    }
                } catch (SQLException e2) {
                    System.err.println("❌ Error retrieving existing user: " + e2.getMessage());
                }
            }
        }

        
        return -1;
    }
}


        

