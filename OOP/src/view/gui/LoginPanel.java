package view.gui;

import model.User;
import services.Auth;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {
    private final Auth auth;
    private final Color BLUE = new Color(53, 94, 221);
    private final Color BG = new Color(245, 247, 252);
    private final Color TEXT = new Color(25, 40, 65);

    public LoginPanel(Auth auth, Consumer<User> onLoginSuccess) {
        this.auth = auth;
        setLayout(new GridBagLayout());
        setBackground(BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(480, 560));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 240), 1, true),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel appTitle = new JLabel("Hệ thống đặt vé sự kiện", SwingConstants.CENTER);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 23));
        appTitle.setForeground(TEXT);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        card.add(appTitle, gbc);

        JLabel title = new JLabel("Đăng nhập", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(BLUE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Đăng nhập để tiếp tục sử dụng hệ thống", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(120, 135, 155));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(subtitle, gbc);

        // Tên đăng nhập
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(TEXT);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(lblUser, gbc);

        JTextField txtUsername = new JTextField();
        styleField(txtUsername);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(txtUsername, gbc);

        // Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(TEXT);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(lblPass, gbc);

        JPasswordField txtPassword = new JPasswordField();
        styleField(txtPassword);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(txtPassword, gbc);

        // Nút đăng nhập
        JButton btnLogin = new JButton("ĐĂNG NHẬP");
        styleButton(btnLogin, BLUE, Color.WHITE);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 12, 0);
        card.add(btnLogin, gbc);

        // Nút đăng ký
        JButton btnRegister = new JButton("Tạo tài khoản mới");
        styleButton(btnRegister, new Color(232, 238, 248), TEXT);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(btnRegister, gbc);

        // Tài khoản mẫu
        JLabel hint = new JLabel("Tài khoản mẫu: admin/123 · staff/123 · customer/123", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(125, 140, 165));
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(hint, gbc);

        add(card);

        // Xử lý đăng nhập
        btnLogin.addActionListener(e -> {
            try {
                User user = auth.login(txtUsername.getText(), new String(txtPassword.getPassword()));
                onLoginSuccess.accept(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        });

        txtPassword.addActionListener(e -> btnLogin.doClick());
        btnRegister.addActionListener(e -> showRegisterDialog());
        SwingUtilities.invokeLater(txtUsername::requestFocusInWindow);
    }

    // Trang trí ô nhập liệu
    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(350, 44));
        field.setBackground(new Color(248, 250, 253));
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 224, 238)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    }

    // Trang trí nút bấm
    private void styleButton(JButton button, Color bg, Color fg) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(350, 46));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
    }

    // Giao diện đăng ký
    private void showRegisterDialog() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.add(new JLabel("Tên đăng nhập:")); form.add(username);
        form.add(new JLabel("Mật khẩu:")); form.add(password);
        form.add(new JLabel("Nhập lại mật khẩu:")); form.add(confirm);

        int choice = JOptionPane.showConfirmDialog(this, form, "Đăng ký tài khoản",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        String pass = new String(password.getPassword());
        String confirmPass = new String(confirm.getPassword());

        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!");
            return;
        }

        try {
            auth.register(username.getText(), pass);
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Hãy đăng nhập.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
        }
    }
}