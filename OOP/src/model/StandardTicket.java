package model;

public class StandardTicket extends Ticket {
    public StandardTicket(Customer customer, String ticketID, Seat ticketSeat, Event event) {
        super(customer, ticketID, ticketSeat, event);
    }

    @Override
    public String getTicketType() { return TicketType.STANDARD.getDisplayName(); }

    @Override
    public int calculateFinalPrice() { return getEvent().getEventPrice(); }

    @Override
    public String getPerks() { return getEvent().getStandardPerks(); }
}