package uni.space.finder;

import java.sql.*;
import java.nio.file.*;

public class DatabaseSetup {
    // H2 File-Based Database (persistent, no Docker needed)
    private static final String DB_URL = "jdbc:h2:file:./data/unispace;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";
    
    // MySQL Database Configuration (if Docker is available)
    private static final String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/unispace?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String MYSQL_USER = "unispace_user";
    private static final String MYSQL_PASS = "unispace_pass";

    public static Connection getConnection() throws SQLException {
        try {
            // Try to connect to MySQL first (if Docker is running)
            return DriverManager.getConnection(MYSQL_DB_URL, MYSQL_USER, MYSQL_PASS);
        } catch (SQLException e) {
            System.out.println("⚠️  MySQL not available, using H2 file database: " + e.getMessage());
            // Use H2 file-based database (persistent, no Docker needed)
            return DriverManager.getConnection(DB_URL, USER, PASS);
        }
    }

    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check if we're using MySQL or H2
            String dbUrl = conn.getMetaData().getURL();
            boolean isMySQL = dbUrl.contains("mysql");
            
            System.out.println("🗄️  Database Type: " + (isMySQL ? "MySQL" : "H2"));
            System.out.println("🔗 Database URL: " + dbUrl);
            
            if (isMySQL) {
                // For MySQL, the schema is already created by init-db.sql in Docker
                System.out.println("✅ Using MySQL database - schema managed by Docker");
                
                // Test connection and verify tables exist
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as room_count FROM room");
                if (rs.next()) {
                    int roomCount = rs.getInt("room_count");
                    System.out.println("✅ MySQL Database connected successfully! Found " + roomCount + " rooms.");
                }
                
                rs = stmt.executeQuery("SELECT COUNT(*) as user_count FROM users");
                if (rs.next()) {
                    int userCount = rs.getInt("user_count");
                    System.out.println("✅ Found " + userCount + " users in database.");
                }
                
                rs = stmt.executeQuery("SELECT COUNT(*) as booking_count FROM booktime");
                if (rs.next()) {
                    int bookingCount = rs.getInt("booking_count");
                    System.out.println("✅ Found " + bookingCount + " bookings in database.");
                }
                return;
            }
            
            // H2 Fallback - Try to read the external asd.sql file from project root
            Path sqlPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("asd.sql");
            
            if (Files.exists(sqlPath)) {
                System.out.println("📁 Reading asd.sql from: " + sqlPath);
                String sqlContent = Files.readString(sqlPath);
                
                // Clean up SQL for H2 (remove MySQL-specific syntax)
                sqlContent = sqlContent.replaceAll("(?i)ENGINE=InnoDB DEFAULT CHARSET=latin1", "");
                sqlContent = sqlContent.replaceAll("(?i)UNSIGNED", "");
                sqlContent = sqlContent.replaceAll("tinyint\\(1\\)", "BOOLEAN");
                sqlContent = sqlContent.replaceAll("int\\(\\d+\\)", "INT");
                sqlContent = sqlContent.replaceAll("varchar\\((\\d+)\\)", "VARCHAR($1)");
                sqlContent = sqlContent.replaceAll("(?i)START TRANSACTION;", "");
                sqlContent = sqlContent.replaceAll("(?i)SET time_zone = \"\\+00:00\";", "");
                sqlContent = sqlContent.replaceAll("(?i)COMMIT;", "");
                // Remove MySQL backticks
                sqlContent = sqlContent.replaceAll("`", "");
                // Remove MySQL-specific datetime format
                sqlContent = sqlContent.replaceAll("'0000-00-00 00:00:00'", "'1970-01-01 00:00:00'");
                sqlContent = sqlContent.replaceAll("'0000-00-00 03:30:00'", "'1970-01-01 03:30:00'");
                
                // Execute SQL statements
                String[] statements = sqlContent.split(";");
                for (String statement : statements) {
                    statement = statement.trim();
                    if (!statement.isEmpty() && 
                        !statement.startsWith("--") && 
                        !statement.startsWith("/*") &&
                        !statement.startsWith("/*!") &&
                        !statement.toLowerCase().contains("set @") &&
                        !statement.toLowerCase().contains("set sql_mode")) {
                        try {
                            System.out.println("🔄 Executing: " + statement.substring(0, Math.min(50, statement.length())) + "...");
                            stmt.execute(statement);
                        } catch (SQLException e) {
                            // Skip non-critical errors (like duplicate inserts)
                            if (!e.getMessage().contains("already exists") && 
                                !e.getMessage().contains("Duplicate key")) {
                                System.err.println("⚠️  SQL Warning: " + e.getMessage());
                            }
                        }
                    }
                }
            } else {
                System.out.println("⚠️  asd.sql not found, creating tables manually...");
            }
            
