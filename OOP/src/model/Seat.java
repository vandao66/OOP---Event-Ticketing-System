package model;

public class Seat {
    private final String seatNumber;
    private boolean isBooked;

    public Seat(String seatNumber) {
        if (seatNumber == null || seatNumber.trim().isEmpty())
            throw new IllegalArgumentException("Mã ghế không được để trống!");
        this.seatNumber = seatNumber.trim();
        this.isBooked = false;
    }

    public String getSeatNumber() { return seatNumber; }
    public boolean isBooked() { return isBooked; }
    public void book() { isBooked = true; }
    public void release() { isBooked = false; }

    @Override
    public String toString() {
        return seatNumber + (isBooked ? " [Đã đặt]" : " [Còn trống]");
    }
}