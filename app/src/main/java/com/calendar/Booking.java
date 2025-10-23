package com.calendar;

public class Booking {
    private int bookingID;
    private int timeID;
    private String bookingRef; // Memorable booking reference (e.g., CB112-1021M-A1K)
    private int roomId;
    private int userId;
    private String startTime;
    private String endTime;
    private String roomName;
    private String roomType;
    private int capacity;
    private boolean speaker;
    private boolean whiteboard;
    private boolean monitor;
    private boolean hdmiCable;
    private String image;
    private String bookingStatus;
    private String createdAt;
    private String cancelledAt;
    private String userFullName;

    public Booking(int bookingID, int timeID, String bookingRef, int roomId, int userId, String startTime, String endTime, String roomName, String roomType, 
                   int capacity, boolean speaker, boolean whiteboard, boolean monitor, boolean hdmiCable, String image, 
                   String bookingStatus, String createdAt, String cancelledAt, String userFullName) {
        this.bookingID = bookingID;
        this.timeID = timeID;
        this.bookingRef = bookingRef;
        this.roomId = roomId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.roomType = roomType;
        this.capacity = capacity;
        this.speaker = speaker;
        this.whiteboard = whiteboard;
        this.monitor = monitor;
        this.hdmiCable = hdmiCable;
        this.image = image;
        this.bookingStatus = bookingStatus;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
        this.userFullName = userFullName;
    }

    // Legacy constructor for backward compatibility
    public Booking(int bookingID, int timeID, int roomId, String startTime, String endTime, String roomName, String roomType, 
                   int capacity, boolean speaker, boolean whiteboard, boolean monitor, boolean hdmiCable, String image) {
        this(bookingID, timeID, null, roomId, 0, startTime, endTime, roomName, roomType, capacity, speaker, whiteboard, monitor, hdmiCable, image, "ACTIVE", null, null, null);
    }

    // Getters and setters for existing fields
    public int getBookingID() { return bookingID; }
    public void setBookingID(int bookingID) { this.bookingID = bookingID; }
    
    public int getTimeID() { return timeID; }
    public void setTimeID(int timeID) { this.timeID = timeID; }
    
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    // Getters and setters for new room details
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public boolean isSpeaker() { return speaker; }
    public void setSpeaker(boolean speaker) { this.speaker = speaker; }
    
    public boolean isWhiteboard() { return whiteboard; }
    public void setWhiteboard(boolean whiteboard) { this.whiteboard = whiteboard; }
    
    public boolean isMonitor() { return monitor; }
    public void setMonitor(boolean monitor) { this.monitor = monitor; }
    
    public boolean isHdmiCable() { return hdmiCable; }
    public void setHdmiCable(boolean hdmiCable) { this.hdmiCable = hdmiCable; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(String cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
}
