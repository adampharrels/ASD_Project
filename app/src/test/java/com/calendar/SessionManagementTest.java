package com.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Session Management Tests")
public class SessionManagementTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private PrintWriter writer;
    
    private StringWriter stringWriter;
    private UserSessionServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        servlet = new UserSessionServlet();
        
        when(response.getWriter()).thenReturn(writer);
    }

    @AfterEach
    void tearDown() throws Exception {
        writer.close();
    }

    @Test
    @DisplayName("Should return error when no session exists")
    void testNoSessionHandling() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        
        servlet.doGet(request, response);
        
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("success") && responseText.contains("false"), 
                  "Should return success: false when no session");
        assertTrue(responseText.contains("error"), 
                  "Should return error message when no session");
    }

    @Test
    @DisplayName("Should return error when session has no email")
    void testSessionWithoutEmail() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn(null);
        
        servlet.doGet(request, response);
        
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("success") && responseText.contains("false"), 
                  "Should return success: false when no email in session");
        assertTrue(responseText.contains("User not logged in"), 
                  "Should return appropriate error message");
    }

    @Test
    @DisplayName("Should return user data when valid session exists")
    void testValidSessionHandling() throws Exception {
        String testEmail = "test@example.com";
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn(testEmail);
        when(session.getId()).thenReturn("test-session-id");
        
        servlet.doGet(request, response);
        
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("success") && responseText.contains("true"), 
                  "Should return success: true for valid session");
        assertTrue(responseText.contains(testEmail), 
                  "Should return user email from session");
    }

    @Test
    @DisplayName("Should set proper CORS headers")
    void testSessionCorsHeaders() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        servlet.doGet(request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Test
    @DisplayName("Should extract username from email")
    void testUsernameExtraction() throws Exception {
        String testEmail = "john.doe@example.com";
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn(testEmail);
        
        servlet.doGet(request, response);
        
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("john.doe") || responseText.contains("username"), 
                  "Should extract username from email");
    }

    @Test
    @DisplayName("Should generate display name from email")
    void testDisplayNameGeneration() throws Exception {
        String testEmail = "jane.smith@company.com";
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn(testEmail);
        
        servlet.doGet(request, response);
        
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("fullName"), 
                  "Should generate fullName field");
    }

    @Test
    @DisplayName("Should handle session debugging information")
    void testSessionDebugging() throws Exception {
        String testEmail = "debug@example.com";
        String testSessionId = "debug-session-123";
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn(testEmail);
        when(session.getId()).thenReturn(testSessionId);
        
        servlet.doGet(request, response);
        
        // Should not crash and should return valid JSON
        String responseText = stringWriter.toString();
        assertTrue(responseText.startsWith("{") && responseText.endsWith("}"), 
                  "Should return valid JSON object");
    }

    @Test
    @DisplayName("Should handle empty email in session")
    void testEmptyEmailInSession() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("");
        
        servlet.doGet(request, response);
        
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("success") && responseText.contains("false"), 
                  "Should return success: false for empty email");
    }

    @Test
    @DisplayName("Should set proper content type and encoding")
    void testResponseHeaders() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn("test@example.com");
        
        servlet.doGet(request, response);
        
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void testSpecialCharactersInEmail() throws Exception {
        String specialEmail = "user+test@domain-name.co.uk";
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("email")).thenReturn(specialEmail);
        
        servlet.doGet(request, response);
        
        String responseText = stringWriter.toString();
        assertTrue(responseText.contains("success") && responseText.contains("true"), 
                  "Should handle special characters in email");
        assertTrue(responseText.contains(specialEmail), 
                  "Should return the full email with special characters");
    }
}