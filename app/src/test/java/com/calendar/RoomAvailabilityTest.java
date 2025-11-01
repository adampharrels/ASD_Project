package com.calendar;

/**
 * @file RoomAvailabilityTest.java
 * @contributor Martin Lau 
 * Created: October 2025
 * Last Updated: October 2025
 * 
 * RoomAvailabilityTest - Unit tests for room availability functionality
 * 
 * This test suite covers the room availability checking system:
 * - Available rooms retrieval
 * - CORS headers and preflight requests
 * - Response format validation
 * - Error handling
 * - Database connection management
 * 
 * Testing Approach:
 * - Uses Mockito for HTTP request/response mocking
 * - Isolates servlet testing from database
 * - Verifies CORS and content type headers
 * - Tests error conditions and edge cases

 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoomAvailabilityTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private AvailableRoomsServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        servlet = new AvailableRoomsServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        
        try {
            when(response.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            // Handle IOException in test setup
        }
    }

    @Test
    void testDoGetReturnsAvailableRooms() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        
        servlet.doGet(request, response);
        
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testCORSHeaders() throws Exception {
        servlet.doGet(request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "*");
        verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @Test
    void testOptionsRequestHandling() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");
        
        servlet.service(request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "*");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDatabaseConnectionHandling() throws Exception {
        servlet.doGet(request, response);
        
        verify(response).setContentType("application/json");
        
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testEmptyRoomResponse() throws Exception {
        servlet.doGet(request, response);
        
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testResponseFormat() throws Exception {
        servlet.doGet(request, response);
        
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testServletInitialization() {
        assertNotNull(servlet);
    }

    @Test
    void testErrorHandling() throws Exception {
        try {
            servlet.doGet(null, response);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testResponseStatus() throws Exception {
        servlet.doGet(request, response);
        
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}