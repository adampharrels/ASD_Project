package com.calendar;

/**
 * BookingIdGenerator - Generates and manages unique, human-readable booking references
 * 
 * This utility class handles booking reference generation with features:
 * - Human-readable reference format (ROOM-DATESLOT-USER)
 * - Time period encoding (Morning/Afternoon/Evening/Night)
 * - Room code abbreviation logic
 * - User initial extraction
 * - Validation and display formatting
 * 
 * Example reference: CB112-1021M-A1K
 * - CB112: Room code (CB06.06.112)
 * - 1021M: October 21, Morning
 * - A1K: User initial (A) + unique suffix
 * 
 * Contributors:
 * - Adam Nguyen (adampharrels) - Design and implementation of booking reference system
 * 
 * Created: September 2023
 * Last Updated: October 2025
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class BookingIdGenerator {
    
    /**
     * Generates a memorable booking reference ID
     * Format: ROOM-DATESLOT-USER
     * Example: CB112-1021M-A (CB06.06.112 on Oct 21 Morning by Adam)
     */
    public static String generateBookingRef(String roomName, LocalDateTime startTime, String userFullName) {
        // Extract room abbreviation (e.g., "CB06.06.112" -> "CB112")
        String roomAbbrev = generateRoomAbbreviation(roomName);
        
        // Generate date code (MMDD format)
        String dateCode = startTime.format(DateTimeFormatter.ofPattern("MMdd"));
        
        // Generate time slot (M=Morning, A=Afternoon, E=Evening, N=Night)
        String timeSlot = generateTimeSlot(startTime.getHour());
        
        // Generate user initial (first letter of first name)
        String userInitial = generateUserInitial(userFullName);
        
        // Add random suffix to ensure uniqueness
        String randomSuffix = generateRandomSuffix();
        
        return roomAbbrev + "-" + dateCode + timeSlot + "-" + userInitial + randomSuffix;
    }
    
    /**
     * Converts room name to memorable abbreviation
     * CB06.06.112 -> CB112
     * CB07.02.010A -> CB010A
     */
    private static String generateRoomAbbreviation(String roomName) {
        if (roomName == null || roomName.isEmpty()) {
            return "RM" + String.format("%02d", new Random().nextInt(99));
        }
        
        // Handle formats like "CB06.06.112"
        if (roomName.matches("CB\\d+\\.\\d+\\.\\d+[A-Z]*")) {
            String[] parts = roomName.split("\\.");
            if (parts.length >= 3) {
                String building = parts[0]; // CB06
                String lastPart = parts[2];  // 112 or 112A
                
                // Extract just the numbers and any letter suffix
                String roomNum = lastPart.replaceAll("^0+", ""); // Remove leading zeros
                if (roomNum.isEmpty()) roomNum = "0";
                
                return building.substring(0, 2) + roomNum; // CB + 112
            }
        }
        
        // Fallback: use first 6 chars, remove dots and spaces
        return roomName.substring(0, Math.min(6, roomName.length()))
                      .replaceAll("[.\\s]", "")
                      .toUpperCase();
    }
    
    /**
     * Generates time slot code based on hour
     * 6-11: M (Morning)
     * 12-17: A (Afternoon) 
     * 18-21: E (Evening)
     * 22-5: N (Night)
     */
    private static String generateTimeSlot(int hour) {
        if (hour >= 6 && hour < 12) {
            return "M"; // Morning
        } else if (hour >= 12 && hour < 18) {
            return "A"; // Afternoon
        } else if (hour >= 18 && hour < 22) {
            return "E"; // Evening
        } else {
            return "N"; // Night
        }
    }
    
    /**
     * Generates user initial from full name
     * "Adam Nguyen" -> "A"
     * "Sarah Jones" -> "S"
     */
    private static String generateUserInitial(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "U"; // Unknown user
        }
        
        String firstName = fullName.trim().split("\\s+")[0];
        return firstName.substring(0, 1).toUpperCase();
    }
    
    /**
     * Generates a 2-character random suffix for uniqueness
     * Examples: 1K, 9X, 4M
     */
    private static String generateRandomSuffix() {
        Random random = new Random();
        char digit = (char) ('0' + random.nextInt(10));  // 0-9
        char letter = (char) ('A' + random.nextInt(26)); // A-Z
        return "" + digit + letter;
    }
    
    /**
     * Validates that a booking reference has the correct format
     */
    public static boolean isValidBookingRef(String bookingRef) {
        if (bookingRef == null) return false;
        
        // Format: ROOM-DATESLOT-USERSUFFIX
        // Example: CB112-1021M-A1K
        return bookingRef.matches("^[A-Z0-9]{3,8}-\\d{4}[MAEN]-[A-Z]\\d[A-Z]$");
    }
    
    /**
     * Extracts human-readable info from booking reference
     */
    public static String formatBookingRefForDisplay(String bookingRef) {
        if (!isValidBookingRef(bookingRef)) {
            return bookingRef; // Return as-is if invalid
        }
        
        String[] parts = bookingRef.split("-");
        if (parts.length != 3) return bookingRef;
        
        String dateSlot = parts[1];
        String userSuffix = parts[2];
        
        // Extract date and time slot
        String date = dateSlot.substring(0, 4); // MMDD
        String timeSlot = dateSlot.substring(4);  // M/A/E/N
        
        String timeSlotName = switch (timeSlot) {
            case "M" -> "Morning";
            case "A" -> "Afternoon";
            case "E" -> "Evening";
            case "N" -> "Night";
            default -> "";
        };
        
        String userInitial = userSuffix.substring(0, 1);
        
        return String.format("%s (%s/%s %s, User %s)", 
                           bookingRef, 
                           date.substring(0, 2), date.substring(2), 
                           timeSlotName, 
                           userInitial);
    }
}