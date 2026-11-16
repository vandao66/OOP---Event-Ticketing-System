import database.DatabaseManager;
import services.*;
import view.gui.MainGUI;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                DatabaseManager db = new DatabaseManager();
                Auth auth = new Auth(db);
                EventService eventService = new EventService(db);
                Booking booking = new Booking(db, eventService);
                Checkin checkin = new Checkin(db);

                MainGUI gui = new MainGUI(auth, eventService, booking, checkin);
                gui.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Lỗi khởi động", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}