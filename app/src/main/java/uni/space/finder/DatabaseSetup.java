package uni.space.finder;

import java.sql.*;
import java.nio.file.*;

public class DatabaseSetup {
    private static final String DB_URL = "jdbc:h2:mem:asd;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Try to read the external asd.sql file from project root
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
                    CREATE TABLE IF NOT EXISTS booktime (
                        timeID INT PRIMARY KEY,
                        room_id INT NOT NULL,
                        start_Time TIMESTAMP NOT NULL,
                        end_Time TIMESTAMP NOT NULL
                    )
                """);
                System.out.println("✅ Created booktime table");
                
                // Insert sample data
                stmt.execute("""
                    INSERT INTO room VALUES 
                    (1, 'CB06.06.112', 'Group Study Room', 8, true, true, true, true, 'Group_Study_Room'),
                    (2, 'CB06.06.113', 'Group Study Room', 8, false, false, true, true, 'Group_Study_Room'),
                    (3, 'CB07.02.010A', 'Online Learning Room', 2, false, false, true, true, 'Online_Learning_Room'),
                    (4, 'CB07.02.010B', 'Online Learning Room', 2, true, false, true, true, 'Online_Learning_Room')
                """);
                System.out.println("✅ Inserted room data");
                
                // Insert sample bookings with current and future dates
                stmt.execute("""
                    INSERT INTO booktime VALUES 
                    (1, 1, '2025-10-17 16:30:00', '2025-10-17 17:30:00'),
                    (2, 2, '2025-10-17 17:00:00', '2025-10-17 18:00:00'),
                    (3, 3, '2025-10-18 10:00:00', '2025-10-18 11:00:00'),
                    (4, 4, '2025-10-18 14:00:00', '2025-10-18 15:00:00'),
                    (5, 1, '2025-10-18 09:00:00', '2025-10-18 10:00:00')
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