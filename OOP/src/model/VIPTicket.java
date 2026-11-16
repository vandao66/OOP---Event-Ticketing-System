package model;

public class VIPTicket extends Ticket {
    public VIPTicket(Customer customer, String ticketID, Seat ticketSeat, Event event) {
        super(customer, ticketID, ticketSeat, event);
    }

    @Override
    public String getTicketType() { return TicketType.VIP.getDisplayName(); }

    @Override
    public int calculateFinalPrice() { return getEvent().getVipPrice(); }

    @Override
    public String getPerks() { return getEvent().getVipPerks(); }
}