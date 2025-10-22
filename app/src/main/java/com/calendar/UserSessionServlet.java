package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/user-session")
public class UserSessionServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        
        HttpSession session = req.getSession(false);
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("🔍 UserSessionServlet: Checking session...");
        System.out.println("Session exists: " + (session != null));
        if (session != null) {
            System.out.println("Session ID: " + session.getId());
            System.out.println("Email attribute: " + session.getAttribute("email"));
        }
        
        if (session != null && session.getAttribute("email") != null) {
            // User is logged in
            String userEmail = (String) session.getAttribute("email");
            String username = (String) session.getAttribute("username");
            String fullName = (String) session.getAttribute("fullName");
            String studentId = (String) session.getAttribute("studentId");
            
            response.put("success", true);
            response.put("email", userEmail);
            response.put("username", username != null ? username : extractUsernameFromEmail(userEmail));
            response.put("fullName", fullName != null ? fullName : extractNameFromEmail(userEmail));
            response.put("studentId", studentId);

            System.out.println("✅ User session found: " + userEmail);
        } else {
            // No session or user not logged in
            response.put("success", false);
            response.put("error", "User not logged in");
            System.out.println("❌ No session or email found");
        }
        
        String json = gson.toJson(response);
        resp.getWriter().write(json);
    }
    
    private String extractUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return email;
    }
    
    private String extractNameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            String username = email.substring(0, email.indexOf("@"));
            // Convert email username to display name (basic transformation)
            return username.replace(".", " ").replace("_", " ");
        }
        return "User";
    }
}