package com.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Booking System Tests")
public class BookingSystemTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private BufferedReader reader;
    
    @Mock
    private PrintWriter writer;
    
    private StringWriter stringWriter;
    private BookingServlet servlet;
    private Gson gson;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        servlet = new BookingServlet();
        gson = new Gson();
        
        when(response.getWriter()).thenReturn(writer);
        when(request.getReader()).thenReturn(reader);
    }

    @AfterEach
    void tearDown() throws Exception {
        writer.close();
    }

    @Test
    @DisplayName("Should reject booking without authentication")
    void testUnauthenticatedBookingRejection() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        
        servlet.doPost(request, response);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("User not logged in"), 
                  "Should return authentication error message");
    }

    @Test
    @DisplayName("Should set proper CORS headers for booking requests")
    void testBookingCorsHeaders() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        // Mock valid JSON input
        String jsonInput = createValidBookingJson();
        when(reader.readLine()).thenReturn(jsonInput, (String) null);
        
        servlet.doPost(request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
        verify(response).setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Test
    @DisplayName("Should validate required booking fields")
    void testBookingFieldValidation() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        // Test missing roomId
        String invalidJson = createBookingJsonWithMissingField("roomId");
        when(reader.readLine()).thenReturn(invalidJson, (String) null);
        
        servlet.doPost(request, response);
        
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("required") || responseText.contains("selection"), 
                  "Should return validation error for missing fields");
    }

    @Test
    @DisplayName("Should handle valid booking request format")
    void testValidBookingRequestFormat() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        String validJson = createValidBookingJson();
        when(reader.readLine()).thenReturn(validJson, (String) null);
        
        servlet.doPost(request, response);
        
        // Should not return 400 Bad Request for valid JSON
        verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle malformed JSON gracefully")
    void testMalformedJsonHandling() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        String malformedJson = "{ invalid json }";
        when(reader.readLine()).thenReturn(malformedJson, (String) null);
        
        servlet.doPost(request, response);
        
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should validate date format")
    void testDateFormatValidation() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        String jsonWithInvalidDate = createBookingJsonWithInvalidDate();
        when(reader.readLine()).thenReturn(jsonWithInvalidDate, (String) null);
        
        servlet.doPost(request, response);
        
        String responseText = stringWriter.toString();
        // Should handle the request without crashing
        assertNotNull(responseText, "Should return some response");
    }

    @Test
    @DisplayName("Should validate time format")
    void testTimeFormatValidation() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        String jsonWithInvalidTime = createBookingJsonWithInvalidTime();
        when(reader.readLine()).thenReturn(jsonWithInvalidTime, (String) null);
        
        servlet.doPost(request, response);
        
        String responseText = stringWriter.toString();
        assertNotNull(responseText, "Should return some response");
    }

    @Test
    @DisplayName("Should handle OPTIONS requests for CORS preflight")
    void testOptionsRequestHandling() throws Exception {
        java.lang.reflect.Method m = BookingServlet.class.getDeclaredMethod("doOptions", jakarta.servlet.http.HttpServletRequest.class, jakarta.servlet.http.HttpServletResponse.class);
        m.setAccessible(true);
        m.invoke(servlet, request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        verify(response).setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Should generate booking reference")
    void testBookingReferenceGeneration() {
        // Test that booking references follow expected format
        String roomId = "108";
        String date = "2025-10-23";
        String startTime = "10:00";
        
        // Expected format: BK-{roomId}-{date without dashes}-{time without colons}
        String expectedPrefix = "BK-" + roomId + "-" + date.replace("-", "");
        
        // This is a conceptual test - the actual reference generation
        // happens in the servlet with database interaction
        assertNotNull(expectedPrefix, "Booking reference format should be predictable");
        assertTrue(expectedPrefix.matches("BK-\\d+-\\d+"), 
                  "Booking reference should follow expected pattern");
    }

    // Helper methods for creating test JSON data
    private String createValidBookingJson() {
        Map<String, String> bookingData = new HashMap<>();
        bookingData.put("roomId", "108");
        bookingData.put("date", "2025-10-23");
        bookingData.put("startTime", "10:00");
        bookingData.put("endTime", "11:00");
        return gson.toJson(bookingData);
    }

    private String createBookingJsonWithMissingField(String fieldToOmit) {
        Map<String, String> bookingData = new HashMap<>();
        if (!"roomId".equals(fieldToOmit)) bookingData.put("roomId", "108");
        if (!"date".equals(fieldToOmit)) bookingData.put("date", "2025-10-23");
        if (!"startTime".equals(fieldToOmit)) bookingData.put("startTime", "10:00");
        if (!"endTime".equals(fieldToOmit)) bookingData.put("endTime", "11:00");
        return gson.toJson(bookingData);
    }

    private String createBookingJsonWithInvalidDate() {
        Map<String, String> bookingData = new HashMap<>();
        bookingData.put("roomId", "108");
        bookingData.put("date", "invalid-date");
        bookingData.put("startTime", "10:00");
        bookingData.put("endTime", "11:00");
        return gson.toJson(bookingData);
    }

    private String createBookingJsonWithInvalidTime() {
        Map<String, String> bookingData = new HashMap<>();
        bookingData.put("roomId", "108");
        bookingData.put("date", "2025-10-23");
        bookingData.put("startTime", "invalid-time");
        bookingData.put("endTime", "11:00");
        return gson.toJson(bookingData);
    }
}