            // Always try to create tables manually as fallback
            try {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS room (
                        room_id INT PRIMARY KEY,
                        room_name VARCHAR(12) NOT NULL,
                        room_type VARCHAR(50) NOT NULL,
                        capacity INT NOT NULL,
                        speaker BOOLEAN NOT NULL,
                        whiteboard BOOLEAN NOT NULL,
                        monitor BOOLEAN NOT NULL,
                        hdmi_cable BOOLEAN NOT NULL,
                        image TEXT DEFAULT NULL
                    )
                """);
                System.out.println("✅ Created room table");
                
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        email VARCHAR(100) UNIQUE NOT NULL,
                        full_name VARCHAR(100) NOT NULL,
                        student_id VARCHAR(20) UNIQUE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("✅ Created users table");
                
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS booktime (
                        timeID INT AUTO_INCREMENT PRIMARY KEY,
                        booking_ref VARCHAR(20) UNIQUE,
                        room_id INT NOT NULL,
                        user_id INT,
                        start_Time TIMESTAMP NOT NULL,
                        end_Time TIMESTAMP NOT NULL,
                        booking_status VARCHAR(20) DEFAULT 'ACTIVE',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        cancelled_at TIMESTAMP NULL,
                        FOREIGN KEY (room_id) REFERENCES room(room_id),
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
                    )
                """);
                
                // Add booking_ref column if it doesn't exist (migration for existing databases)
                try {
                    stmt.execute("ALTER TABLE booktime ADD COLUMN booking_ref VARCHAR(20) UNIQUE");
                    System.out.println("✅ Added booking_ref column to existing booktime table");
                } catch (SQLException e) {
                    if (e.getMessage().contains("already exists") || e.getMessage().contains("Duplicate column")) {
                        System.out.println("✅ booking_ref column already exists");
                    } else {
                        System.out.println("⚠️  Note: " + e.getMessage());
                    }
                }
                System.out.println("✅ Created booktime table");
                
