package services;

import database.DatabaseManager;
import exception.*;
import model.Ticket;

public class Checkin {
    private final DatabaseManager db;
    public Checkin(DatabaseManager db) { this.db = db; }

    public Ticket verifyAndCheckin(String ticketID) throws TicketNotFoundException, TicketAlreadyUsedException {
        if (ticketID == null || ticketID.trim().isEmpty())
            throw new TicketNotFoundException("Vui lòng nhập mã vé!");
        String cleanID = ticketID.trim();
        Ticket ticket = db.getTicket(cleanID);
        if (ticket == null) throw new TicketNotFoundException("Không tìm thấy mã vé: " + cleanID);
        if (ticket.isUsed()) throw new TicketAlreadyUsedException("Vé " + cleanID + " đã được sử dụng!");
        if (!db.markTicketUsed(cleanID)) {
            if (db.getTicket(cleanID) == null) throw new TicketNotFoundException("Không tìm thấy mã vé: " + cleanID);
            throw new TicketAlreadyUsedException("Vé " + cleanID + " đã được sử dụng!");
        }
        ticket.markAsUsed();
        return ticket;
    }
}
