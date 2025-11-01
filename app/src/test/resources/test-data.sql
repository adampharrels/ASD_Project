/**
 * @file test-data.sql
 * @contributor Martin Lau
 * Created: October 2025
 * Last Updated: October 2025
 * 
 * Test data for calendar and room booking unit tests
 */

-- Test data for unit tests
CREATE TABLE IF NOT EXISTS room (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    image VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS booktime (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT,
    room_name VARCHAR(255),
    room_type VARCHAR(255),
    date DATE,
    time_start VARCHAR(10),
    time_end VARCHAR(10),
    user_id VARCHAR(255),
    user_name VARCHAR(255),
    user_email VARCHAR(255),
    status VARCHAR(50),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

-- Insert test room data
INSERT INTO room (id, name, type, image) VALUES 
(1, 'Group Study Room A', 'Group Study Room', 'Group_Study_Room.jpg'),
(2, 'Online Learning Room B', 'Online Learning Room', 'Online_Learning_Room.jpg'),
(3, 'Conference Room C', 'Conference Room', 'conference_room.jpg');

-- Insert test booking data
INSERT INTO booktime (id, room_id, room_name, room_type, date, time_start, time_end, user_id, user_name, user_email, status) VALUES
(1, 1, 'Group Study Room A', 'Group Study Room', '2024-12-15', '09:00', '11:00', 'user1', 'John Doe', 'john@example.com', 'confirmed'),
(2, 2, 'Online Learning Room B', 'Online Learning Room', '2024-12-15', '14:00', '16:00', 'user2', 'Jane Smith', 'jane@example.com', 'confirmed'),
(3, 1, 'Group Study Room A', 'Group Study Room', '2024-12-16', '10:00', '12:00', 'user3', 'Bob Wilson', 'bob@example.com', 'pending');