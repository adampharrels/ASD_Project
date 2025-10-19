package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/bookings")
public class BookingsServlet extends HttpServlet {
    private CalendarService calendarService = new CalendarService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            List<Booking> bookings = calendarService.getAllBookings();
            resp.getWriter().write(gson.toJson(bookings));
            System.out.println("✅ Fetched " + bookings.size() + " bookings");
        } catch (Exception e) {
            System.err.println("❌ Error in BookingsServlet: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}