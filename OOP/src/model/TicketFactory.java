package model;

public class TicketFactory {
    public static Ticket createTicket(TicketType type, Customer customer,
                                      String ticketID, Seat seat, Event event) {
        if (type == null)
            throw new IllegalArgumentException("Loại vé không hợp lệ!");
        if (type == TicketType.VIP)
            return new VIPTicket(customer, ticketID, seat, event);
        return new StandardTicket(customer, ticketID, seat, event);
    }
}