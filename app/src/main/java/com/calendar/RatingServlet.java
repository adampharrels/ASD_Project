package com.calendar;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.google.gson.Gson;
import uni.space.finder.DatabaseSetup;

@WebServlet(name = "RatingServlet", urlPatterns = {"/api/ratings"})
public class RatingServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String bookingId = req.getParameter("bookingId");
        if (bookingId == null) {
            resp.getWriter().write("[]");
            return;
        }
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, rating, comment, created_at FROM ratings WHERE booking_id = ? ORDER BY created_at DESC")) {
            ps.setLong(1, Long.parseLong(bookingId));
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("rating", rs.getInt("rating"));
                m.put("comment", rs.getString("comment"));
                m.put("createdAt", rs.getTimestamp("created_at").toInstant().toString());
                list.add(m);
            }
            resp.getWriter().write(gson.toJson(list));
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"db_error\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (BufferedReader br = req.getReader()) {
            Map<?, ?> data = gson.fromJson(br, Map.class);
            Number bookingIdN = (Number) data.get("bookingId");
            Number ratingN = (Number) data.get("rating");
            String comment = data.get("comment") == null ? null : data.get("comment").toString();
            if (bookingIdN == null || ratingN == null) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"missing_fields\"}");
                return;
            }
            long bookingId = bookingIdN.longValue();
            int rating = ratingN.intValue();

            try (Connection conn = DatabaseSetup.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO ratings (booking_id, rating, comment) VALUES (?, ?, ?)")) {
                ps.setLong(1, bookingId);
                ps.setInt(2, rating);
                ps.setString(3, comment);
                ps.executeUpdate();
                resp.getWriter().write("{\"success\":true}");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"server_error\"}");
            e.printStackTrace();
        }
    }
}