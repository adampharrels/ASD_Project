package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class App {
    
    @Autowired
    private CalendarService bookingService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/calendar.html";
    }

    @GetMapping("/api/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        if (bookingService.isDatabaseConnected()) {
            status.put("database", "connected");
        } else {
            status.put("database", "disconnected");
        }
        status.put("timestamp", new java.util.Date());
        return status;
    }

    @GetMapping("/api/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/api/rooms")
    public List<Map<String, Object>> getAllRooms() {
        return bookingService.getAllRooms();
    }
}