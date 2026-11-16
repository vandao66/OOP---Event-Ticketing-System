
package view.gui;

import model.Event;
import model.User;
import services.Booking;
import services.EventService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigInteger;
import java.util.List;

public class AdminPanel extends JPanel {
    private final EventService eventService;
    private final Booking booking;
    private final DefaultTableModel eventModel;
    private final JTable eventTable;
    private final JLabel totalEvents = UIStyle.label("0", 23, true, UIStyle.BLUE);
    private final JLabel totalStandard = UIStyle.label("0", 23, true, UIStyle.GREEN);
    private final JLabel totalVip = UIStyle.label("0", 23, true, UIStyle.BLUE);
    private final JLabel detailTitle = UIStyle.label("Chọn sự kiện để xem quyền lợi", 15, true, UIStyle.NAVY);
    private final JTextArea standardPerks = UIStyle.textArea("");
    private final JTextArea vipPerks = UIStyle.textArea("");

    public AdminPanel(EventService eventService, Booking booking, User user, Runnable onLogout) {
        this.eventService = eventService; this.booking = booking;
        setLayout(new BorderLayout()); setBackground(UIStyle.BG);
        add(UIStyle.header("ADMIN DASHBOARD", "Quản lý sự kiện", "Theo dõi số ghế và quyền lợi của từng hạng vé", user.getUsername(), onLogout), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false); content.setBorder(BorderFactory.createEmptyBorder(18, 25, 22, 25));
        JPanel top = new JPanel(new BorderLayout(0, 14)); top.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0)); stats.setOpaque(false);
        stats.add(createMetric("SỰ KIỆN ĐANG CÓ", totalEvents));
        stats.add(createMetric("GHẾ THƯỜNG CÒN TRỐNG", totalStandard));
        stats.add(createMetric("GHẾ VIP CÒN TRỐNG", totalVip));
        top.add(stats, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new BorderLayout()); toolbar.setOpaque(false);
        toolbar.add(UIStyle.label("Danh sách sự kiện", 18, true, UIStyle.NAVY), BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); actions.setOpaque(false);
        JButton addButton = UIStyle.button("+ Thêm sự kiện");
        JButton deleteButton = UIStyle.danger("Xóa sự kiện");
        JButton refreshButton = UIStyle.quiet("Làm mới");
        actions.add(addButton); actions.add(deleteButton); actions.add(refreshButton);
        toolbar.add(actions, BorderLayout.EAST); top.add(toolbar, BorderLayout.SOUTH);
        content.add(top, BorderLayout.NORTH);

        String[] cols = {"Mã SK", "Tên sự kiện", "Giá thường", "Giá VIP", "Ghế thường trống", "Ghế VIP trống", "Phụ thu VIP"};
        eventModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        eventTable = UIStyle.table(eventModel);
        JPanel tableCard = UIStyle.card(new BorderLayout());
        tableCard.add(UIStyle.scroll(eventTable), BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);

        JPanel details = UIStyle.card(new BorderLayout(0, 10));
        details.add(detailTitle, BorderLayout.NORTH);
        JPanel perks = new JPanel(new GridLayout(1, 2, 18, 0)); perks.setOpaque(false);
        perks.add(perkBlock("VÉ THƯỜNG", standardPerks, UIStyle.GREEN));
        perks.add(perkBlock("VÉ VIP", vipPerks, UIStyle.BLUE));
        details.add(perks, BorderLayout.CENTER);
        details.setPreferredSize(new Dimension(200, 175));
        content.add(details, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);

        eventTable.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateDetails(); });
        addButton.addActionListener(e -> showAddDialog());
        deleteButton.addActionListener(e -> deleteSelectedEvent());
        refreshButton.addActionListener(e -> refreshData());
        refreshData();
    }

    private JPanel createMetric(String caption, JLabel number) {
        JPanel card = UIStyle.card(new BorderLayout(0, 6));
        card.add(UIStyle.label(caption, 12, true, UIStyle.MUTED), BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER); return card;
    }

    private JPanel perkBlock(String title, JTextArea description, Color accent) {
        JPanel block = new JPanel(new BorderLayout(0, 6)); block.setOpaque(false);
        block.add(UIStyle.label(title, 12, true, accent), BorderLayout.NORTH);
        block.add(description, BorderLayout.CENTER); return block;
    }

    private String selectedEventId() {
        int row = eventTable.getSelectedRow();
        return row < 0 ? null : (String) eventModel.getValueAt(eventTable.convertRowIndexToModel(row), 0);
    }

    private void updateDetails() {
        String id = selectedEventId();
        if (id == null) {
            detailTitle.setText("Chọn sự kiện để xem quyền lợi");
            standardPerks.setText(""); vipPerks.setText(""); return;
        }
        try {
            Event event = eventService.getEventById(id);
            detailTitle.setText("Quyền lợi: " + event.getEventName());
            standardPerks.setText(event.getStandardPerks());
            vipPerks.setText(event.getVipPerks());
        } catch (Exception e) {
            detailTitle.setText("Không tải được thông tin sự kiện");
            standardPerks.setText(""); vipPerks.setText("");
        }
    }

    private void showAddDialog() {
        JTextField id = UIStyle.input(new JTextField());
        JTextField name = UIStyle.input(new JTextField());
        JTextField price = UIStyle.input(new JTextField());
        JTextField stdCount = UIStyle.input(new JTextField());
        JTextField vipCount = UIStyle.input(new JTextField());
        JTextField percent = UIStyle.input(new JTextField("20"));
        JTextField stdPerks = UIStyle.input(new JTextField(Event.DEFAULT_STD_PERKS));
        JTextField vipPerks = UIStyle.input(new JTextField(Event.DEFAULT_VIP_PERKS));

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12)); form.setBackground(UIStyle.WHITE);
        form.add(new JLabel("Mã sự kiện")); form.add(id);
        form.add(new JLabel("Tên sự kiện")); form.add(name);
        form.add(new JLabel("Giá vé thường (VND)")); form.add(price);
        form.add(new JLabel("Số ghế thường")); form.add(stdCount);
        form.add(new JLabel("Số ghế VIP")); form.add(vipCount);
        form.add(new JLabel("Phụ thu VIP (%)")); form.add(percent);
        form.add(new JLabel("Quyền lợi thường")); form.add(stdPerks);
        form.add(new JLabel("Quyền lợi VIP")); form.add(vipPerks);

        int choice = JOptionPane.showConfirmDialog(this, form, "Thêm sự kiện mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        try {
            BigInteger inputPrice = new BigInteger(price.getText().trim());

            if (inputPrice.signum() < 0)
                throw new IllegalArgumentException("Giá vé không được âm!");
            if (inputPrice.compareTo(BigInteger.valueOf(100_000_000)) > 0)
                throw new IllegalArgumentException("Giá vé thường tối đa là 100 triệu VND!");

            int eventPrice = inputPrice.intValueExact();
            BigInteger inputStd = new BigInteger(stdCount.getText().trim());
            BigInteger inputVip = new BigInteger(vipCount.getText().trim());

            if (inputStd.signum() < 0)
                throw new IllegalArgumentException("Số ghế thường không được âm!");
            if (inputVip.signum() < 0)
                throw new IllegalArgumentException("Số ghế VIP không được âm!");

            if (inputStd.compareTo(BigInteger.valueOf(1000)) > 0)
                throw new IllegalArgumentException("Số ghế thường tối đa là 1000!");
            if (inputVip.compareTo(BigInteger.valueOf(1000)) > 0)
                throw new IllegalArgumentException("Số ghế VIP tối đa là 1000!");

            int standardSeats = inputStd.intValueExact();
            int vipSeats = inputVip.intValueExact();

            BigInteger inputPercent = new BigInteger(percent.getText().trim());
            if (inputPercent.signum() < 0)
                throw new IllegalArgumentException("Phụ thu VIP không được âm!");
            if (inputPercent.compareTo(BigInteger.valueOf(100)) > 0)
                throw new IllegalArgumentException("Phụ thu VIP không được vượt quá 100%!");
            int vipPercent = inputPercent.intValueExact();

            eventService.addEvent(id.getText(), name.getText(), eventPrice,
                    standardSeats, vipSeats, vipPercent, stdPerks.getText(), vipPerks.getText());

            JOptionPane.showMessageDialog(this, "Đã thêm sự kiện thành công!");
            refreshData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá vé, số ghế và phụ thu phải là số nguyên hợp lệ!", "Sai dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Không thể thêm sự kiện", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedEvent() {
        String id = selectedEventId();
        if (id == null) { JOptionPane.showMessageDialog(this, "Hãy chọn sự kiện muốn xóa!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn xóa sự kiện " + id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            if (booking.hasTicketsForEvent(id)) {
                JOptionPane.showMessageDialog(this, "Không thể xóa: sự kiện đã có người đặt vé!");
                return;
            }
            eventService.deleteEvent(id);
            JOptionPane.showMessageDialog(this, "Đã xóa sự kiện.");
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Không thể xóa sự kiện", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        String selected = selectedEventId();
        List<Event> events = eventService.getAllEvent();
        eventModel.setRowCount(0);
        int std = 0, vip = 0;
        for (Event event : events) {
            std += event.getAvailableStandardCount();
            vip += event.getAvailableVipCount();
            eventModel.addRow(new Object[]{event.getEventID(), event.getEventName(),
                    String.format("%,d đ", event.getEventPrice()), String.format("%,d đ", event.getVipPrice()),
                    event.getAvailableStandardCount(), event.getAvailableVipCount(),
                    "+" + event.getVipSurchargePercent() + "%"});
        }
        totalEvents.setText(String.valueOf(events.size()));
        totalStandard.setText(String.valueOf(std));
        totalVip.setText(String.valueOf(vip));

        int selectRow = -1;
        for (int i = 0; i < eventModel.getRowCount(); i++)
            if (eventModel.getValueAt(i, 0).equals(selected)) { selectRow = i; break; }
        if (selectRow == -1 && eventModel.getRowCount() > 0) selectRow = 0;
        if (selectRow >= 0) eventTable.setRowSelectionInterval(selectRow, selectRow);
        else updateDetails();
    }
}
