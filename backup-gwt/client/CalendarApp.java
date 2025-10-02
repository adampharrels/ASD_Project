package com.example.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.ui.*;

public class CalendarApp implements EntryPoint {
    
    private ListBox roomFilter;
    private FlexTable calendarTable;
    
    @Override
    public void onModuleLoad() {
        createUI();
        loadRooms();
        loadBookings();
        generateCalendar();
    }
    
    private void createUI() {
        // Create main panel
        VerticalPanel mainPanel = new VerticalPanel();
        
        // Title
        HTML title = new HTML("<h1>Room Booking Calendar</h1>");
        mainPanel.add(title);
        
        // Filter section
        HorizontalPanel filterPanel = new HorizontalPanel();
        filterPanel.add(new Label("Filter by Room:"));
        
        roomFilter = new ListBox();
        roomFilter.addItem("All Rooms", "");
        filterPanel.add(roomFilter);
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.addClickHandler(event -> loadBookings());
        filterPanel.add(refreshBtn);
        
        mainPanel.add(filterPanel);
        
        // Calendar table
        calendarTable = new FlexTable();
        calendarTable.setStyleName("calendar");
        mainPanel.add(calendarTable);
        
        // Add to page
        RootPanel.get("calendarContainer").add(mainPanel);
    }
    
    private void generateCalendar() {
        // Header row
        calendarTable.setText(0, 0, "Time");
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            calendarTable.setText(0, i + 1, days[i]);
        }
        
        // Time slots from 8 AM to 6 PM
        for (int hour = 8; hour <= 18; hour++) {
            int row = hour - 7;
            calendarTable.setText(row, 0, hour + ":00");
            
            for (int day = 1; day <= 7; day++) {
                calendarTable.getFlexCellFormatter().setStyleName(row, day, "time-slot");
            }
        }
    }
    
    private void loadRooms() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/api/rooms");
        
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        processRoomsResponse(response.getText());
                    }
                }
                
                @Override
                public void onError(Request request, Throwable exception) {
                    GWT.log("Error loading rooms: " + exception.getMessage());
                }
            });
        } catch (RequestException e) {
            GWT.log("Request exception: " + e.getMessage());
        }
    }
    
    private void processRoomsResponse(String jsonText) {
        JSONValue jsonValue = JSONParser.parseStrict(jsonText);
        JSONArray rooms = jsonValue.isArray();
        
        if (rooms != null) {
            for (int i = 0; i < rooms.size(); i++) {
                JSONObject room = rooms.get(i).isObject();
                if (room != null) {
                    String roomId = room.get("roomId").isNumber().toString();
                    String roomName = room.get("roomName").isString().stringValue();
                    String roomType = room.get("roomType").isString().stringValue();
                    
                    roomFilter.addItem(roomName + " (" + roomType + ")", roomId);
                }
            }
        }
    }
    
    private void loadBookings() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/api/bookings");
        
        try {
            builder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        processBookingsResponse(response.getText());
                    }
                }
                
                @Override
                public void onError(Request request, Throwable exception) {
                    GWT.log("Error loading bookings: " + exception.getMessage());
                }
            });
        } catch (RequestException e) {
            GWT.log("Request exception: " + e.getMessage());
        }
    }
    
    private void processBookingsResponse(String jsonText) {
        // Clear existing bookings
        clearBookings();
        
        JSONValue jsonValue = JSONParser.parseStrict(jsonText);
        JSONArray bookings = jsonValue.isArray();
        
        String selectedRoomId = roomFilter.getSelectedValue();
        
        if (bookings != null) {
            for (int i = 0; i < bookings.size(); i++) {
                JSONObject booking = bookings.get(i).isObject();
                if (booking != null) {
                    String roomId = booking.get("roomId").isNumber().toString();
                    
                    // Filter by selected room if applicable
                    if (selectedRoomId.isEmpty() || selectedRoomId.equals(roomId)) {
                        displayBooking(booking);
                    }
                }
            }
        }
    }
    
    private void clearBookings() {
        for (int row = 1; row <= 11; row++) {
            for (int col = 1; col <= 7; col++) {
                calendarTable.setHTML(row, col, "");
            }
        }
    }
    
    private void displayBooking(JSONObject booking) {
        String startTime = booking.get("startTime").isString().stringValue();
        String roomName = booking.get("roomName").isString().stringValue();
        String roomType = booking.get("roomType").isString().stringValue();
        
        // Parse time and determine cell position
        // This is simplified - you'd need proper date parsing
        if (!startTime.equals("0000-00-00 00:00:00")) {
            // Extract hour from startTime (assuming format: YYYY-MM-DD HH:mm:ss)
            String[] parts = startTime.split(" ");
            if (parts.length > 1) {
                String[] timeParts = parts[1].split(":");
                if (timeParts.length > 0) {
                    try {
                        int hour = Integer.parseInt(timeParts[0]);
                        if (hour >= 8 && hour <= 18) {
                            int row = hour - 7;
                            // For simplicity, put in Monday column (you'd calculate actual day)
                            int col = 1;
                            
                            String currentContent = calendarTable.getHTML(row, col);
                            String bookingHtml = "<div class='booking'><strong>" + roomName + 
                                               "</strong><br/>" + roomType + "</div>";
                            calendarTable.setHTML(row, col, currentContent + bookingHtml);
                        }
                    } catch (NumberFormatException e) {
                        GWT.log("Error parsing hour: " + e.getMessage());
                    }
                }
            }
        }
    }
}
