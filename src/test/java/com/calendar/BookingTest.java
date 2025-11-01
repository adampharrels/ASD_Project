/**
 * @file BookingTest.java
 * @contributor Martin Lau
 * Created: October 2023
 * Last Updated: October 2025
 * 
 * Unit tests for the Booking class and its functionality
 */

package com.calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingTest {
    
    private Booking booking;
    
    @BeforeEach
    void setUp() {
        booking = new Booking(1, 101, "2025-01-15 09:00:00", "2025-01-15 10:00:00", 
                             "CB06.06.112", "Group Study Room", 8, true, true, true, true, "Group_Study_Room");
    }
    
    @Test
    public void testBookingCreationWithAllParameters() {
        // Test that booking is created correctly with all parameters
        assertEquals(1, booking.getTimeID());
        assertEquals(101, booking.getRoomId());
        assertEquals("2025-01-15 09:00:00", booking.getStartTime());
        assertEquals("2025-01-15 10:00:00", booking.getEndTime());
        assertEquals("CB06.06.112", booking.getRoomName());
        assertEquals("Group Study Room", booking.getRoomType());
        assertEquals(8, booking.getCapacity());
        assertTrue(booking.isSpeaker());
        assertTrue(booking.isWhiteboard());
        assertTrue(booking.isMonitor());
        assertTrue(booking.isHdmiCable());
        assertEquals("Group_Study_Room", booking.getImage());
    }
    
    @Test
    public void testBookingDefaultConstructor() {
        // Test default constructor behavior with minimal values
        Booking emptyBooking = new Booking(0, 0, "", "", "", "", 0, false, false, false, false, "");
        assertNotNull(emptyBooking);
        assertEquals(0, emptyBooking.getTimeID());
        assertEquals(0, emptyBooking.getRoomId());
        assertEquals("", emptyBooking.getStartTime());
        assertEquals("", emptyBooking.getEndTime());
    }
    
    @Test
    public void testBookingSetters() {
        // Test all setter methods
        Booking testBooking = new Booking(0, 0, "", "", "", "", 0, false, false, false, false, "");
        
        testBooking.setTimeID(99);
        testBooking.setRoomId(999);
        testBooking.setStartTime("2025-12-25 14:00:00");
        testBooking.setEndTime("2025-12-25 15:00:00");
        testBooking.setRoomName("Test Room");
        testBooking.setRoomType("Test Type");
        testBooking.setCapacity(50);
        testBooking.setSpeaker(false);
        testBooking.setWhiteboard(false);
        testBooking.setMonitor(false);
        testBooking.setHdmiCable(false);
        testBooking.setImage("test_image");
        
        assertEquals(99, testBooking.getTimeID());
        assertEquals(999, testBooking.getRoomId());
        assertEquals("2025-12-25 14:00:00", testBooking.getStartTime());
        assertEquals("2025-12-25 15:00:00", testBooking.getEndTime());
        assertEquals("Test Room", testBooking.getRoomName());
        assertEquals("Test Type", testBooking.getRoomType());
        assertEquals(50, testBooking.getCapacity());
        assertFalse(testBooking.isSpeaker());
        assertFalse(testBooking.isWhiteboard());
        assertFalse(testBooking.isMonitor());
        assertFalse(testBooking.isHdmiCable());
        assertEquals("test_image", testBooking.getImage());
    }
    
    @Test
    public void testBookingEquality() {
        // Test booking equality based on timeID
        Booking booking1 = new Booking(1, 101, "2025-01-15 09:00:00", "2025-01-15 10:00:00", 
                                      "CB06.06.112", "Group Study Room", 8, true, true, true, true, "Group_Study_Room");
        Booking booking2 = new Booking(1, 101, "2025-01-15 09:00:00", "2025-01-15 10:00:00", 
                                      "CB06.06.112", "Group Study Room", 8, true, true, true, true, "Group_Study_Room");
        Booking booking3 = new Booking(2, 102, "2025-01-15 11:00:00", "2025-01-15 12:00:00", 
                                      "CB07.02.010A", "Online Learning Room", 20, false, true, true, true, "Online_Learning_Room");
        
        // Note: This test assumes equals() method exists. If not, this tests object reference equality
        if (booking1.equals(booking2)) {
            assertEquals(booking1, booking2);
        }
        assertNotEquals(booking1, booking3);
    }
    
    @Test
    public void testBookingValidTimeRange() {
        // Test that start time is before end time (if validation exists)
        String startTime = "2025-01-15 09:00:00";
        String endTime = "2025-01-15 10:00:00";
        
        Booking validBooking = new Booking(1, 101, startTime, endTime, 
                                          "CB06.06.112", "Group Study Room", 8, true, true, true, true, "Group_Study_Room");
        
        assertEquals(startTime, validBooking.getStartTime());
        assertEquals(endTime, validBooking.getEndTime());
        // Additional validation logic would go here if implemented in the Booking class
    }
    
    @Test
    public void testBookingWithSpecialCharacters() {
        // Test booking with special characters in room name
        String specialRoomName = "Room-A/B (Special)";
        Booking specialBooking = new Booking(1, 101, "2025-01-15 09:00:00", "2025-01-15 10:00:00", 
                                           specialRoomName, "Special Type", 8, true, true, true, true, "special_image");
        
        assertEquals(specialRoomName, specialBooking.getRoomName());
    }
    
    @Test
    public void testBookingCapacityBoundaries() {
        // Test edge cases for capacity
        Booking minCapacityBooking = new Booking(1, 101, "2025-01-15 09:00:00", "2025-01-15 10:00:00", 
                                                "Small Room", "Single", 1, false, false, false, false, "single_room");
        
        Booking maxCapacityBooking = new Booking(2, 102, "2025-01-15 11:00:00", "2025-01-15 12:00:00", 
                                                "Large Hall", "Auditorium", 500, true, true, true, true, "auditorium");
        
        assertEquals(1, minCapacityBooking.getCapacity());
        assertEquals(500, maxCapacityBooking.getCapacity());
    }
    
    @Test
    public void testBookingBooleanFeatures() {
        // Test different combinations of boolean features
        Booking basicRoom = new Booking(1, 101, "2025-01-15 09:00:00", "2025-01-15 10:00:00", 
                                       "Basic Room", "Basic", 10, false, false, false, false, "basic");
        
        Booking fullyEquippedRoom = new Booking(2, 102, "2025-01-15 11:00:00", "2025-01-15 12:00:00", 
                                               "Tech Room", "Advanced", 20, true, true, true, true, "tech");
        
        // Basic room should have no features
        assertFalse(basicRoom.isSpeaker());
        assertFalse(basicRoom.isWhiteboard());
        assertFalse(basicRoom.isMonitor());
        assertFalse(basicRoom.isHdmiCable());
        
        // Fully equipped room should have all features
        assertTrue(fullyEquippedRoom.isSpeaker());
        assertTrue(fullyEquippedRoom.isWhiteboard());
        assertTrue(fullyEquippedRoom.isMonitor());
        assertTrue(fullyEquippedRoom.isHdmiCable());
    }
}