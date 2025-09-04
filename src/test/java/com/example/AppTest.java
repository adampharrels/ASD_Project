package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    public void testGetStartTime() {
        String url = "jdbc:mysql://localhost:3306/asd";
        String user = "root";
        String password = "";
        String startTime = App.getStartTime(url, user, password);
        // Replace with your expected value from the database
        String expectedStartTime = "0000-00-00 00:00:00";
        assertEquals(expectedStartTime, startTime, "Start time should match expected value");
        System.out.println("Start time: " + startTime);
    }

    @Test
    public void testGetEndTime() {
        String url = "jdbc:mysql://localhost:3306/asd";
        String user = "root";
        String password = "";
        String endTime = App.getEndTime(url, user, password);
        // Replace with your expected value from the database
        String expectedEndTime = "0000-00-00 03:30:00";
        assertEquals(expectedEndTime, endTime, "End time should match expected value");
        System.out.println("End time: " + endTime);
    }
}
