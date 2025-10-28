package com.calendar;

/**
 * RoomsServlet - Handles room data retrieval for the UniSpace system
 * 
 * This servlet provides room data access functionality:
 * - Retrieves all available rooms from the database
 * - Returns room details in JSON format
 * - Integrates with CalendarService for data access
 * 
 * Dependencies:
 * - CalendarService for room data retrieval
 * - GSON for JSON serialization
 * 
 * Contributors:
 * - Adam Nguyen (adampharrels) - Calendar feature and room listing implementation
 * 
 * Created: September 2023
 * Last Updated: October 2025
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/rooms")
public class RoomsServlet extends HttpServlet {
    private CalendarService calendarService = new CalendarService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            List<Map<String, Object>> rooms = calendarService.getAllRooms();
            resp.getWriter().write(gson.toJson(rooms));
            System.out.println("✅ Fetched " + rooms.size() + " rooms");
        } catch (Exception e) {
            System.err.println("❌ Error in RoomsServlet: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}