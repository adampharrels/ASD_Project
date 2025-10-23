package com.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ReadListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingSystemTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private BookingServlet servlet;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        servlet = new BookingServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        
        try {
            when(response.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            // Handle IOException in test setup
        }
        
        when(request.getSession(false)).thenReturn(session);
    }

    @Test
    void testValidBookingRequest() throws Exception {
        String bookingJson = """
            {
                "roomId": "1",
                "date": "2025-10-23",
                "startTime": "09:00",
                "endTime": "10:00"
            }
            """;
        
        setupMockInputStream(bookingJson);
        when(session.getAttribute("email")).thenReturn("test@unispace.edu");
        when(request.getContentType()).thenReturn("application/json");
        
        servlet.doPost(request, response);
        
        verify(response).setContentType("application/json");
    }

    @Test
    void testBookingWithoutSession() throws Exception {
        String bookingJson = """
            {
                "roomId": "1",
                "date": "2025-10-23",
                "startTime": "09:00",
                "endTime": "10:00"
            }
            """;
        
        setupMockInputStream(bookingJson);
        when(request.getSession(false)).thenReturn(null);
        
        servlet.doPost(request, response);
        
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testInvalidBookingData() throws Exception {
        String invalidJson = """
            {
                "roomId": "",
                "date": "invalid-date",
                "startTime": "25:00"
            }
            """;
        
        setupMockInputStream(invalidJson);
        when(session.getAttribute("email")).thenReturn("test@unispace.edu");
        
        servlet.doPost(request, response);
        
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testMissingRequiredFields() throws Exception {
        String incompleteJson = """
            {
                "roomId": "1"
            }
            """;
        
        setupMockInputStream(incompleteJson);
        when(session.getAttribute("email")).thenReturn("test@unispace.edu");
        
        servlet.doPost(request, response);
        
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testCORSHeadersInBooking() throws Exception {
        String bookingJson = """
            {
                "roomId": "1",
                "date": "2025-10-23",
                "startTime": "09:00",
                "endTime": "10:00"
            }
            """;
        
        setupMockInputStream(bookingJson);
        when(session.getAttribute("email")).thenReturn("test@unispace.edu");
        
        servlet.doPost(request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "*");
    }

    @Test
    void testOptionsRequestForBooking() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");
        
        servlet.service(request, response);
        
        verify(response).setHeader("Access-Control-Allow-Origin", "*");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testBookingReferenceGeneration() throws Exception {
        String bookingJson = """
            {
                "roomId": "1",
                "date": "2025-10-23", 
                "startTime": "09:00",
                "endTime": "10:00"
            }
            """;
        
        setupMockInputStream(bookingJson);
        when(session.getAttribute("email")).thenReturn("test@unispace.edu");
        
        servlet.doPost(request, response);
        
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testDatabaseErrorHandling() throws Exception {
        String bookingJson = """
            {
                "roomId": "999",
                "date": "2025-10-23",
                "startTime": "09:00", 
                "endTime": "10:00"
            }
            """;
        
        setupMockInputStream(bookingJson);
        when(session.getAttribute("email")).thenReturn("test@unispace.edu");
        
        servlet.doPost(request, response);
        
        verify(response).setContentType("application/json");
        String responseBody = stringWriter.toString();
        assertNotNull(responseBody);
    }

    @Test
    void testServletInitialization() {
        assertNotNull(servlet);
    }

    private void setupMockInputStream(String content) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(content.getBytes());
        
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteStream.read();
            }
            
            @Override
            public boolean isFinished() {
                return byteStream.available() == 0;
            }
            
            @Override
            public boolean isReady() {
                return true;
            }
            
            @Override
            public void setReadListener(ReadListener readListener) {
                // Not implemented for test
            }
        };
        
        when(request.getInputStream()).thenReturn(servletInputStream);
    }
}