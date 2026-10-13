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
import java.time.LocalDate;
import java.util.List;

public class CustomerDashboardView extends JFrame {

    private final AuthController authController;
    private final VehicleController vehicleController;
    private final BookingController bookingController;

    // Tab 1 — Αναζήτηση
    private JComboBox<Integer> fromDayCombo, fromMonthCombo, fromYearCombo;
    private JComboBox<Integer> toDayCombo,   toMonthCombo,   toYearCombo;
    private JComboBox<String> categoryCombo;
    private DefaultTableModel vehicleTableModel;
    private JTable vehicleTable;
    private JLabel searchMessageLabel;

    // Tab 2 — Κρατήσεις μου
    private DefaultTableModel bookingTableModel;
    private JTable bookingTable;
    private JLabel bookingMessageLabel;

    private List<Vehicle> currentSearchResults;
    private List<Booking> currentBookings;

    public CustomerDashboardView(AuthController authController) {
        this.authController    = authController;
        this.vehicleController = new VehicleController();
        this.bookingController = new BookingController();
        buildUI();
    }

    private void buildUI() {
        setTitle("Car Reservation — Πελάτης: " + authController.getLoggedInUser().getUsername());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BACKGROUND);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.fontBodyBold());
        tabs.setBackground(UITheme.SURFACE);
        tabs.addTab("Αναζήτηση & Κράτηση", buildSearchTab());
        tabs.addTab("Οι Κρατήσεις μου",    buildMyBookingsTab());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) loadMyBookings();
        });

        add(tabs);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Λογαριασμός");
        JMenuItem logoutItem = new JMenuItem("Αποσύνδεση");
        logoutItem.addActionListener(e -> logout());
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    // ── Tab 1: Αναζήτηση & Κράτηση ────────────────────────────────────────────

    private JPanel buildSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(UITheme.BACKGROUND);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        formPanel.setBackground(UITheme.SURFACE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT),
                new EmptyBorder(10, 12, 10, 12)));

        fromDayCombo   = new JComboBox<>();
        fromMonthCombo = new JComboBox<>();
        fromYearCombo  = new JComboBox<>();
        toDayCombo     = new JComboBox<>();
        toMonthCombo   = new JComboBox<>();
        toYearCombo    = new JComboBox<>();
        initDateCombos();

        formPanel.add(themedLabel("Από:"));
        formPanel.add(buildDatePanel(fromDayCombo, fromMonthCombo, fromYearCombo));

        formPanel.add(themedLabel("Έως:"));
        formPanel.add(buildDatePanel(toDayCombo, toMonthCombo, toYearCombo));

        formPanel.add(themedLabel("Κατηγορία:"));
        categoryCombo = new JComboBox<>(new String[]{"ALL", "ECONOMY", "SUV", "LUXURY"});
        formPanel.add(categoryCombo);

        JButton searchBtn = new JButton("Αναζήτηση");
        UITheme.stylePrimaryButton(searchBtn);
        searchBtn.addActionListener(e -> handleSearch());
        formPanel.add(searchBtn);

        searchMessageLabel = new JLabel(" ");
        searchMessageLabel.setFont(UITheme.fontSmall());
        searchMessageLabel.setForeground(UITheme.ERROR);
        formPanel.add(searchMessageLabel);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] cols = {"Πινακίδα", "Μάρκα", "Μοντέλο", "Κατηγορία", "Τιμή/μέρα (€)", "Εκτιμ. Κόστος (€)"};
        vehicleTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        vehicleTable = new JTable(vehicleTableModel);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(vehicleTable);
        UITheme.applyZebraRows(vehicleTable);
        UITheme.applyStatusBadgeRenderer(vehicleTable, 3);
        JScrollPane vehicleScroll = new JScrollPane(vehicleTable);
        vehicleScroll.setBorder(BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT));
        panel.add(vehicleScroll, BorderLayout.CENTER);

        JButton bookBtn = new JButton("Κράτηση επιλεγμένου οχήματος");
        UITheme.stylePrimaryButton(bookBtn);
        bookBtn.addActionListener(e -> handleBook());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(UITheme.BACKGROUND);
        southPanel.add(bookBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel themedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.fontBodyBold());
        label.setForeground(UITheme.ON_SURFACE_VARIANT);
        return label;
    }

    private void initDateCombos() {
        LocalDate today    = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        int currentYear    = today.getYear();

        for (int d = 1; d <= 31; d++) { fromDayCombo.addItem(d); toDayCombo.addItem(d); }
        for (int m = 1; m <= 12; m++) { fromMonthCombo.addItem(m); toMonthCombo.addItem(m); }
        for (int y = currentYear; y <= currentYear + 2; y++) { fromYearCombo.addItem(y); toYearCombo.addItem(y); }

        fromDayCombo.setSelectedItem(today.getDayOfMonth());
        fromMonthCombo.setSelectedItem(today.getMonthValue());
        fromYearCombo.setSelectedItem(today.getYear());

        toDayCombo.setSelectedItem(tomorrow.getDayOfMonth());
        toMonthCombo.setSelectedItem(tomorrow.getMonthValue());
        toYearCombo.setSelectedItem(tomorrow.getYear());
    }

    private JPanel buildDatePanel(JComboBox<Integer> dayCombo, JComboBox<Integer> monthCombo, JComboBox<Integer> yearCombo) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        p.add(dayCombo);
        p.add(new JLabel("/"));
        p.add(monthCombo);
        p.add(new JLabel("/"));
        p.add(yearCombo);
        return p;
    }

    private LocalDate readDate(JComboBox<Integer> dayCombo, JComboBox<Integer> monthCombo, JComboBox<Integer> yearCombo) {
        int day   = (Integer) dayCombo.getSelectedItem();
        int month = (Integer) monthCombo.getSelectedItem();
        int year  = (Integer) yearCombo.getSelectedItem();
        return LocalDate.of(year, month, day);
    }

    private void handleSearch() {
        searchMessageLabel.setText(" ");
        vehicleTableModel.setRowCount(0);
        currentSearchResults = null;

        LocalDate from, to;
        try {
            from = readDate(fromDayCombo, fromMonthCombo, fromYearCombo);
            to   = readDate(toDayCombo, toMonthCombo, toYearCombo);
        } catch (java.time.DateTimeException ex) {
            searchMessageLabel.setText("Μη έγκυρη ημερομηνία (π.χ. 31 Φεβρουαρίου).");
            return;
        }

        String category = (String) categoryCombo.getSelectedItem();

        try {
            currentSearchResults = vehicleController.searchAvailable(from, to, category);
            if (currentSearchResults.isEmpty()) {
                searchMessageLabel.setText("Δεν βρέθηκαν διαθέσιμα οχήματα για τις επιλεγμένες ημερομηνίες.");
                return;
            }
            for (Vehicle v : currentSearchResults) {
                double cost = vehicleController.calculateCost(v, from, to);
                vehicleTableModel.addRow(new Object[]{
                        v.getPlate(), v.getBrand(), v.getModel(),
                        v.getCategory(), v.getDailyRate(), String.format("%.2f", cost)
                });
            }
        } catch (IllegalArgumentException ex) {
            searchMessageLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            searchMessageLabel.setText("Σφάλμα: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleBook() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow < 0 || currentSearchResults == null) {
            JOptionPane.showMessageDialog(this, "Επιλέξτε πρώτα ένα όχημα από τον πίνακα.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate from, to;
        try {
            from = readDate(fromDayCombo, fromMonthCombo, fromYearCombo);
            to   = readDate(toDayCombo, toMonthCombo, toYearCombo);
        } catch (java.time.DateTimeException ex) {
            JOptionPane.showMessageDialog(this, "Μη έγκυρη ημερομηνία.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Vehicle selected = currentSearchResults.get(selectedRow);
        double cost = vehicleController.calculateCost(selected, from, to);

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Επιβεβαίωση κράτησης:\n%s %s (%s)\nΑπό: %s  Έως: %s\nΣυνολικό κόστος: %.2f €",
                        selected.getBrand(), selected.getModel(), selected.getPlate(), from, to, cost),
                "Επιβεβαίωση Κράτησης", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            bookingController.createBooking(authController.getLoggedInUser(),
                    selected.getVehicleId(), from, to, selected.getDailyRate());
            JOptionPane.showMessageDialog(this, "Η κράτηση καταχωρήθηκε επιτυχώς!", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            vehicleTableModel.setRowCount(0);
            currentSearchResults = null;
            searchMessageLabel.setText(" ");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα: " + ex.getMessage(), "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── Tab 2: Οι Κρατήσεις μου ───────────────────────────────────────────────

    private JPanel buildMyBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(UITheme.BACKGROUND);

        String[] cols = {"#", "ID Κράτησης", "ID Οχήματος", "Από", "Έως", "Κατάσταση", "Κόστος (€)"};
        bookingTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.getColumnModel().getColumn(0).setMinWidth(0);
        bookingTable.getColumnModel().getColumn(0).setMaxWidth(0);
        UITheme.styleTable(bookingTable);
        UITheme.applyZebraRows(bookingTable);
        UITheme.applyStatusBadgeRenderer(bookingTable, 5);
        JScrollPane bookingScroll = new JScrollPane(bookingTable);
        bookingScroll.setBorder(BorderFactory.createLineBorder(UITheme.OUTLINE_VARIANT));
        panel.add(bookingScroll, BorderLayout.CENTER);

        bookingMessageLabel = new JLabel(" ");
        bookingMessageLabel.setFont(UITheme.fontSmall());
        bookingMessageLabel.setForeground(UITheme.ERROR);

        JButton refreshBtn = new JButton("Ανανέωση");
        UITheme.styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadMyBookings());

        JButton cancelBtn = new JButton("Ακύρωση επιλεγμένης κράτησης");
        UITheme.styleDangerButton(cancelBtn);
        cancelBtn.addActionListener(e -> handleCancel());

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(UITheme.BACKGROUND);
        southPanel.add(bookingMessageLabel);
        southPanel.add(refreshBtn);
        southPanel.add(cancelBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMyBookings() {
        bookingTableModel.setRowCount(0);
        currentBookings = null;
        bookingMessageLabel.setText(" ");
        try {
            currentBookings = bookingController.getMyBookings(authController.getLoggedInUser());
            if (currentBookings.isEmpty()) {
                bookingMessageLabel.setText("Δεν έχετε κρατήσεις.");
                return;
            }
            for (int i = 0; i < currentBookings.size(); i++) {
                Booking b = currentBookings.get(i);
                bookingTableModel.addRow(new Object[]{
                        i,
                        b.getBookingId(), b.getVehicleId(),
                        b.getStartDate(), b.getEndDate(),
                        b.getStatus(), String.format("%.2f", b.getTotalCost())
                });
            }
        } catch (Exception ex) {
            bookingMessageLabel.setText("Σφάλμα φόρτωσης: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleCancel() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow < 0 || currentBookings == null) {
            JOptionPane.showMessageDialog(this, "Επιλέξτε πρώτα μια κράτηση.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = currentBookings.get(selectedRow);

        if (!"ACTIVE".equals(booking.getStatus())) {
            JOptionPane.showMessageDialog(this, "Μόνο ACTIVE κρατήσεις μπορούν να ακυρωθούν.", "Προσοχή", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Ακύρωση κράτησης #%d;\nΟχ. ID: %d  |  %s → %s",
                        booking.getBookingId(), booking.getVehicleId(),
                        booking.getStartDate(), booking.getEndDate()),
                "Επιβεβαίωση Ακύρωσης", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            bookingController.cancelBooking(booking.getBookingId(), booking.getVehicleId());
            JOptionPane.showMessageDialog(this, "Η κράτηση ακυρώθηκε.", "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
            loadMyBookings();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα: " + ex.getMessage(), "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── Logout ─────────────────────────────────────────────────────────────────

    private void logout() {
        authController.logout();
        dispose();
        new LoginView(new controller.AuthController()).setVisible(true);
    }
}
