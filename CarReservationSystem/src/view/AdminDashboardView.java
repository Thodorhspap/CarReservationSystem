package view;

import controller.AuthController;
import controller.BookingController;
import controller.VehicleController;
import model.entities.Booking;
import model.entities.Vehicle;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardView extends JFrame {

    private final AuthController    authController;
    private final VehicleController vehicleController;
    private final BookingController bookingController;

    // Tab 1 — Στόλος
    private DefaultTableModel fleetTableModel;
    private JTable            fleetTable;
    private JLabel            fleetMsgLabel;
    private List<Vehicle>     currentVehicles;

    // Φόρμα προσθήκης οχήματος
    private JTextField plateField;
    private JTextField brandField;
    private JTextField modelField;
    private JComboBox<String> categoryCombo;
    private JTextField dailyRateField;

    // Tab 2 — Κρατήσεις
    private DefaultTableModel allBookingsTableModel;
    private JTable            allBookingsTable;
    private JLabel            allBookingsMsgLabel;

    public AdminDashboardView(AuthController authController) {
        this.authController    = authController;
        this.vehicleController = new VehicleController();
        this.bookingController = new BookingController();
        buildUI();
    }

    private void buildUI() {
        setTitle("Car Reservation — Admin: " + authController.getLoggedInUser().getUsername());
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BACKGROUND);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.fontBodyBold());
        tabs.setBackground(UITheme.SURFACE);
        tabs.addTab("Διαχείριση Στόλου",  buildFleetTab());
        tabs.addTab("Εποπτεία Κρατήσεων", buildAllBookingsTab());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) loadFleet();
            if (tabs.getSelectedIndex() == 1) loadAllBookings();
        });

        add(tabs);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Λογαριασμός");
        JMenuItem logoutItem = new JMenuItem("Αποσύνδεση");
        logoutItem.addActionListener(e -> logout());
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        loadFleet();
    }

    // ── Tab 1: Διαχείριση Στόλου ───────────────────────────────────────────────

    private JPanel buildFleetTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(UITheme.BACKGROUND);

        // Πίνακας οχημάτων
        String[] cols = {"#", "Πινακίδα", "Μάρκα", "Μοντέλο", "Κατηγορία", "Κατάσταση", "Χλμ.", "Τιμή/μέρα (€)"};
        fleetTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        fleetTable = new JTable(fleetTableModel);
        fleetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fleetTable.getColumnModel().getColumn(0).setMinWidth(0);
        fleetTable.getColumnModel().getColumn(0).setMaxWidth(0);
        UITheme.styleTable(fleetTable);
        UITheme.applyZebraRows(fleetTable);
        UITheme.applyStatusBadgeRenderer(fleetTable, 5);

        fleetMsgLabel = new JLabel(" ");
        fleetMsgLabel.setFont(UITheme.fontSmall());
        fleetMsgLabel.setForeground(UITheme.ERROR);

        JScrollPane fleetScroll = new JScrollPane(fleetTable);
        fleetScroll.setBorder(BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT));
        panel.add(fleetScroll, BorderLayout.CENTER);
        panel.add(buildAddVehiclePanel(), BorderLayout.EAST);
        panel.add(buildFleetSouthPanel(),  BorderLayout.SOUTH);

        return panel;
    }

    // Φόρμα προσθήκης νέου οχήματος (δεξιά πλευρά)
    private JPanel buildAddVehiclePanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UITheme.SURFACE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT),
                BorderFactory.createTitledBorder(null, "Προσθήκη Νέου Οχήματος", 0, 0, UITheme.fontBodyBold(), UITheme.PRIMARY)));
        form.setPreferredSize(new Dimension(240, 0));

        plateField    = new JTextField();
        brandField    = new JTextField();
        modelField    = new JTextField();
        categoryCombo = new JComboBox<>(new String[]{"ECONOMY", "SUV", "LUXURY"});
        dailyRateField = new JTextField();

        form.add(labeledField("Πινακίδα:",    plateField));
        form.add(labeledField("Μάρκα:",       brandField));
        form.add(labeledField("Μοντέλο:",     modelField));
        form.add(labeledCombo("Κατηγορία:",   categoryCombo));
        form.add(labeledField("Τιμή/μέρα €:", dailyRateField));
        form.add(Box.createVerticalStrut(10));

        JButton addBtn = new JButton("Προσθήκη Οχήματος");
        UITheme.stylePrimaryButton(addBtn);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> handleAddVehicle());
        form.add(addBtn);

        return form;
    }

    private JPanel buildFleetSouthPanel() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.BACKGROUND);
        south.add(fleetMsgLabel);

        JButton refreshBtn    = new JButton("Ανανέωση");
        JButton statusBtn     = new JButton("Αλλαγή Κατάστασης");
        JButton deleteBtn     = new JButton("Διαγραφή Επιλεγμένου");
        UITheme.styleSecondaryButton(refreshBtn);
        UITheme.styleSecondaryButton(statusBtn);
        UITheme.styleDangerButton(deleteBtn);

        refreshBtn.addActionListener(e -> loadFleet());
        statusBtn.addActionListener(e  -> handleChangeStatus());
        deleteBtn.addActionListener(e  -> handleDeleteVehicle());

        south.add(refreshBtn);
        south.add(statusBtn);
        south.add(deleteBtn);
        return south;
    }

    private void handleChangeStatus() {
        int row = fleetTable.getSelectedRow();
        if (row < 0 || currentVehicles == null) {
            JOptionPane.showMessageDialog(this, "Επιλέξτε πρώτα ένα όχημα.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicle vehicle = currentVehicles.get(row);

        if ("RESERVED".equals(vehicle.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "Δεν μπορείτε να αλλάξετε κατάσταση οχήματος που έχει ενεργή κράτηση.",
                    "Απαγορεύεται", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] options = {"AVAILABLE", "MAINTENANCE"};
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setSelectedItem(vehicle.getStatus());

        int result = JOptionPane.showConfirmDialog(this, combo,
                "Νέα κατάσταση για " + vehicle.getPlate(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String newStatus = (String) combo.getSelectedItem();
        if (newStatus.equals(vehicle.getStatus())) return;

        try {
            vehicleController.updateStatus(vehicle.getVehicleId(), newStatus);
            fleetMsgLabel.setText("Κατάσταση " + vehicle.getPlate() + " → " + newStatus);
            fleetMsgLabel.setForeground(new Color(0, 130, 0));
            loadFleet();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα: " + ex.getMessage(), "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadFleet() {
        fleetTableModel.setRowCount(0);
        currentVehicles = null;
        fleetMsgLabel.setText(" ");
        try {
            currentVehicles = vehicleController.getAllVehicles();
            if (currentVehicles.isEmpty()) {
                fleetMsgLabel.setText("Δεν υπάρχουν οχήματα.");
                return;
            }
            for (int i = 0; i < currentVehicles.size(); i++) {
                Vehicle v = currentVehicles.get(i);
                fleetTableModel.addRow(new Object[]{
                        i,
                        v.getPlate(), v.getBrand(), v.getModel(),
                        v.getCategory(), v.getStatus(),
                        v.getMileage(), v.getDailyRate()
                });
            }
        } catch (Exception ex) {
            fleetMsgLabel.setText("Σφάλμα: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleAddVehicle() {
        String plate    = plateField.getText().trim();
        String brand    = brandField.getText().trim();
        String model    = modelField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String rateText = dailyRateField.getText().trim();

        if (plate.isEmpty() || brand.isEmpty() || model.isEmpty() || rateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Συμπληρώστε όλα τα πεδία.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double dailyRate;
        try {
            dailyRate = Double.parseDouble(rateText);
            if (dailyRate <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Η τιμή/μέρα πρέπει να είναι θετικός αριθμός.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Προσθήκη οχήματος:\n%s %s (%s) — %s — %.2f €/μέρα",
                        brand, model, plate, category, dailyRate),
                "Επιβεβαίωση Προσθήκης", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Vehicle v = new Vehicle(0, plate, brand, model, category, "AVAILABLE", 0, dailyRate);
            vehicleController.addVehicle(v);
            JOptionPane.showMessageDialog(this, "Το όχημα προστέθηκε επιτυχώς.", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            plateField.setText("");
            brandField.setText("");
            modelField.setText("");
            dailyRateField.setText("");
            loadFleet();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα: " + ex.getMessage(), "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleDeleteVehicle() {
        int row = fleetTable.getSelectedRow();
        if (row < 0 || currentVehicles == null) {
            JOptionPane.showMessageDialog(this, "Επιλέξτε πρώτα ένα όχημα.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicle vehicle = currentVehicles.get(row);

        if ("RESERVED".equals(vehicle.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "Δεν μπορείτε να διαγράψετε όχημα με κατάσταση RESERVED.",
                    "Απαγορεύεται", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Οριστική διαγραφή οχήματος:\n%s %s (%s)\n\nΗ ενέργεια δεν αναιρείται.",
                        vehicle.getBrand(), vehicle.getModel(), vehicle.getPlate()),
                "Επιβεβαίωση Διαγραφής", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            vehicleController.deleteVehicle(vehicle.getVehicleId());
            JOptionPane.showMessageDialog(this, "Το όχημα διαγράφηκε.", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            loadFleet();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα διαγραφής (πιθανόν υπάρχουν κρατήσεις για αυτό το όχημα):\n" + ex.getMessage(),
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── Tab 2: Εποπτεία Κρατήσεων ─────────────────────────────────────────────

    private JPanel buildAllBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(UITheme.BACKGROUND);

        String[] cols = {"ID", "ID Πελάτη", "ID Οχήματος", "Από", "Έως", "Κατάσταση", "Κόστος (€)"};
        allBookingsTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        allBookingsTable = new JTable(allBookingsTableModel);
        allBookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(allBookingsTable);
        UITheme.applyZebraRows(allBookingsTable);
        UITheme.applyStatusBadgeRenderer(allBookingsTable, 5);

        allBookingsMsgLabel = new JLabel(" ");
        allBookingsMsgLabel.setFont(UITheme.fontSmall());
        allBookingsMsgLabel.setForeground(UITheme.ERROR);

        JButton refreshBtn = new JButton("Ανανέωση");
        UITheme.styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadAllBookings());

        JLabel legend = new JLabel("  Κατάσταση: ACTIVE / CANCELLED / COMPLETED");
        legend.setFont(UITheme.fontSmall());
        legend.setForeground(UITheme.ON_SURFACE_VARIANT);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.BACKGROUND);
        south.add(allBookingsMsgLabel);
        south.add(refreshBtn);

        JScrollPane allBookingsScroll = new JScrollPane(allBookingsTable);
        allBookingsScroll.setBorder(BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT));
        panel.add(legend,                        BorderLayout.NORTH);
        panel.add(allBookingsScroll,              BorderLayout.CENTER);
        panel.add(south,                         BorderLayout.SOUTH);

        return panel;
    }

    private void loadAllBookings() {
        allBookingsTableModel.setRowCount(0);
        allBookingsMsgLabel.setText(" ");
        try {
            List<Booking> bookings = bookingController.getAllBookings();
            if (bookings.isEmpty()) {
                allBookingsMsgLabel.setText("Δεν υπάρχουν κρατήσεις.");
                return;
            }
            for (Booking b : bookings) {
                allBookingsTableModel.addRow(new Object[]{
                        b.getBookingId(), b.getCustomerId(), b.getVehicleId(),
                        b.getStartDate(), b.getEndDate(),
                        b.getStatus(), String.format("%.2f", b.getTotalCost())
                });
            }
        } catch (Exception ex) {
            allBookingsMsgLabel.setText("Σφάλμα: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ── Βοηθητικές μέθοδοι UI ─────────────────────────────────────────────────

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel labeledCombo(String label, JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(4, 2));
        p.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    // ── Logout ─────────────────────────────────────────────────────────────────

    private void logout() {
        authController.logout();
        dispose();
        new LoginView(new AuthController()).setVisible(true);
    }
}
