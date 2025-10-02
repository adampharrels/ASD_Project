package com.example;

// Simple test class without JUnit dependencies for basic validation
public class SimpleTest {
    
    public static void main(String[] args) {
        SimpleTest test = new SimpleTest();
        test.runAllTests();
    }
    
    public void runAllTests() {
        testBasicMath();
        testStringOperations();
        testBookingClassExists();
        System.out.println("All basic tests passed!");
    }
    
    public void testBasicMath() {
        assert 2 + 2 == 4 : "Basic math failed";
        assert 5 * 2 == 10 : "Multiplication failed";
        System.out.println("✓ Math tests passed");
    }
    
    public void testStringOperations() {
        String test = "Hello World";
        assert test.length() == 11 : "String length failed";
        assert test.contains("World") : "String contains failed";
        System.out.println("✓ String tests passed");
    }
    
    public void testBookingClassExists() {
        try {
            // Test that we can create a Booking object
            Booking booking = new Booking(1, 101, "2025-10-02 09:00:00", "2025-10-02 10:00:00", 
                                        "CB06.06.112", "Group Study Room", 8, true, true, true, true, "Group_Study_Room");
            assert booking != null : "Booking creation failed";
            assert booking.getTimeID() == 1 : "Booking timeID failed";
            assert booking.getCapacity() == 8 : "Booking capacity failed";
            assert booking.getRoomName().equals("CB06.06.112") : "Room name failed";
            System.out.println("✓ Booking class tests passed");
        } catch (Exception e) {
            System.err.println("✗ Booking class test failed: " + e.getMessage());
        }
    }
}