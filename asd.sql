-- UniSpace Database Schema
-- This script creates the complete database structure for the room booking system

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    student_id VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create room table
CREATE TABLE IF NOT EXISTS room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(50) UNIQUE NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    speaker BOOLEAN DEFAULT FALSE,
    whiteboard BOOLEAN DEFAULT FALSE,
    monitor BOOLEAN DEFAULT FALSE,
    hdmi_cable BOOLEAN DEFAULT FALSE,
    image VARCHAR(255) DEFAULT NULL,
    location VARCHAR(100),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create booktime table (booking history)
CREATE TABLE IF NOT EXISTS booktime (
    timeID INT AUTO_INCREMENT PRIMARY KEY,
    booking_ref VARCHAR(20) UNIQUE NOT NULL, -- Short memorable booking reference
    room_id INT NOT NULL,
    user_id INT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    booking_status ENUM('ACTIVE', 'CHECKED_IN', 'CHECKED_OUT', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    purpose VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP NULL,
    checked_in_at TIMESTAMP NULL,
    checked_out_at TIMESTAMP NULL,
    FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_room_time (room_id, start_time, end_time),
    INDEX idx_user_bookings (user_id, created_at),
    INDEX idx_status (booking_status),
    INDEX idx_booking_ref (booking_ref)
);

-- Insert sample users (H2 compatible - using INSERT with IF NOT EXISTS check, no explicit user_id)
INSERT INTO users (username, email, full_name, student_id) 
SELECT 'admin_user', 'admins@student.uts.edu.au', 'Admin User', '123' 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admins@student.uts.edu.au');

INSERT INTO users (username, email, full_name, student_id) 
SELECT 'adam_p', 'adam.nguyen@student.edu', 'Adam Nguyen', '123123' 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'adam.nguyen@student.edu');

INSERT INTO users (username, email, full_name, student_id) 
SELECT 'sarah_j', 'sarah.jones@student.edu', 'Sarah Jones', '456789' 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'sarah.jones@student.edu');

INSERT INTO users (username, email, full_name, student_id) 
SELECT 'mike_w', 'mike.wilson@student.edu', 'Mike Wilson', '111003' 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'mike.wilson@student.edu');

INSERT INTO users (username, email, full_name, student_id) 
SELECT 'emma_d', 'emma.davis@student.edu', 'Emma Davis', '111004' 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'emma.davis@student.edu');

INSERT INTO users (username, email, full_name, student_id) 
SELECT 'john_s', 'john.smith@student.edu', 'John Smith', '111005' 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'john.smith@student.edu');

-- Insert sample rooms (H2 compatible)
MERGE INTO room (room_id, room_name, room_type, capacity, speaker, whiteboard, monitor, hdmi_cable, image, location) VALUES 
(1, 'CB06.06.112', 'Group Study Room', 8, true, true, true, true, 'Group_Study_Room', 'Building CB06, Level 6'),
(2, 'CB06.06.113', 'Group Study Room', 8, false, false, true, true, 'Group_Study_Room', 'Building CB06, Level 6'),
(3, 'CB07.02.010A', 'Online Learning Room', 2, false, false, true, true, 'Online_Learning_Room', 'Building CB07, Level 2'),
(4, 'CB07.02.010B', 'Online Learning Room', 2, true, false, true, true, 'Online_Learning_Room', 'Building CB07, Level 2'),
(5, 'CB06.03.205', 'Group Study Room', 12, true, true, true, true, 'Group_Study_Room', 'Building CB06, Level 3'),
(6, 'CB06.03.206', 'Group Study Room', 10, false, true, true, true, 'Group_Study_Room', 'Building CB06, Level 3'),
(7, 'CB08.01.115', 'Lecture Room', 50, true, true, true, true, 'Lecture_Room', 'Building CB08, Level 1'),
(8, 'CB08.01.116', 'Lecture Room', 40, true, true, false, true, 'Lecture_Room', 'Building CB08, Level 1'),
(9, 'CB07.04.020A', 'Online Learning Room', 4, true, false, true, true, 'Online_Learning_Room', 'Building CB07, Level 4'),
(10, 'CB07.04.020B', 'Online Learning Room', 4, false, false, true, false, 'Online_Learning_Room', 'Building CB07, Level 4'),
(11, 'CB09.02.301', 'Conference Room', 16, true, true, true, true, 'Conference_Room', 'Building CB09, Level 2'),
(12, 'CB09.02.302', 'Conference Room', 20, true, true, true, true, 'Conference_Room', 'Building CB09, Level 2'),
(13, 'CB05.01.108', 'Computer Lab', 24, false, true, true, true, 'Computer_Lab', 'Building CB05, Level 1'),
(14, 'CB05.01.109', 'Computer Lab', 28, false, true, true, true, 'Computer_Lab', 'Building CB05, Level 1'),
(15, 'CB06.05.220', 'Group Study Room', 6, false, true, false, true, 'Group_Study_Room', 'Building CB06, Level 5');

-- Insert sample bookings with memorable booking references (H2 compatible)
MERGE INTO booktime (booking_ref, room_id, user_id, start_time, end_time, booking_status) VALUES 
('CB112-1017P-A', 1, 1, '2025-10-17 16:30:00', '2025-10-17 17:30:00', 'COMPLETED'),
('CB113-1017P-S', 2, 1, '2025-10-17 17:00:00', '2025-10-17 18:00:00', 'COMPLETED'),
('CB010A-1018M-A', 3, 1, '2025-10-18 10:00:00', '2025-10-18 11:00:00', 'COMPLETED'),
('CB010B-1018P-M', 4, 1, '2025-10-18 14:00:00', '2025-10-18 15:00:00', 'COMPLETED'),
('CB112-1021M-A', 1, 1, '2025-10-21 09:00:00', '2025-10-21 10:00:00', 'ACTIVE'),
('CB205-1021P-S', 5, 1, '2025-10-21 14:00:00', '2025-10-21 16:00:00', 'ACTIVE'),
('CB115-1022M-A', 7, 1, '2025-10-22 11:00:00', '2025-10-22 12:00:00', 'ACTIVE');

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_booktime_room_status ON booktime(room_id, booking_status);
CREATE INDEX IF NOT EXISTS idx_booktime_time_range ON booktime(start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_room_type ON room(room_type);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

COMMIT;
