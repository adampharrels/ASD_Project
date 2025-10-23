package com.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DisplayName("Integration Tests - Full Workflow")
public class IntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Should complete full booking workflow")
    void testFullBookingWorkflow() {
        // Step 1: Check available rooms
        ResponseEntity<String> roomsResponse = restTemplate.getForEntity(
            baseUrl + "/api/available-rooms", String.class);
        
        assertEquals(HttpStatus.OK, roomsResponse.getStatusCode(), 
                    "Available rooms endpoint should be accessible");
        assertTrue(roomsResponse.getBody().startsWith("["), 
                  "Should return JSON array of rooms");

        // Step 2: Attempt booking without authentication (should fail)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String bookingJson = "{\"roomId\":\"108\",\"date\":\"2025-10-24\",\"startTime\":\"10:00\",\"endTime\":\"11:00\"}";
        HttpEntity<String> bookingRequest = new HttpEntity<>(bookingJson, headers);
        
        ResponseEntity<String> unauthorizedResponse = restTemplate.postForEntity(
            baseUrl + "/api/booking", bookingRequest, String.class);
        
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedResponse.getStatusCode(), 
                    "Should reject booking without authentication");
    }

    @Test
    @DisplayName("Should handle login workflow")
    void testLoginWorkflow() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Test valid login
        String loginJson = "{\"email\":\"testuser@example.com\",\"password\":\"pass123\"}";
        HttpEntity<String> loginRequest = new HttpEntity<>(loginJson, headers);
        
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
            baseUrl + "/api/login", loginRequest, String.class);
        
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), 
                    "Login endpoint should be accessible");
        assertTrue(loginResponse.getBody().contains("success"), 
                  "Login response should contain success field");

        // Test invalid login
        String invalidLoginJson = "{\"email\":\"invalid@example.com\",\"password\":\"wrong\"}";
        HttpEntity<String> invalidLoginRequest = new HttpEntity<>(invalidLoginJson, headers);
        
        ResponseEntity<String> invalidResponse = restTemplate.postForEntity(
            baseUrl + "/api/login", invalidLoginRequest, String.class);
        
        assertEquals(HttpStatus.OK, invalidResponse.getStatusCode(), 
                    "Invalid login should return 200 with error message");
        assertTrue(invalidResponse.getBody().contains("false"), 
                  "Invalid login should return success: false");
    }

    @Test
    @DisplayName("Should handle session management")
    void testSessionManagement() {
        // Test session endpoint without authentication
        ResponseEntity<String> sessionResponse = restTemplate.getForEntity(
            baseUrl + "/api/user-session", String.class);
        
        assertEquals(HttpStatus.OK, sessionResponse.getStatusCode(), 
                    "Session endpoint should be accessible");
        assertTrue(sessionResponse.getBody().contains("success"), 
                  "Session response should contain success field");
    }

    @Test
    @DisplayName("Should handle CORS preflight requests")
    void testCorsPreflightRequests() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Request-Method", "POST");
        headers.add("Access-Control-Request-Headers", "Content-Type");
        headers.add("Origin", "http://localhost:8080");
        
        HttpEntity<?> preflightRequest = new HttpEntity<>(headers);
        
        ResponseEntity<String> preflightResponse = restTemplate.exchange(
            baseUrl + "/api/booking", HttpMethod.OPTIONS, preflightRequest, String.class);
        
        assertEquals(HttpStatus.OK, preflightResponse.getStatusCode(), 
                    "CORS preflight should be handled");
        
        HttpHeaders responseHeaders = preflightResponse.getHeaders();
        assertTrue(responseHeaders.containsKey("Access-Control-Allow-Origin"), 
                  "Should include CORS headers");
    }

    @Test
    @DisplayName("Should serve static web content")
    void testStaticContentServing() {
        // Test that main pages are accessible
        ResponseEntity<String> homeResponse = restTemplate.getForEntity(
            baseUrl + "/home.html", String.class);
        
        assertEquals(HttpStatus.OK, homeResponse.getStatusCode(), 
                    "Home page should be accessible");
        assertTrue(homeResponse.getBody().contains("html"), 
                  "Should return HTML content");

        ResponseEntity<String> indexResponse = restTemplate.getForEntity(
            baseUrl + "/index.html", String.class);
        
        assertEquals(HttpStatus.OK, indexResponse.getStatusCode(), 
                    "Index page should be accessible");
    }

    @Test
    @DisplayName("Should handle API error responses gracefully")
    void testApiErrorHandling() {
        // Test malformed JSON to booking endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String malformedJson = "{ invalid json }";
        HttpEntity<String> malformedRequest = new HttpEntity<>(malformedJson, headers);
        
        ResponseEntity<String> errorResponse = restTemplate.postForEntity(
            baseUrl + "/api/booking", malformedRequest, String.class);
        
        // Should handle gracefully (not crash the server)
        assertTrue(errorResponse.getStatusCode().is4xxClientError() || 
                  errorResponse.getStatusCode().is5xxServerError(), 
                  "Should return appropriate error status for malformed JSON");
    }

    @Test
    @DisplayName("Should maintain consistent API response format")
    void testApiResponseFormat() {
        // Test that all API endpoints return JSON
        ResponseEntity<String> roomsResponse = restTemplate.getForEntity(
            baseUrl + "/api/available-rooms", String.class);
        
        assertTrue(roomsResponse.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON), 
                  "Available rooms should return JSON");

        ResponseEntity<String> sessionResponse = restTemplate.getForEntity(
            baseUrl + "/api/user-session", String.class);
        
        assertTrue(sessionResponse.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON), 
                  "User session should return JSON");
    }

    @Test
    @DisplayName("Should handle database connectivity")
    void testDatabaseConnectivity() {
        // Test that endpoints requiring database access don't crash
        ResponseEntity<String> roomsResponse = restTemplate.getForEntity(
            baseUrl + "/api/available-rooms", String.class);
        
        assertEquals(HttpStatus.OK, roomsResponse.getStatusCode(), 
                    "Database-dependent endpoints should handle connection issues gracefully");
        
        // Response should be valid JSON even if database is unavailable
        String body = roomsResponse.getBody();
        assertTrue(body.startsWith("[") && body.endsWith("]"), 
                  "Should return valid JSON array format");
    }

    @Test
    @DisplayName("Should validate booking verification endpoint")
    void testBookingVerificationEndpoint() {
        ResponseEntity<String> verificationResponse = restTemplate.getForEntity(
            baseUrl + "/api/verify-bookings", String.class);
        
        // Should handle request (might return unauthorized but shouldn't crash)
        assertNotNull(verificationResponse, "Verification endpoint should respond");
        assertTrue(verificationResponse.getStatusCode().is2xxSuccessful() || 
                  verificationResponse.getStatusCode().is4xxClientError(), 
                  "Should return appropriate status code");
    }
}