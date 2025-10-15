package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/status")
public class StatusServlet extends HttpServlet {
    private CalendarService calendarService = new CalendarService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        
        Map<String, Object> status = new HashMap<>();
        if (calendarService.isDatabaseConnected()) {
            status.put("database", "connected");
        } else {
            status.put("database", "disconnected");
        }
        status.put("timestamp", new java.util.Date());
        
        resp.getWriter().write(gson.toJson(status));
        System.out.println("✅ Status check: " + status.get("database"));
    }
}