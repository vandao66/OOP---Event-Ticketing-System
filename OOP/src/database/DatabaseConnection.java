package database;

import java.sql.*;
import java.nio.file.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:database/event_ticketing.db";

    public static Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(Paths.get("database"));
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            throw new SQLException("Không thể kết nối SQLite: " + e.getMessage(), e);
        }
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void createTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "username TEXT PRIMARY KEY, "
                    + "password TEXT NOT NULL, "
                    + "role TEXT NOT NULL CHECK(role IN ('Admin','Staff','Customer'))"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS events ("
                    + "event_id TEXT PRIMARY KEY, "
                    + "event_name TEXT NOT NULL, "
                    + "event_price INTEGER NOT NULL CHECK(event_price >= 0), "
                    + "standard_count INTEGER NOT NULL CHECK(standard_count >= 0), "
                    + "vip_count INTEGER NOT NULL CHECK(vip_count >= 0), "
                    + "vip_surcharge_percent INTEGER NOT NULL CHECK(vip_surcharge_percent >= 0), "
                    + "standard_perks TEXT NOT NULL, "
                    + "vip_perks TEXT NOT NULL, "
                    + "CHECK(standard_count + vip_count > 0)"
                    + ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS tickets ("
                    + "ticket_id TEXT PRIMARY KEY, "
                    + "username TEXT NOT NULL, "
                    + "event_id TEXT NOT NULL, "
                    + "seat_number TEXT NOT NULL, "
                    + "ticket_type TEXT NOT NULL CHECK(ticket_type IN ('STANDARD','VIP')), "
                    + "is_used INTEGER NOT NULL DEFAULT 0 CHECK(is_used IN (0,1)), "
                    + "UNIQUE(event_id, seat_number), "
                    + "FOREIGN KEY(username) REFERENCES users(username) ON DELETE CASCADE, "
                    + "FOREIGN KEY(event_id) REFERENCES events(event_id) ON DELETE RESTRICT"
                    + ")");
        }
    }
}