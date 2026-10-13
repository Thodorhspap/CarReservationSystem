package view;

import controller.AuthController;
import controller.BookingController;
import controller.VehicleController;
import model.entities.Booking;
import model.entities.Vehicle;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SecretaryDashboardView extends JFrame {

    private final AuthController    authController;
    private final BookingController bookingController;
    private final VehicleController vehicleController;

    // Tab 1 — Κρατήσεις
    private DefaultTableModel bookingTableModel;
    private JTable            bookingTable;
    private JLabel            bookingMsgLabel;
    private List<Booking>     currentBookings;

    // Tab 2 — Οχήματα
    private DefaultTableModel vehicleTableModel;
    private JTable            vehicleTable;
    private JComboBox<String> statusCombo;
    private JLabel            vehicleMsgLabel;
    private List<Vehicle>     currentVehicles;

    public SecretaryDashboardView(AuthController authController) {
        this.authController    = authController;
        this.bookingController = new BookingController();
        this.vehicleController = new VehicleController();
        buildUI();
    }

    private void buildUI() {
        setTitle("Car Reservation — Γραμματεία: " + authController.getLoggedInUser().getUsername());
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BACKGROUND);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.fontBodyBold());
        tabs.setBackground(UITheme.SURFACE);
        tabs.addTab("Ενεργές Κρατήσεις",    buildBookingsTab());
        tabs.addTab("Κατάσταση Οχημάτων",   buildVehiclesTab());

        // Φορτώνει τα δεδομένα κάθε tab όταν επιλεγεί
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) loadActiveBookings();
            if (tabs.getSelectedIndex() == 1) loadAllVehicles();
        });

        add(tabs);

        // Logout μενού
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Λογαριασμός");
        JMenuItem logoutItem = new JMenuItem("Αποσύνδεση");
        logoutItem.addActionListener(e -> logout());
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Φορτώνει αρχικά το πρώτο tab
        loadActiveBookings();
    }

    // ── Tab 1: Ενεργές Κρατήσεις ──────────────────────────────────────────────

    private JPanel buildBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(UITheme.BACKGROUND);

        String[] cols = {"#", "ID Κράτησης", "ID Πελάτη", "ID Οχήματος", "Από", "Έως", "Κατάσταση", "Κόστος (€)"};
        bookingTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Κρύβει τον εσωτερικό index
        bookingTable.getColumnModel().getColumn(0).setMinWidth(0);
        bookingTable.getColumnModel().getColumn(0).setMaxWidth(0);
        UITheme.styleTable(bookingTable);
        UITheme.applyZebraRows(bookingTable);
        UITheme.applyStatusBadgeRenderer(bookingTable, 6);
        JScrollPane bookingScroll = new JScrollPane(bookingTable);
        bookingScroll.setBorder(BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT));
        panel.add(bookingScroll, BorderLayout.CENTER);

        bookingMsgLabel = new JLabel(" ");
        bookingMsgLabel.setFont(UITheme.fontSmall());
        bookingMsgLabel.setForeground(UITheme.ERROR);

        JButton refreshBtn   = new JButton("Ανανέωση");
        JButton completeBtn  = new JButton("Ολοκλήρωση (COMPLETED)");
        JButton cancelBtn    = new JButton("Ακύρωση (CANCELLED)");
        UITheme.styleSecondaryButton(refreshBtn);
        UITheme.styleSuccessButton(completeBtn);
        UITheme.styleDangerButton(cancelBtn);

        refreshBtn.addActionListener(e  -> loadActiveBookings());
        completeBtn.addActionListener(e -> handleBookingStatusChange("COMPLETED"));
        cancelBtn.addActionListener(e   -> handleBookingStatusChange("CANCELLED"));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.BACKGROUND);
        south.add(bookingMsgLabel);
        south.add(refreshBtn);
        south.add(completeBtn);
        south.add(cancelBtn);
        panel.add(south, BorderLayout.SOUTH);

        JLabel info = new JLabel("  Εδώ εμφανίζονται όλες οι ενεργές κρατήσεις. Επιλέξτε μία για να αλλάξετε κατάσταση.");
        info.setFont(UITheme.fontSmall());
        info.setForeground(UITheme.ON_SURFACE_VARIANT);
        panel.add(info, BorderLayout.NORTH);

        return panel;
    }

    private void loadActiveBookings() {
        bookingTableModel.setRowCount(0);
        currentBookings = null;
        bookingMsgLabel.setText(" ");
        try {
            currentBookings = bookingController.getAllActiveBookings();
            if (currentBookings.isEmpty()) {
                bookingMsgLabel.setText("Δεν υπάρχουν ενεργές κρατήσεις.");
                return;
            }
            for (int i = 0; i < currentBookings.size(); i++) {
                Booking b = currentBookings.get(i);
                bookingTableModel.addRow(new Object[]{
                        i,
                        b.getBookingId(), b.getCustomerId(), b.getVehicleId(),
                        b.getStartDate(), b.getEndDate(),
                        b.getStatus(), String.format("%.2f", b.getTotalCost())
                });
            }
        } catch (Exception ex) {
            bookingMsgLabel.setText("Σφάλμα: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleBookingStatusChange(String newStatus) {
        int row = bookingTable.getSelectedRow();
        if (row < 0 || currentBookings == null) {
            JOptionPane.showMessageDialog(this, "Επιλέξτε πρώτα μια κράτηση.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = currentBookings.get(row);

        String action = newStatus.equals("COMPLETED") ? "ολοκλήρωση" : "ακύρωση";
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Επιβεβαίωση %s κράτησης #%d;", action, booking.getBookingId()),
                "Επιβεβαίωση", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (newStatus.equals("COMPLETED")) {
                bookingController.completeBooking(booking.getBookingId(), booking.getVehicleId());
            } else {
                bookingController.cancelBooking(booking.getBookingId(), booking.getVehicleId());
            }
            JOptionPane.showMessageDialog(this,
                    "Η κράτηση #" + booking.getBookingId() + " ενημερώθηκε σε " + newStatus + ".",
                    "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            loadActiveBookings();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα: " + ex.getMessage(), "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── Tab 2: Κατάσταση Οχημάτων (UC4) ──────────────────────────────────────

    private JPanel buildVehiclesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(UITheme.BACKGROUND);

        String[] cols = {"#", "Πινακίδα", "Μάρκα", "Μοντέλο", "Κατηγορία", "Κατάσταση", "Χλμ.", "Τιμή/μέρα (€)"};
        vehicleTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        vehicleTable = new JTable(vehicleTableModel);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.getColumnModel().getColumn(0).setMinWidth(0);
        vehicleTable.getColumnModel().getColumn(0).setMaxWidth(0);
        UITheme.styleTable(vehicleTable);
        UITheme.applyZebraRows(vehicleTable);
        UITheme.applyStatusBadgeRenderer(vehicleTable, 5);
        JScrollPane vehicleScroll = new JScrollPane(vehicleTable);
        vehicleScroll.setBorder(BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT));
        panel.add(vehicleScroll, BorderLayout.CENTER);

        vehicleMsgLabel = new JLabel(" ");
        vehicleMsgLabel.setFont(UITheme.fontSmall());
        vehicleMsgLabel.setForeground(UITheme.ERROR);

        // Dropdown νέου status
        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "RESERVED", "MAINTENANCE", "DAMAGED"});

        JButton refreshBtn = new JButton("Ανανέωση");
        JButton updateBtn  = new JButton("Ενημέρωση Κατάστασης");
        UITheme.styleSecondaryButton(refreshBtn);
        UITheme.stylePrimaryButton(updateBtn);

        refreshBtn.addActionListener(e -> loadAllVehicles());
        updateBtn.addActionListener(e  -> handleUpdateVehicleStatus());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.BACKGROUND);
        south.add(vehicleMsgLabel);
        JLabel newStatusLabel = new JLabel("Νέα κατάσταση:");
        newStatusLabel.setFont(UITheme.fontBodyBold());
        south.add(newStatusLabel);
        south.add(statusCombo);
        south.add(refreshBtn);
        south.add(updateBtn);
        panel.add(south, BorderLayout.SOUTH);

        JLabel info = new JLabel("  Επιλέξτε όχημα → ορίστε νέα κατάσταση → πατήστε \"Ενημέρωση\".");
        info.setFont(UITheme.fontSmall());
        info.setForeground(UITheme.ON_SURFACE_VARIANT);
        panel.add(info, BorderLayout.NORTH);

        return panel;
    }

    private void loadAllVehicles() {
        vehicleTableModel.setRowCount(0);
        currentVehicles = null;
        vehicleMsgLabel.setText(" ");
        try {
            currentVehicles = vehicleController.getAllVehicles();
            if (currentVehicles.isEmpty()) {
                vehicleMsgLabel.setText("Δεν υπάρχουν οχήματα στο σύστημα.");
                return;
            }
            for (int i = 0; i < currentVehicles.size(); i++) {
                Vehicle v = currentVehicles.get(i);
                vehicleTableModel.addRow(new Object[]{
                        i,
                        v.getPlate(), v.getBrand(), v.getModel(),
                        v.getCategory(), v.getStatus(),
                        v.getMileage(), v.getDailyRate()
                });
            }
        } catch (Exception ex) {
            vehicleMsgLabel.setText("Σφάλμα: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleUpdateVehicleStatus() {
        int row = vehicleTable.getSelectedRow();
        if (row < 0 || currentVehicles == null) {
            JOptionPane.showMessageDialog(this, "Επιλέξτε πρώτα ένα όχημα.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicle vehicle   = currentVehicles.get(row);
        String newStatus  = (String) statusCombo.getSelectedItem();

        if (newStatus.equals(vehicle.getStatus())) {
            JOptionPane.showMessageDialog(this, "Το όχημα έχει ήδη κατάσταση: " + newStatus, "Πληροφορία", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Αλλαγή κατάστασης οχήματος %s %s (%s)\nΑπό: %s  →  Σε: %s",
                        vehicle.getBrand(), vehicle.getModel(), vehicle.getPlate(),
                        vehicle.getStatus(), newStatus),
                "Επιβεβαίωση Αλλαγής", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            vehicleController.updateStatus(vehicle.getVehicleId(), newStatus);
            JOptionPane.showMessageDialog(this,
                    "Η κατάσταση του οχήματος ενημερώθηκε σε " + newStatus + ".",
                    "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            loadAllVehicles();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα: " + ex.getMessage(), "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── Logout ─────────────────────────────────────────────────────────────────

    private void logout() {
        authController.logout();
        dispose();
        new LoginView(new AuthController()).setVisible(true);
    }
}
