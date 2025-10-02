package com.calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AppTest {
    
    @Mock
    private CalendarService calendarService;
    
    @InjectMocks
    private App app;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(app).build();
    }
    
    @Test
    public void testHomeRedirection() throws Exception {
        // Test that root URL returns redirect string
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("redirect:/calendar.html"));
    }
    
    @Test
    public void testGetStatusWhenDatabaseConnected() throws Exception {
        // Given
        when(calendarService.isDatabaseConnected()).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("connected"))
                .andExpect(jsonPath("$.timestamp").exists());
                
        verify(calendarService).isDatabaseConnected();
    }
    
    @Test
    public void testGetStatusWhenDatabaseDisconnected() throws Exception {
        // Given
        when(calendarService.isDatabaseConnected()).thenReturn(false);
        
        // When & Then
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("disconnected"))
                .andExpect(jsonPath("$.timestamp").exists());
                
        verify(calendarService).isDatabaseConnected();
    }
}