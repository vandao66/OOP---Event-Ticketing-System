package services;

import database.DatabaseManager;
import exception.EventAlreadyExistsException;
import exception.EventNotFoundException;
import model.Event;
import java.util.List;
import java.util.Locale;

public class EventService {
    private final DatabaseManager db;

    public EventService(DatabaseManager db) {
        this.db = db;
        seedSampleEvents();
    }
    // dữ liệu mẫu , xóa thì phải xóa cả lời gọi hàm ở trên 
    private void seedSampleEvents() {
        try {
            if (!db.eventExists("EV01"))
                addEvent("EV01", "Concert Anh Trai Say Hi", 500000, 20, 10, 50,
                        "Khán đài tiêu chuẩn, vòng tay phát sáng",
                        "Khu Fanzone sát sân khấu, lối vào riêng, tặng Lightstick");

            if (!db.eventExists("EV02"))
                addEvent("EV02", "Đêm Nhạc Acoustic PTIT", 150000, 25, 8, 20,
                        "Ghế ngồi khu sau, cổng check-in tiêu chuẩn",
                        "Ghế hàng đầu, giao lưu ca sĩ, miễn phí đồ uống");

            if (!db.eventExists("EV03"))
                addEvent("EV03", "RockStorm Music Festival", 300000, 30, 12, 40,
                        "Khu đứng tiêu chuẩn, cổng vào chung",
                        "Khu VIP sát sân khấu, lối đi ưu tiên, tặng áo thun");
        } catch (EventAlreadyExistsException e) {
            throw new IllegalStateException("Không thể tạo sự kiện mẫu!", e);
        }
    }

    public void addEvent(String eventID, String eventName, int eventPrice,
                         int standardSeats, int vipSeats, int vipSurchargePercent,
                         String standardPerks, String vipPerks)
                         throws EventAlreadyExistsException {
        if (eventID == null || eventID.trim().isEmpty())
            throw new IllegalArgumentException("Mã sự kiện không được để trống!");

        String cleanID = eventID.trim().toUpperCase(Locale.ROOT);
        if (db.eventExists(cleanID))
            throw new EventAlreadyExistsException("Sự kiện " + cleanID + " đã tồn tại!");

        Event event = new Event(cleanID, eventName, eventPrice, standardSeats,
                vipSeats, vipSurchargePercent, standardPerks, vipPerks);
        db.saveEvent(event);
    }

    public Event getEventById(String eventID) throws EventNotFoundException {
        if (eventID == null || eventID.trim().isEmpty())
            throw new EventNotFoundException("Mã sự kiện không hợp lệ!");

        String cleanID = eventID.trim().toUpperCase(Locale.ROOT);
        Event event = db.getEvent(cleanID);
        if (event == null)
            throw new EventNotFoundException("Không tìm thấy sự kiện: " + cleanID);
        return event;
    }

    public List<Event> getAllEvent() {
        return db.getAllEvents();
    }

    public void deleteEvent(String eventID) throws EventNotFoundException {
        if (eventID == null || eventID.trim().isEmpty())
            throw new EventNotFoundException("Mã sự kiện không hợp lệ!");

        String cleanID = eventID.trim().toUpperCase(Locale.ROOT);
        if (!db.deleteEvent(cleanID))
            throw new EventNotFoundException("Không tìm thấy sự kiện: " + cleanID);
    }
}