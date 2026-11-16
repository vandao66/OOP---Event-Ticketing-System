package database;

import model.*;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    public DatabaseManager() {
        try { DatabaseConnection.createTables(); }
        catch (SQLException e) { throw new RuntimeException("Không thể khởi tạo Database: " + e.getMessage(), e); }
    }

    // =============== USER ===============
    public void saveUser(User user) {
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.role());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Lỗi lưu người dùng: " + e.getMessage(), e); }
    }

    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return readUser(rs);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi tìm người dùng: " + e.getMessage(), e); }
        return null;
    }

    public boolean userExists(String username) { return getUser(username) != null; }

    private User readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username"), password = rs.getString("password"), role = rs.getString("role");
        if (role.equals("Admin")) return new Admin(username, password);
        if (role.equals("Staff")) return new Staff(username, password);
        if (role.equals("Customer")) return new Customer(username, password);
        throw new SQLException("Vai trò không hợp lệ: " + role);
    }

    // =============== EVENT ===============
    public void saveEvent(Event event) {
        String sql = "INSERT INTO events(event_id, event_name, event_price, standard_count, vip_count, vip_surcharge_percent, standard_perks, vip_perks) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getEventID());
            ps.setString(2, event.getEventName());
            ps.setInt(3, event.getEventPrice());
            ps.setInt(4, event.getTotalStandardCount());
            ps.setInt(5, event.getTotalVipCount());
            ps.setInt(6, event.getVipSurchargePercent());
            ps.setString(7, event.getStandardPerks());
            ps.setString(8, event.getVipPerks());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Lỗi lưu sự kiện: " + e.getMessage(), e); }
    }

    public Event getEvent(String eventID) {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Event event = readEvent(rs);
                    loadBookedSeats(conn, event);
                    return event;
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi tìm sự kiện: " + e.getMessage(), e); }
        return null;
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_id";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Event event = readEvent(rs);
                loadBookedSeats(conn, event);
                list.add(event);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi lấy danh sách sự kiện: " + e.getMessage(), e); }
        return list;
    }

    public boolean deleteEvent(String eventID) {
        String sql = "DELETE FROM events WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Lỗi xóa sự kiện: " + e.getMessage(), e); }
    }

    public boolean eventExists(String eventID) { return getEvent(eventID) != null; }

    private Event readEvent(ResultSet rs) throws SQLException {
        return new Event(rs.getString("event_id"), rs.getString("event_name"), rs.getInt("event_price"),
                rs.getInt("standard_count"), rs.getInt("vip_count"), rs.getInt("vip_surcharge_percent"),
                rs.getString("standard_perks"), rs.getString("vip_perks"));
    }

    private void loadBookedSeats(Connection conn, Event event) throws SQLException {
        String sql = "SELECT seat_number FROM tickets WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getEventID());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) event.markSeatAsBooked(rs.getString("seat_number"));
            }
        }
    }

    // =============== TICKET ===============
    // INSERT một nhóm vé trong cùng giao dịch: hoặc lưu hết, hoặc không lưu vé nào.
    public void saveTickets(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) throw new IllegalArgumentException("Danh sách vé trống!");
        String sql = "INSERT INTO tickets(ticket_id, username, event_id, seat_number, ticket_type, is_used) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Ticket ticket : tickets) {
                    ps.setString(1, ticket.getTicketID());
                    ps.setString(2, ticket.getCustomer().getUsername());
                    ps.setString(3, ticket.getEvent().getEventID());
                    ps.setString(4, ticket.getTicketSeat().getSeatNumber());
                    ps.setString(5, ticket instanceof VIPTicket ? "VIP" : "STANDARD");
                    ps.setInt(6, ticket.isUsed() ? 1 : 0);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                try { conn.rollback(); }
                catch (SQLException ex) { e.addSuppressed(ex); }
                throw new RuntimeException("Không thể lưu toàn bộ vé: " + e.getMessage(), e);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi giao dịch đặt vé: " + e.getMessage(), e); }
    }

    public Ticket getTicket(String ticketID) {
        String sql = "SELECT t.*, u.password FROM tickets t JOIN users u ON t.username = u.username WHERE t.ticket_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return readTicket(rs);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi tìm vé: " + e.getMessage(), e); }
        return null;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT t.*, u.password FROM tickets t JOIN users u ON t.username = u.username ORDER BY t.ticket_id";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(readTicket(rs));
        } catch (SQLException e) { throw new RuntimeException("Lỗi lấy danh sách vé: " + e.getMessage(), e); }
        return list;
    }

    // Dành cho chức năng hủy: chỉ xóa nếu vé chưa check-in.
    public boolean cancelUnusedTicket(String ticketID) {
        String sql = "DELETE FROM tickets WHERE ticket_id = ? AND is_used = 0";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Lỗi hủy vé: " + e.getMessage(), e); }
    }

    // Hàm xóa chung, hữu ích cho dọn dữ liệu kiểm thử.
    public boolean deleteTicket(String ticketID) {
        String sql = "DELETE FROM tickets WHERE ticket_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Lỗi xóa vé: " + e.getMessage(), e); }
    }

    // Đổi trạng thái check-in bằng một lệnh UPDATE có điều kiện.
    public boolean markTicketUsed(String ticketID) {
        String sql = "UPDATE tickets SET is_used = 1 WHERE ticket_id = ? AND is_used = 0";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Lỗi check-in: " + e.getMessage(), e); }
    }

    public boolean ticketExists(String ticketID) { return getTicket(ticketID) != null; }

    private Ticket readTicket(ResultSet rs) throws SQLException {
        Event event = getEvent(rs.getString("event_id"));
        if (event == null) throw new SQLException("Không tìm thấy sự kiện của vé!");
        Seat seat = event.getSeatByNumber(rs.getString("seat_number"));
        if (seat == null) throw new SQLException("Không tìm thấy ghế của vé!");
        Customer customer = new Customer(rs.getString("username"), rs.getString("password"));
        TicketType type = TicketType.valueOf(rs.getString("ticket_type"));
        Ticket ticket = TicketFactory.createTicket(type, customer, rs.getString("ticket_id"), seat, event);
        if (rs.getInt("is_used") == 1) ticket.markAsUsed();
        return ticket;
    }
}
