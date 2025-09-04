package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static String getValueFromDatabase(String url, String user, String password, String query) {
        String result = null;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                result = rs.getString(1);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        // Connect to MySQL database "asd" (phpMyAdmin manages MySQL)
        String url = "jdbc:mysql://localhost:3306/asd";
        String user = "root";
        String password = "";
        //System.out.println("Booking time: " + getStartTime(url, user, password) + ", " + getEndTime(url, user, password));
    }

    public static String getStartTime(String url, String user, String password){
        String query = "SELECT start_Time, end_Time FROM booktime where room_id = 0";
        String startTime = null;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                startTime = rs.getString("start_Time");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
        return startTime;
    }

    public static String getEndTime(String url, String user, String password){
        String query = "SELECT start_Time, end_Time FROM booktime where room_id = 0";
        String endTime = null;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                endTime = rs.getString("end_Time");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
        return endTime;
    }
}
