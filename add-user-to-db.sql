-- Add the logged-in user account to the database
-- This syncs the account from accounts.txt to the users table for booking system

INSERT IGNORE INTO users (username, email, full_name, student_id) VALUES 
('admins', 'admins@student.uts.edu.au', 'Admin User', '123');

-- Check if user was added
SELECT * FROM users WHERE email = 'admins@student.uts.edu.au';