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
                
                // Execute SQL statements
                String[] statements = sqlContent.split(";");
                for (String statement : statements) {
                    statement = statement.trim();
                    if (!statement.isEmpty() && !statement.startsWith("--") && !statement.startsWith("/*")) {
                        try {
                            stmt.execute(statement);
                        } catch (SQLException e) {
                            // Skip non-critical errors (like duplicate inserts)
                            if (!e.getMessage().contains("already exists")) {
                                System.err.println("⚠️  SQL Warning: " + e.getMessage());
                            }
                        }
                    }
                }
            } else {
                System.out.println("⚠️  asd.sql not found, creating tables manually...");
                
                // Fallback: create tables manually
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
                
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS booktime (
                        timeID INT PRIMARY KEY,
                        room_id INT NOT NULL,
                        start_Time TIMESTAMP NOT NULL,
                        end_Time TIMESTAMP NOT NULL
                    )
                """);
                
                // Insert sample data
                stmt.execute("""
                    INSERT INTO room VALUES 
                    (1, 'CB06.06.112', 'Group Study Room', 8, true, true, true, true, 'Group_Study_Room'),
                    (2, 'CB06.06.113', 'Group Study Room', 8, false, false, true, true, 'Group_Study_Room'),
                    (3, 'CB07.02.010A', 'Online Learning Room', 2, false, false, true, true, 'Online_Learning_Room'),
                    (4, 'CB07.02.010B', 'Online Learning Room', 2, true, false, true, true, 'Online_Learning_Room')
                """);
                
                stmt.execute("""
                    INSERT INTO booktime VALUES 
                    (1, 1, '2025-10-01 12:30:21', '2025-10-01 13:00:00'),
                    (2, 4, '2025-10-01 14:24:00', '2025-10-01 15:30:00'),
                    (3, 4, '2025-10-01 15:30:00', '2025-10-01 17:00:00'),
                    (4, 2, '2025-10-02 15:16:48', '2025-10-02 16:16:48')
                """);
            }
            
            System.out.println("✅ H2 Database initialized successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Database setup failed: " + e.getMessage());
        }
    }
}