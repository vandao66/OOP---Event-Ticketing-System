
package services;

import database.DatabaseManager;
import exception.*;
import model.*;
import java.util.*;

public class Booking {
    private final DatabaseManager db;
    private final EventService eventService;

    public Booking(DatabaseManager db, EventService eventService) {
        this.db = db;
        this.eventService = eventService;
    }

    public List<Ticket> bookTickets(Customer customer, String eventID, TicketType type, int quantity)
            throws EventNotFoundException, SoldOutException, ValidationException {
        if (customer == null || type == null || quantity <= 0)
            throw new ValidationException("Thông tin đặt vé không hợp lệ!");

        Event event = eventService.getEventById(eventID);
        int available = (type == TicketType.VIP) ? event.getAvailableVipCount() : event.getAvailableStandardCount();
        if (quantity > available)
            throw new SoldOutException("Không đủ vé trống! Chỉ còn " + available + " vé " + type.getDisplayName());

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Seat seat = event.bookNextAvailableSeat(type);
            String ticketID = "TK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            tickets.add(TicketFactory.createTicket(type, customer, ticketID, seat, event));
        }

        try {
            db.saveTickets(tickets);
            return tickets;
        } catch (RuntimeException e) {
            for (Ticket t : tickets) {
                event.releaseSeat(t.getTicketSeat().getSeatNumber());
            }
            throw e;
        }
    }

    public void cancelTicket(Customer customer, String ticketID)
            throws TicketNotFoundException, ValidationException {
        if (customer == null)
            throw new ValidationException("Người yêu cầu hủy vé không hợp lệ!");
        if (ticketID == null || ticketID.trim().isEmpty())
            throw new TicketNotFoundException("Mã vé không hợp lệ!");

        String cleanID = ticketID.trim();
        Ticket ticket = db.getTicket(cleanID);

        if (ticket == null)
            throw new TicketNotFoundException("Không tìm thấy mã vé: " + cleanID);

        if (!ticket.getCustomer().getUsername().equals(customer.getUsername()))
            throw new ValidationException("Bạn không có quyền hủy vé này!");

        if (ticket.isUsed())
            throw new ValidationException("Vé đã check-in, không thể hủy!");

        if (!db.cancelUnusedTicket(cleanID)) {
            if (db.getTicket(cleanID) == null)
                throw new TicketNotFoundException("Không tìm thấy mã vé: " + cleanID);
            throw new ValidationException("Vé đã check-in, không thể hủy!");
        }
    }

    public List<Ticket> getTicketByUsername(String username) {
        List<Ticket> list = new ArrayList<>();
        for (Ticket ticket : db.getAllTickets()) {
            if (ticket.getCustomer().getUsername().equals(username))
                list.add(ticket);
        }
        return list;
    }

    public boolean hasTicketsForEvent(String eventID) {
        for (Ticket ticket : db.getAllTickets()) {
            if (ticket.getEvent().getEventID().equalsIgnoreCase(eventID))
                return true;
        }
        return false;
    }
}
