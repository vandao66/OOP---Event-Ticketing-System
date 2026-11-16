package view.gui;

import model.Customer;
import model.User;
import services.*;
import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private final Auth auth;
    private final EventService eventService;
    private final Booking booking;
    private final Checkin checkin;

    public MainGUI(Auth auth, EventService eventService, Booking booking, Checkin checkin) {
        this.auth = auth; this.eventService = eventService; this.booking = booking; this.checkin = checkin;
        setTitle("EVENT HUB | Hệ thống đặt vé sự kiện");
        setSize(1180, 780); setMinimumSize(new Dimension(960, 680));
        setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showLogin();
    }

    public void showLogin() {
        setContentPane(new LoginPanel(auth, this::showHome));
        revalidate(); repaint();
    }

    private void showHome(User user) {
        switch (user.role()) {
            case "Admin": setContentPane(new AdminPanel(eventService, booking, user, this::showLogin)); break;
            case "Customer": setContentPane(new CustomerPanel(eventService, booking, (Customer) user, this::showLogin)); break;
            case "Staff": setContentPane(new StaffPanel(checkin, user, this::showLogin)); break;
            default: JOptionPane.showMessageDialog(this, "Vai trò không hợp lệ!"); return;
        }
        revalidate(); repaint();
    }
}
