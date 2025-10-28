package com.calendar;

/**
 * UserSessionServlet - Manages user session information and authentication state
 * 
 * This servlet handles user session management tasks including:
 * - Retrieving current user session information
 * - Providing user profile data to the frontend
 * - Managing session attributes (email, name, student ID)
 * - Basic user profile data transformation
 * 
 * Dependencies:
 * - Requires valid HTTP session with user authentication
 * - Uses GSON for JSON serialization
 * 
 * Contributors:
 * - Adam Nguyen (adampharrels) - Initial session management implementation
 * - Noah (Noahkhuu127) - Enhanced session handling and booking flow
 * 
 * Created: September 2023
 * Last Updated: October 2025
 */

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
            String firstName = (String) session.getAttribute("firstName");
            String lastName = (String) session.getAttribute("lastName");
            String fullName = (String) session.getAttribute("fullName");
            String studentId = (String) session.getAttribute("studentId");
            
            response.put("success", true);
            response.put("email", userEmail);
            response.put("firstName", firstName);
            response.put("lastName", lastName);
            response.put("username", extractUsernameFromEmail(userEmail));
            response.put("fullName", fullName != null ? fullName : extractNameFromEmail(userEmail));
            response.put("studentId", studentId);

            System.out.println("✅ User session found: " + fullName + " (Email: " + userEmail + ", StudentId: " + studentId + ")");
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