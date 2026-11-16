package model;

import java.util.ArrayList;
import java.util.List;

public class Event {
    public static final String DEFAULT_STD_PERKS = "Khu vực tiêu chuẩn";
    public static final String DEFAULT_VIP_PERKS = "Khu vực VIP, lối vào ưu tiên";

    private final String eventID, eventName;
    private final int eventPrice, vipSurchargePercent;
    private final String standardPerks, vipPerks;
    private final List<Seat> standardSeats = new ArrayList<>();
    private final List<Seat> vipSeats = new ArrayList<>();

    public Event(String eventID, String eventName, int eventPrice,
                 int standardCount, int vipCount, int vipSurchargePercent,
                 String standardPerks, String vipPerks) {
        if (eventID == null || eventID.trim().isEmpty())
            throw new IllegalArgumentException("Mã sự kiện không được để trống!");
        if (eventName == null || eventName.trim().isEmpty())
            throw new IllegalArgumentException("Tên sự kiện không được để trống!");
        if (eventPrice < 0 || standardCount < 0 || vipCount < 0 || vipSurchargePercent < 0)
            throw new IllegalArgumentException("Giá, số ghế và phụ thu không được âm!");
        if (standardCount > 1000)
            throw new IllegalArgumentException("Số ghế thường tối đa là 1000!");
        if (vipCount > 1000)
            throw new IllegalArgumentException("Số ghế VIP tối đa là 1000!");
        if (eventPrice > 100_000_000)
            throw new IllegalArgumentException("Giá vé thường tối đa là 100 triệu VND!");
        if (vipSurchargePercent > 100)
            throw new IllegalArgumentException("Phụ thu VIP không được vượt quá 100%!");
        if (standardCount == 0 && vipCount == 0)
            throw new IllegalArgumentException("Sự kiện phải có ít nhất một ghế!");

        this.eventID = eventID.trim();
        this.eventName = eventName.trim();
        this.eventPrice = eventPrice;
        this.vipSurchargePercent = vipSurchargePercent;
        this.standardPerks = (standardPerks == null || standardPerks.trim().isEmpty())
                ? DEFAULT_STD_PERKS : standardPerks.trim();
        this.vipPerks = (vipPerks == null || vipPerks.trim().isEmpty())
                ? DEFAULT_VIP_PERKS : vipPerks.trim();

        for (int i = 1; i <= standardCount; i++)
            standardSeats.add(new Seat(String.format("STD-%02d", i)));
        for (int i = 1; i <= vipCount; i++)
            vipSeats.add(new Seat(String.format("VIP-%02d", i)));
    }

    // Getters
    public String getEventID() { return eventID; }
    public String getEventName() { return eventName; }
    public int getEventPrice() { return eventPrice; }
    public int getVipSurchargePercent() { return vipSurchargePercent; }
    public String getStandardPerks() { return standardPerks; }
    public String getVipPerks() { return vipPerks; }
    public int getTotalStandardCount() { return standardSeats.size(); }
    public int getTotalVipCount() { return vipSeats.size(); }

    public int getVipPrice() {
        return eventPrice + (int) ((long) eventPrice * vipSurchargePercent / 100);
    }

    // Đếm số ghế còn trống
    public int getAvailableStandardCount() {
        int count = 0;
        for (Seat seat : standardSeats)
            if (!seat.isBooked()) count++;
        return count;
    }

    public int getAvailableVipCount() {
        int count = 0;
        for (Seat seat : vipSeats)
            if (!seat.isBooked()) count++;
        return count;
    }

    // Tìm ghế theo mã
    public Seat getSeatByNumber(String seatNumber) {
        if (seatNumber == null) return null;
        for (Seat seat : standardSeats)
            if (seat.getSeatNumber().equalsIgnoreCase(seatNumber)) return seat;
        for (Seat seat : vipSeats)
            if (seat.getSeatNumber().equalsIgnoreCase(seatNumber)) return seat;
        return null;
    }

    // Đặt ghế trống đầu tiên theo loại vé
    public Seat bookNextAvailableSeat(TicketType type) {
        if (type == null)
            throw new IllegalArgumentException("Loại vé không hợp lệ!");
        List<Seat> targetSeats = (type == TicketType.VIP) ? vipSeats : standardSeats;
        for (Seat seat : targetSeats) {
            if (!seat.isBooked()) {
                seat.book();
                return seat;
            }
        }
        return null;
    }

    // Đánh dấu một ghế đã được đặt
    public void markSeatAsBooked(String seatNumber) {
        Seat seat = getSeatByNumber(seatNumber);
        if (seat != null) seat.book();
    }

    // Giải phóng ghế khi hủy vé
    public boolean releaseSeat(String seatNumber) {
        Seat seat = getSeatByNumber(seatNumber);
        if (seat != null && seat.isBooked()) {
            seat.release();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s - %s | Giá thường: %,d | Giá VIP: %,d",
                eventID, eventName, eventPrice, getVipPrice());
    }
}