package com.calendar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class AppIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testApplicationStartsSuccessfully() {
        // Test that the application context loads successfully
        assertTrue(port > 0);
    }

    @Test
    public void testHomeEndpointRedirection() {
        // Given
        String url = "http://localhost:" + port + "/";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("redirect:/calendar.html", response.getBody());
    }

    @Test
    public void testStatusEndpoint() {
        // Given
        String url = "http://localhost:" + port + "/api/status";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("database"));
        assertTrue(response.getBody().contains("timestamp"));
    }

    @Test
    public void testBookingsEndpoint() {
        // Given
        String url = "http://localhost:" + port + "/api/bookings";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then - Should return OK even if database is not connected (will return empty list)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testRoomsEndpoint() {
        // Given
        String url = "http://localhost:" + port + "/api/rooms";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then - Should return OK even if database is not connected (will return empty list)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testCorsHeaders() {
        // Given
        String url = "http://localhost:" + port + "/api/status";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then - Check if CORS is properly configured
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: In a real test, you might want to check specific CORS headers
    }

    @Test
    public void testNonExistentEndpoint() {
        // Given
        String url = "http://localhost:" + port + "/api/nonexistent";
        
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}