                // Insert sample data using MERGE to avoid duplicates
                stmt.execute("""
                    MERGE INTO room (room_id, room_name, room_type, capacity, speaker, whiteboard, monitor, hdmi_cable, image) VALUES 
                    (1, 'CB06.06.112', 'Group Study Room', 8, true, true, true, true, 'Group_Study_Room'),
                    (2, 'CB06.06.113', 'Group Study Room', 8, false, false, true, true, 'Group_Study_Room'),
                    (3, 'CB07.02.010A', 'Online Learning Room', 2, false, false, true, true, 'Online_Learning_Room'),
                    (4, 'CB07.02.010B', 'Online Learning Room', 2, true, false, true, true, 'Online_Learning_Room'),
                    (5, 'CB06.03.205', 'Group Study Room', 12, true, true, true, true, 'Group_Study_Room'),
                    (6, 'CB06.03.206', 'Group Study Room', 10, false, true, true, true, 'Group_Study_Room'),
                    (7, 'CB08.01.115', 'Lecture Room', 50, true, true, true, true, 'Lecture_Room'),
                    (8, 'CB08.01.116', 'Lecture Room', 40, true, true, false, true, 'Lecture_Room'),
                    (9, 'CB07.04.020A', 'Online Learning Room', 4, true, false, true, true, 'Online_Learning_Room'),
                    (10, 'CB07.04.020B', 'Online Learning Room', 4, false, false, true, false, 'Online_Learning_Room'),
                    (11, 'CB09.02.301', 'Conference Room', 16, true, true, true, true, 'Conference_Room'),
                    (12, 'CB09.02.302', 'Conference Room', 20, true, true, true, true, 'Conference_Room'),
                    (13, 'CB05.01.108', 'Computer Lab', 24, false, true, true, true, 'Computer_Lab'),
                    (14, 'CB05.01.109', 'Computer Lab', 28, false, true, true, true, 'Computer_Lab'),
                    (15, 'CB06.05.220', 'Group Study Room', 6, false, true, false, true, 'Group_Study_Room')
                """);
                System.out.println("✅ Inserted room data");
                
                // Insert sample users using MERGE
                stmt.execute("""
                    MERGE INTO users (user_id, username, email, full_name, student_id) VALUES 
                    (1, 'adam_p', 'adam.pharrels@student.edu', 'Adam Pharrels', 'ST001'),
                    (2, 'sarah_j', 'sarah.jones@student.edu', 'Sarah Jones', 'ST002'),
                    (3, 'mike_w', 'mike.wilson@student.edu', 'Mike Wilson', 'ST003'),
                    (4, 'emma_d', 'emma.davis@student.edu', 'Emma Davis', 'ST004'),
                    (5, 'john_s', 'john.smith@student.edu', 'John Smith', 'ST005')
                """);
                System.out.println("✅ Inserted user data");
                
                // Insert sample bookings with memorable references using MERGE
                stmt.execute("""
                    MERGE INTO booktime (timeID, booking_ref, room_id, user_id, start_Time, end_Time, booking_status) VALUES 
                    (1, 'CB112-1017E-A', 1, 1, '2025-10-17 16:30:00', '2025-10-17 17:30:00', 'COMPLETED'),
                    (2, 'CB113-1017E-S', 2, 2, '2025-10-17 17:00:00', '2025-10-17 18:00:00', 'COMPLETED'),
                    (3, 'CB010A-1018M-A', 3, 1, '2025-10-18 10:00:00', '2025-10-18 11:00:00', 'COMPLETED'),
                    (4, 'CB010B-1018A-M', 4, 3, '2025-10-18 14:00:00', '2025-10-18 15:00:00', 'COMPLETED'),
                    (5, 'CB112-1021M-A2K', 1, 1, '2025-10-21 09:00:00', '2025-10-21 10:00:00', 'ACTIVE'),
                    (6, 'CB205-1021A-S5X', 5, 2, '2025-10-21 14:00:00', '2025-10-21 16:00:00', 'ACTIVE'),
                    (7, 'CB115-1022M-A7F', 7, 1, '2025-10-22 11:00:00', '2025-10-22 12:00:00', 'ACTIVE')
                """);
                System.out.println("✅ Inserted booking data");
                
            } catch (SQLException fallbackError) {
                if (!fallbackError.getMessage().contains("already exists") && 
                    !fallbackError.getMessage().contains("Duplicate key")) {
                    System.err.println("⚠️  Fallback table creation warning: " + fallbackError.getMessage());
                }
            }
            
            // Test the database
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM room");
            if (rs.next()) {
                int roomCount = rs.getInt("count");
                System.out.println("✅ H2 Database initialized successfully! Found " + roomCount + " rooms.");
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM booktime");
            if (rs.next()) {
                int bookingCount = rs.getInt("count");
                System.out.println("✅ Found " + bookingCount + " bookings in database.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Database setup failed: " + e.getMessage());
        }
    }
}