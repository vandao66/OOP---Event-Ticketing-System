package view.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

// Các màu và thành phần giao diện dùng chung, không cần cài thêm thư viện.
public final class UIStyle {
    public static final Color BG = new Color(244, 247, 252);
    public static final Color WHITE = Color.WHITE;
    public static final Color NAVY = new Color(22, 35, 57);
    public static final Color BLUE = new Color(53, 94, 221);
    public static final Color BLUE_HOVER = new Color(40, 78, 192);
    public static final Color GREEN = new Color(21, 150, 107);
    public static final Color RED = new Color(213, 67, 75);
    public static final Color TEXT = new Color(33, 48, 71);
    public static final Color MUTED = new Color(106, 122, 143);
    public static final Color BORDER = new Color(222, 229, 240);
    public static final Color SOFT = new Color(248, 250, 254);

    private UIStyle() {}
    public static Font font(int size, boolean bold) { return new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size); }

    public static JLabel label(String value, int size, boolean bold, Color color) {
        JLabel label = new JLabel(value);
        label.setFont(font(size, bold)); label.setForeground(color);
        return label;
    }

    public static JTextField input(JTextField field) {
        field.setFont(font(14, false)); field.setForeground(TEXT); field.setBackground(SOFT);
        field.setCaretColor(BLUE); field.setPreferredSize(new Dimension(220, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(9, 12, 9, 12)));
        return field;
    }

    public static JButton button(String title) { return new RoundButton(title, BLUE, WHITE); }
    public static JButton danger(String title) { return new RoundButton(title, RED, WHITE); }
    public static JButton quiet(String title) { return new RoundButton(title, new Color(232, 238, 247), TEXT); }

    public static JPanel card(LayoutManager layout) {
        JPanel panel = new RoundCard(); panel.setLayout(layout);
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));
        return panel;
    }

    public static JScrollPane scroll(Component component) {
        JScrollPane pane = new JScrollPane(component);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.getViewport().setBackground(WHITE);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        pane.getHorizontalScrollBar().setUnitIncrement(16);
        return pane;
    }

    public static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(font(13, false)); table.setForeground(TEXT); table.setRowHeight(40);
        table.setShowHorizontalLines(true); table.setShowVerticalLines(false);
        table.setGridColor(new Color(236, 241, 248));
        table.setSelectionBackground(new Color(220, 233, 255));
        table.setSelectionForeground(NAVY); table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setBorder(new EmptyBorder(0, 11, 0, 8));
                if (!selected) {
                    setBackground(row % 2 == 0 ? WHITE : SOFT);
                    setForeground(TEXT);
                }
                return this;
            }
        });
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 42));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                setFont(font(12, true)); setForeground(NAVY); setBackground(new Color(234, 240, 249));
                setBorder(new EmptyBorder(0, 11, 0, 8)); return this;
            }
        });
        return table;
    }

    public static JTextArea textArea(String value) {
        JTextArea area = new JTextArea(value);
        area.setFont(font(13, false)); area.setForeground(TEXT); area.setOpaque(false);
        area.setLineWrap(true); area.setWrapStyleWord(true); area.setEditable(false);
        area.setFocusable(false); area.setBorder(BorderFactory.createEmptyBorder());
        return area;
    }

    public static JPanel metric(String label, String value, Color accent) {
        JPanel card = card(new BorderLayout(0, 5));
        JLabel caption = label(label, 12, false, MUTED);
        JLabel number = label(value, 23, true, accent);
        card.add(caption, BorderLayout.NORTH); card.add(number, BorderLayout.CENTER);
        return card;
    }

    public static JPanel header(String section, String title, String subtitle, String username, Runnable onLogout) {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(NAVY); panel.setBorder(new EmptyBorder(19, 27, 19, 27));
        JPanel left = new JPanel(new GridLayout(3, 1, 0, 2)); left.setOpaque(false);
        left.add(label(section.toUpperCase(), 11, true, new Color(138, 178, 255)));
        left.add(label(title, 23, true, WHITE));
        left.add(label(subtitle, 12, false, new Color(200, 212, 231)));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 19)); right.setOpaque(false);
        JLabel account = label("Tài khoản: " + username, 13, true, WHITE);
        JButton logout = new RoundButton("Đăng xuất", new Color(57, 76, 108), WHITE);
        logout.addActionListener(e -> onLogout.run());
        right.add(account); right.add(logout);
        panel.add(left, BorderLayout.CENTER); panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private static class RoundCard extends JPanel {
        RoundCard() { setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(WHITE); g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
            g.setColor(BORDER); g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
            g.dispose(); super.paintComponent(graphics);
        }
    }

    private static class RoundButton extends JButton {
        private final Color base;
        private boolean hover;
        RoundButton(String title, Color base, Color foreground) {
            super(title); this.base = base;
            setForeground(foreground); setFont(font(13, true)); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setBorder(new EmptyBorder(11, 17, 11, 17));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = base;
            if (hover) bg = new Color(Math.max(0, base.getRed() - 16), Math.max(0, base.getGreen() - 16), Math.max(0, base.getBlue() - 16));
            if (!isEnabled()) bg = new Color(177, 186, 200);
            g.setColor(bg); g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g.dispose(); super.paintComponent(graphics);
        }
    }
}
