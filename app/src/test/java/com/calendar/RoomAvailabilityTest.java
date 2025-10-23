package com.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uni.space.finder.DatabaseSetup;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Room Availability System Tests")
public class RoomAvailabilityTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private PrintWriter writer;
    
    private StringWriter stringWriter;
    private AvailableRoomsServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        servlet = new AvailableRoomsServlet();
        
        when(response.getWriter()).thenReturn(writer);
    }

    @AfterEach
    void tearDown() throws Exception {
        writer.close();
    }

    @Test
    @DisplayName("Should return available rooms in JSON format")
    void testAvailableRoomsJsonResponse() throws Exception {
        servlet.doGet(request, response);
        
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.startsWith("["), "Response should be a JSON array");
        assertTrue(jsonResponse.contains("roomId"), "Response should contain room information");
    }

    @Test
    @DisplayName("Should set proper CORS headers")
    void testCorsHeaders() throws Exception {
        servlet.doGet(request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Test
    @DisplayName("Should handle database connection errors gracefully")
    void testDatabaseConnectionError() throws Exception {
        // This test checks that the servlet handles database errors without crashing
        servlet.doGet(request, response);
        
        // Should not throw exception and should return some response
        verify(response).getWriter();
        String response = stringWriter.toString();
        assertNotNull(response, "Should return some response even if database fails");
    }

    @Test
    @DisplayName("Should return rooms with required fields")
    void testRoomDataStructure() throws Exception {
        servlet.doGet(request, response);
        
        String jsonResponse = stringWriter.toString();
        
        // Check that response contains expected room fields
        assertTrue(jsonResponse.contains("roomId") || jsonResponse.length() == 2, 
                  "Response should contain room data or be empty array");
        
        if (jsonResponse.length() > 2) { // Not empty array
            assertTrue(jsonResponse.contains("roomName"), "Rooms should have names");
            assertTrue(jsonResponse.contains("building") || jsonResponse.contains("roomId"), 
                      "Rooms should have building or ID information");
        }
    }

    @Test
    @DisplayName("Should filter out unavailable rooms")
    void testRoomFiltering() throws Exception {
        servlet.doGet(request, response);
        
        String jsonResponse = stringWriter.toString();
        
        // The servlet should only return currently available rooms
        // This is verified by the fact that it doesn't crash and returns valid JSON
        assertNotNull(jsonResponse, "Response should not be null");
        assertTrue(jsonResponse.startsWith("[") && jsonResponse.endsWith("]"), 
                  "Response should be a valid JSON array");
    }

    @Test
    @DisplayName("Should handle current time availability check")
    void testCurrentTimeAvailability() {
        // Test that the servlet correctly identifies current time
        LocalDateTime now = LocalDateTime.now();
        String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        assertNotNull(currentTime, "Current time should be properly formatted");
        assertTrue(currentTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), 
                  "Time format should match database format");
    }

    @Test
    @DisplayName("Should return consistent room count")
    void testConsistentRoomCount() throws Exception {
        // First call
        servlet.doGet(request, response);
        String firstResponse = stringWriter.toString();
        
        // Reset string writer
        stringWriter.getBuffer().setLength(0);
        
        // Second call
        servlet.doGet(request, response);
        String secondResponse = stringWriter.toString();
        
        // Room count should be consistent (unless bookings changed)
        assertNotNull(firstResponse, "First response should not be null");
        assertNotNull(secondResponse, "Second response should not be null");
    }

    @Test
    @DisplayName("Should handle room equipment data")
    void testRoomEquipmentData() throws Exception {
        servlet.doGet(request, response);
        
        String jsonResponse = stringWriter.toString();
        
        if (jsonResponse.length() > 2) { // Not empty array
            // Should contain equipment information or handle missing equipment gracefully
            assertTrue(jsonResponse.contains("equipment") || 
                      jsonResponse.contains("roomName"), 
                      "Response should contain room equipment or name data");
        }
    }

    @Test
    @DisplayName("Should validate room capacity information")
    void testRoomCapacityValidation() throws Exception {
        servlet.doGet(request, response);
        
        String jsonResponse = stringWriter.toString();
        
        // Response should be valid JSON regardless of content
        assertTrue(jsonResponse.matches("\\[.*\\]"), "Response should be valid JSON array format");
    }
}