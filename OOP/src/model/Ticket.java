package model;

public abstract class Ticket {
    private final Customer customer;
    private final String ticketID;
    private final Seat ticketSeat;
    private final Event event;
    private boolean isUsed;

    public Ticket(Customer customer, String ticketID, Seat ticketSeat, Event event) {
        if (customer == null || ticketSeat == null || event == null)
            throw new IllegalArgumentException("Thông tin vé không hợp lệ!");
        if (ticketID == null || ticketID.trim().isEmpty())
            throw new IllegalArgumentException("Mã vé không được để trống!");
        this.customer = customer;
        this.ticketID = ticketID.trim();
        this.ticketSeat = ticketSeat;
        this.event = event;
        this.isUsed = false;
    }

    public Customer getCustomer() { return customer; }
    public String getTicketID() { return ticketID; }
    public Seat getTicketSeat() { return ticketSeat; }
    public Event getEvent() { return event; }
    public boolean isUsed() { return isUsed; }
    public void markAsUsed() { isUsed = true; }

    public abstract String getTicketType();
    public abstract int calculateFinalPrice();
    public abstract String getPerks();

    @Override
    public String toString() {
        String status = isUsed ? "[ĐÃ CHECK-IN]" : "[CHƯA DÙNG]";
        return String.format("Mã vé: %-7s | Loại: %-8s | Khách: %-10s | Sự kiện: %-20s | Ghế: %-6s | Giá: %,d VND | %s",
                ticketID, getTicketType(), customer.getUsername(), event.getEventName(),
                ticketSeat.getSeatNumber(), calculateFinalPrice(), status);
    }
}