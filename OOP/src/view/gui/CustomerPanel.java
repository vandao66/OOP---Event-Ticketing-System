package view.gui;

import model.Customer;
import model.Event;
import model.Ticket;
import model.TicketType;
import services.Booking;
import services.EventService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private final EventService eventService;
    private final Booking booking;
    private final Customer customer;
    private final DefaultTableModel eventModel, ticketModel;
    private final JTable eventTable, ticketTable;
    private final JTabbedPane tabs = new JTabbedPane();
    private final JComboBox<String> typeChoice = new JComboBox<>(new String[]{"Vé thường", "Vé VIP"});
    private final JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JLabel eventTitle = UIStyle.label("Chọn một sự kiện", 19, true, UIStyle.NAVY);
    private final JLabel eventCount = UIStyle.label("", 12, false, UIStyle.MUTED);
    private final JLabel stdPrice = UIStyle.label("—", 14, true, UIStyle.GREEN);
    private final JLabel vipPrice = UIStyle.label("—", 14, true, UIStyle.BLUE);
    private final JTextArea stdPerks = UIStyle.textArea("");
    private final JTextArea vipPerks = UIStyle.textArea("");
    private final JLabel totalPrice = UIStyle.label("0 đ", 21, true, UIStyle.BLUE);
    private final JLabel ticketTitle = UIStyle.label("Chọn vé để xem chi tiết", 15, true, UIStyle.NAVY);
    private final JTextArea ticketInfo = UIStyle.textArea("");
    private final JButton bookButton = UIStyle.button("ĐẶT VÉ NGAY");

    public CustomerPanel(EventService eventService, Booking booking, Customer customer, Runnable onLogout) {
        this.eventService = eventService; this.booking = booking; this.customer = customer;
        setLayout(new BorderLayout()); setBackground(UIStyle.BG);
        add(UIStyle.header("CUSTOMER PORTAL", "Khám phá & đặt vé", "Chọn sự kiện, so sánh quyền lợi và quản lý vé của bạn", customer.getUsername(), onLogout), BorderLayout.NORTH);

        eventModel = new DefaultTableModel(new String[]{"Mã SK", "Sự kiện", "Giá thường", "Giá VIP", "Còn thường", "Còn VIP"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        ticketModel = new DefaultTableModel(new String[]{"Mã vé", "Sự kiện", "Loại vé", "Ghế", "Giá", "Trạng thái", "Quyền lợi"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        eventTable = UIStyle.table(eventModel); ticketTable = UIStyle.table(ticketModel);
        eventTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] eventWidths = {85, 215, 120, 125, 100, 95};
        for (int i = 0; i < eventWidths.length; i++) eventTable.getColumnModel().getColumn(i).setPreferredWidth(eventWidths[i]);
        ticketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] ticketWidths = {180, 225, 90, 100, 125, 115, 400};
        for (int i = 0; i < ticketWidths.length; i++) ticketTable.getColumnModel().getColumn(i).setPreferredWidth(ticketWidths[i]);
        eventTable.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateEventDetails(); });
        ticketTable.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateTicketDetails(); });

        tabs.setFont(UIStyle.font(14, true)); tabs.setBackground(UIStyle.BG);
        tabs.addTab("Sự kiện & đặt vé", createEventsPage());
        tabs.addTab("Vé của tôi", createTicketsPage());
        tabs.addChangeListener(e -> refreshData());
        JPanel outer = new JPanel(new BorderLayout()); outer.setBackground(UIStyle.BG);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 22, 20, 22));
        outer.add(tabs, BorderLayout.CENTER); add(outer, BorderLayout.CENTER);
        typeChoice.addActionListener(e -> updatePricePreview());
        quantity.addChangeListener(e -> updatePricePreview());
        refreshData();
    }

    private JPanel createEventsPage() {
        JPanel page = new JPanel(new BorderLayout(15, 0)); page.setBackground(UIStyle.BG);
        page.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JPanel tableCard = UIStyle.card(new BorderLayout(0, 14));
        tableCard.add(UIStyle.label("Sự kiện đang mở bán", 18, true, UIStyle.NAVY), BorderLayout.NORTH);
        tableCard.add(UIStyle.scroll(eventTable), BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout()); footer.setOpaque(false);
        footer.add(UIStyle.label("Chọn sự kiện để xem giá, ghế trống và quyền lợi chi tiết.", 12, false, UIStyle.MUTED), BorderLayout.WEST);
        JButton refresh = UIStyle.quiet("Làm mới"); refresh.addActionListener(e -> refreshData());
        footer.add(refresh, BorderLayout.EAST); tableCard.add(footer, BorderLayout.SOUTH);
        page.add(tableCard, BorderLayout.CENTER);

        JPanel sidebar = UIStyle.card(new BorderLayout(0, 12));
        sidebar.setPreferredSize(new Dimension(355, 300));
        JPanel info = new JPanel(); info.setOpaque(false); info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[]{eventTitle, eventCount, stdPrice, vipPrice, stdPerks, vipPerks})
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(UIStyle.label("THÔNG TIN VÉ", 11, true, UIStyle.BLUE));
        info.add(Box.createVerticalStrut(8)); info.add(eventTitle);
        info.add(Box.createVerticalStrut(5)); info.add(eventCount);
        info.add(Box.createVerticalStrut(20));
        info.add(UIStyle.label("Giá vé thường", 12, true, UIStyle.MUTED)); info.add(Box.createVerticalStrut(4)); info.add(stdPrice);
        info.add(Box.createVerticalStrut(12));
        info.add(UIStyle.label("Giá vé VIP", 12, true, UIStyle.MUTED)); info.add(Box.createVerticalStrut(4)); info.add(vipPrice);
        info.add(Box.createVerticalStrut(19));
        info.add(UIStyle.label("QUYỀN LỢI VÉ THƯỜNG", 11, true, UIStyle.GREEN));
        info.add(Box.createVerticalStrut(6)); info.add(areaHolder(stdPerks));
        info.add(Box.createVerticalStrut(13));
        info.add(UIStyle.label("QUYỀN LỢI VÉ VIP", 11, true, UIStyle.BLUE));
        info.add(Box.createVerticalStrut(6)); info.add(areaHolder(vipPerks));
        JScrollPane infoScroll = UIStyle.scroll(info);
        infoScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebar.add(infoScroll, BorderLayout.CENTER);

        JPanel bookingBox = new JPanel(new BorderLayout(0, 10)); bookingBox.setOpaque(false);
        JPanel choices = new JPanel(new GridLayout(1, 2, 10, 0)); choices.setOpaque(false);
        JPanel typeBox = new JPanel(new BorderLayout(0, 5)); typeBox.setOpaque(false);
        typeBox.add(UIStyle.label("HẠNG VÉ", 11, true, UIStyle.MUTED), BorderLayout.NORTH);
        typeChoice.setFont(UIStyle.font(13, false)); typeBox.add(typeChoice, BorderLayout.CENTER);
        JPanel quantityBox = new JPanel(new BorderLayout(0, 5)); quantityBox.setOpaque(false);
        quantityBox.add(UIStyle.label("SỐ LƯỢNG", 11, true, UIStyle.MUTED), BorderLayout.NORTH);
        quantity.setFont(UIStyle.font(13, false)); quantityBox.add(quantity, BorderLayout.CENTER);
        choices.add(typeBox); choices.add(quantityBox); bookingBox.add(choices, BorderLayout.NORTH);
        JPanel confirmBox = new JPanel(new BorderLayout(0, 9)); confirmBox.setOpaque(false);
        JPanel totalRow = new JPanel(new BorderLayout()); totalRow.setOpaque(false);
        totalRow.add(UIStyle.label("TẠM TÍNH", 11, true, UIStyle.MUTED), BorderLayout.WEST);
        totalRow.add(totalPrice, BorderLayout.EAST);
        confirmBox.add(totalRow, BorderLayout.NORTH); confirmBox.add(bookButton, BorderLayout.SOUTH);
        bookingBox.add(confirmBox, BorderLayout.CENTER);
        bookButton.addActionListener(e -> bookSelectedEvent());
        sidebar.add(bookingBox, BorderLayout.SOUTH);
        page.add(sidebar, BorderLayout.EAST); return page;
    }

    private JPanel areaHolder(JTextArea area) {
        JPanel holder = new JPanel(new BorderLayout()); holder.setOpaque(false);
        area.setRows(3); area.setColumns(24);
        holder.add(area, BorderLayout.CENTER);
        holder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        holder.setAlignmentX(Component.LEFT_ALIGNMENT); return holder;
    }

    private JPanel createTicketsPage() {
        JPanel page = new JPanel(new BorderLayout(0, 14)); page.setBackground(UIStyle.BG);
        page.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JPanel tableCard = UIStyle.card(new BorderLayout(0, 12));
        JPanel toolbar = new JPanel(new BorderLayout()); toolbar.setOpaque(false);
        toolbar.add(UIStyle.label("Vé đã đặt", 18, true, UIStyle.NAVY), BorderLayout.WEST);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); controls.setOpaque(false);
        JButton refresh = UIStyle.quiet("Làm mới");
        JButton cancel = UIStyle.danger("Hủy vé đã chọn");
        controls.add(refresh); controls.add(cancel); toolbar.add(controls, BorderLayout.EAST);
        tableCard.add(toolbar, BorderLayout.NORTH);
        tableCard.add(UIStyle.scroll(ticketTable), BorderLayout.CENTER);
        page.add(tableCard, BorderLayout.CENTER);
        JPanel detail = UIStyle.card(new BorderLayout(0, 8)); detail.setPreferredSize(new Dimension(100, 125));
        detail.add(ticketTitle, BorderLayout.NORTH);
        detail.add(ticketInfo, BorderLayout.CENTER);
        page.add(detail, BorderLayout.SOUTH);
        cancel.addActionListener(e -> cancelSelectedTicket());
        refresh.addActionListener(e -> refreshData());
        return page;
    }

    private String selectedEventId() {
        int row = eventTable.getSelectedRow();
        return row < 0 ? null : (String) eventModel.getValueAt(eventTable.convertRowIndexToModel(row), 0);
    }

    private String selectedTicketId() {
        int row = ticketTable.getSelectedRow();
        return row < 0 ? null : (String) ticketModel.getValueAt(ticketTable.convertRowIndexToModel(row), 0);
    }

    private void updateEventDetails() {
        String id = selectedEventId();
        if (id == null) {
            eventTitle.setText("Chưa chọn sự kiện"); eventCount.setText("Hãy chọn một sự kiện ở bảng bên trái.");
            stdPrice.setText("—"); vipPrice.setText("—"); stdPerks.setText(""); vipPerks.setText("");
            bookButton.setEnabled(false); updatePricePreview(); return;
        }
        try {
            Event event = eventService.getEventById(id);
            eventTitle.setText(event.getEventName());
            eventCount.setText("Còn " + event.getAvailableStandardCount() + " thường · " + event.getAvailableVipCount() + " VIP");
            stdPrice.setText(String.format("%,d đ", event.getEventPrice()));
            vipPrice.setText(String.format("%,d đ  (+%d%%)", event.getVipPrice(), event.getVipSurchargePercent()));
            stdPerks.setText(event.getStandardPerks()); vipPerks.setText(event.getVipPerks());
            bookButton.setEnabled(true); updatePricePreview();
        } catch (Exception e) {
            eventTitle.setText("Không thể tải sự kiện"); bookButton.setEnabled(false);
        }
    }

    private void updatePricePreview() {
        String id = selectedEventId();
        if (id == null) { totalPrice.setText("0 đ"); return; }
        try {
            Event event = eventService.getEventById(id);
            long unit = typeChoice.getSelectedIndex() == 1 ? event.getVipPrice() : event.getEventPrice();
            totalPrice.setText(String.format("%,d đ", unit * (Integer) quantity.getValue()));
        } catch (Exception e) { totalPrice.setText("—"); }
    }

    private void bookSelectedEvent() {
        String id = selectedEventId();
        if (id == null) { JOptionPane.showMessageDialog(this, "Hãy chọn sự kiện cần đặt!"); return; }
        TicketType type = typeChoice.getSelectedIndex() == 1 ? TicketType.VIP : TicketType.STANDARD;
        int count = (Integer) quantity.getValue();
        int choice = JOptionPane.showConfirmDialog(this,
                "Xác nhận đặt " + count + " vé " + type.getDisplayName() + " của sự kiện " + id
                        + "?\nTổng tiền: " + totalPrice.getText(), "Xác nhận đặt vé", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            List<Ticket> tickets = booking.bookTickets(customer, id, type, count);
            StringBuilder message = new StringBuilder("ĐẶT VÉ THÀNH CÔNG!\n\n");
            long total = 0;
            for (Ticket ticket : tickets) {
                message.append(ticket.getTicketID()).append("  •  ").append(ticket.getTicketSeat().getSeatNumber()).append("\n");
                total += ticket.calculateFinalPrice();
            }
            message.append("\nTổng thanh toán: ").append(String.format("%,d đ", total));
            JOptionPane.showMessageDialog(this, message.toString(), "Đặt vé thành công", JOptionPane.INFORMATION_MESSAGE);
            refreshData(); tabs.setSelectedIndex(1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi đặt vé", JOptionPane.ERROR_MESSAGE);
            refreshData();
        }
    }

    private void updateTicketDetails() {
        int row = ticketTable.getSelectedRow();
        if (row == -1) { ticketTitle.setText("Chọn vé để xem chi tiết"); ticketInfo.setText(""); return; }
        row = ticketTable.convertRowIndexToModel(row);
        ticketTitle.setText("Vé " + ticketModel.getValueAt(row, 0) + " · " + ticketModel.getValueAt(row, 5));
        ticketInfo.setText("Sự kiện: " + ticketModel.getValueAt(row, 1)
                + "  |  Ghế: " + ticketModel.getValueAt(row, 3)
                + "  |  Giá: " + ticketModel.getValueAt(row, 4)
                + "\nQuyền lợi: " + ticketModel.getValueAt(row, 6));
    }

    private void cancelSelectedTicket() {
        String ticketId = selectedTicketId();
        if (ticketId == null) { JOptionPane.showMessageDialog(this, "Hãy chọn vé muốn hủy!"); return; }
        int answer = JOptionPane.showConfirmDialog(this, "Hủy vé " + ticketId + "?", "Xác nhận hủy vé", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        try {
            booking.cancelTicket(customer,ticketId);
            JOptionPane.showMessageDialog(this, "Đã hủy vé thành công!"); refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi hủy vé", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        String oldEvent = selectedEventId(), oldTicket = selectedTicketId();
        eventModel.setRowCount(0);
        for (Event event : eventService.getAllEvent()) {
            eventModel.addRow(new Object[]{event.getEventID(), event.getEventName(), String.format("%,d đ", event.getEventPrice()),
                    String.format("%,d đ", event.getVipPrice()), event.getAvailableStandardCount(), event.getAvailableVipCount()});
        }
        ticketModel.setRowCount(0);
        for (Ticket ticket : booking.getTicketByUsername(customer.getUsername())) {
            ticketModel.addRow(new Object[]{ticket.getTicketID(), ticket.getEvent().getEventName(), ticket.getTicketType(),
                    ticket.getTicketSeat().getSeatNumber(), String.format("%,d đ", ticket.calculateFinalPrice()),
                    ticket.isUsed() ? "Đã check-in" : "Chưa sử dụng", ticket.getPerks()});
        }
        selectRowById(eventTable, eventModel, oldEvent);
        selectRowById(ticketTable, ticketModel, oldTicket);
        updateEventDetails(); updateTicketDetails();
    }

    private void selectRowById(JTable table, DefaultTableModel model, String id) {
        int row = -1;
        for (int i = 0; i < model.getRowCount(); i++)
            if (model.getValueAt(i, 0).equals(id)) { row = i; break; }
        if (row == -1 && model.getRowCount() > 0) row = 0;
        if (row >= 0) table.setRowSelectionInterval(row, row);
    }
}
