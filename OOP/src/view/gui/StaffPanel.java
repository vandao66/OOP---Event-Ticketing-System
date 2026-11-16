package view.gui;

import model.Ticket;
import model.User;
import services.Checkin;
import javax.swing.*;
import java.awt.*;

public class StaffPanel extends JPanel {
    public StaffPanel(Checkin checkin, User user, Runnable onLogout) {
        setLayout(new BorderLayout()); setBackground(UIStyle.BG);
        add(UIStyle.header("STAFF CHECK-IN", "Quầy soát vé", "Xác minh vé nhanh chóng và chính xác", user.getUsername(), onLogout), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout()); center.setBackground(UIStyle.BG);
        center.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        JPanel card = UIStyle.card(new BorderLayout(0, 22));
        card.setPreferredSize(new Dimension(680, 445));
        JPanel intro = new JPanel(new GridLayout(3, 1, 0, 7)); intro.setOpaque(false);
        intro.add(UIStyle.label("XÁC NHẬN VÉ", 12, true, UIStyle.BLUE));
        intro.add(UIStyle.label("Check-in khách tham dự", 24, true, UIStyle.NAVY));
        intro.add(UIStyle.label("Nhập mã vé để kiểm tra trạng thái và quyền lợi của khách.", 13, false, UIStyle.MUTED));
        card.add(intro, BorderLayout.NORTH);

        JPanel controls = new JPanel(new BorderLayout(10, 9)); controls.setOpaque(false);
        controls.add(UIStyle.label("Mã vé cần check-in", 13, true, UIStyle.TEXT), BorderLayout.NORTH);
        JTextField ticketId = UIStyle.input(new JTextField());
        ticketId.setToolTipText("Ví dụ: TK-xxxxxxxxxxxx");
        JButton verify = UIStyle.button("XÁC NHẬN CHECK-IN");
        JPanel row = new JPanel(new BorderLayout(12, 0)); row.setOpaque(false);
        row.add(ticketId, BorderLayout.CENTER); row.add(verify, BorderLayout.EAST);
        controls.add(row, BorderLayout.CENTER);

        JPanel result = UIStyle.card(new BorderLayout(0, 9));
        result.setBackground(UIStyle.SOFT);
        JLabel resultTitle = UIStyle.label("Chưa có vé được xác minh", 16, true, UIStyle.NAVY);
        JTextArea details = UIStyle.textArea("Nhập mã vé ở phía trên, sau đó nhấn XÁC NHẬN CHECK-IN.");
        details.setRows(7);
        result.add(resultTitle, BorderLayout.NORTH);
        result.add(details, BorderLayout.CENTER);
        JPanel body = new JPanel(new BorderLayout(0, 18)); body.setOpaque(false);
        body.add(controls, BorderLayout.NORTH); body.add(result, BorderLayout.CENTER);
        card.add(body, BorderLayout.CENTER);
        center.add(card); add(center, BorderLayout.CENTER);

        verify.addActionListener(e -> {
            try {
                Ticket ticket = checkin.verifyAndCheckin(ticketId.getText());
                resultTitle.setText("CHECK-IN THÀNH CÔNG"); resultTitle.setForeground(UIStyle.GREEN);
                details.setText("Mã vé: " + ticket.getTicketID()
                        + "\nKhách hàng: " + ticket.getCustomer().getUsername()
                        + "\nSự kiện: " + ticket.getEvent().getEventName()
                        + "\nLoại vé: " + ticket.getTicketType()
                        + "  |  Ghế: " + ticket.getTicketSeat().getSeatNumber()
                        + "\nQuyền lợi: " + ticket.getPerks());
                ticketId.setText(""); ticketId.requestFocusInWindow();
            } catch (Exception ex) {
                resultTitle.setText("KHÔNG THỂ CHECK-IN"); resultTitle.setForeground(UIStyle.RED);
                details.setText(ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi check-in", JOptionPane.ERROR_MESSAGE);
            }
        });
        ticketId.addActionListener(e -> verify.doClick());
    }
}
