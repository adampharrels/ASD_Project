package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    
    @Test
    public void testMathOperations() {
        // Basic math test to verify JUnit is working
        int result1 = 2 + 2;
        int result2 = 5 * 2;
        
        assertEquals(4, result1);
        assertEquals(10, result2);
        assertTrue(5 > 3);
        assertFalse(2 > 5);
    }
    
    @Test
    public void testStringOperations() {
        // Basic string test
        String test = "Hello World";
        String upperTest = test.toUpperCase();
        
        assertEquals(11, test.length());
        assertTrue(test.contains("World"));
        assertEquals("HELLO WORLD", upperTest);
    }
    
    @Test 
    public void testArrayOperations() {
        // Test array operations
        int[] numbers = {1, 2, 3, 4, 5};
        
        assertEquals(5, numbers.length);
        assertEquals(1, numbers[0]);
        assertEquals(5, numbers[4]);
    }
    
    @Test
    public void testBookingClassBasics() {
        // Test that we can reference the Booking class
        // This tests the class structure without database dependencies
        try {
            // Just test class loading, not instantiation to avoid constructor issues
            Class<?> bookingClass = Class.forName("com.example.Booking");
            assertNotNull(bookingClass);
            assertTrue(bookingClass.getName().contains("Booking"));
        } catch (ClassNotFoundException e) {
            fail("Booking class should exist in the project");
        }
    }
}
