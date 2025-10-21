package com.calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="BookingServlet", urlPatterns={"/book-room"})
public class BookingServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final AtomicInteger bookingIdCounter = new AtomicInteger(0);
    private final List<Booking> bookings = new ArrayList<>(); // simple in-memory store

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();

        int bookingID = bookingIdCounter.incrementAndGet();
        int timeID = json.has("timeID") ? json.get("timeID").getAsInt() : 0;
        int roomId = json.has("roomId") ? json.get("roomId").getAsInt() : 0;
        String startTime = json.has("startTime") ? json.get("startTime").getAsString() : "";
        String endTime = json.has("endTime") ? json.get("endTime").getAsString() : "";
        String roomName = json.has("roomName") ? json.get("roomName").getAsString() : "";
        String roomType = json.has("roomType") ? json.get("roomType").getAsString() : "";
        int capacity = json.has("capacity") ? json.get("capacity").getAsInt() : 0;
        boolean speaker = json.has("speaker") && json.get("speaker").getAsBoolean();
        boolean whiteboard = json.has("whiteboard") && json.get("whiteboard").getAsBoolean();
        boolean monitor = json.has("monitor") && json.get("monitor").getAsBoolean();
        boolean hdmiCable = json.has("hdmiCable") && json.get("hdmiCable").getAsBoolean();
        String image = json.has("image") ? json.get("image").getAsString() : "";

        Booking booking = new Booking(bookingID, timeID, roomId, startTime, endTime, roomName, roomType,
                                      capacity, speaker, whiteboard, monitor, hdmiCable, image);
        bookings.add(booking);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        JsonObject out = new JsonObject();
        out.addProperty("bookingID", bookingID);
        out.addProperty("status", "ok");
        resp.getWriter().write(gson.toJson(out));
    }
}

