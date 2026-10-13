package controller;

import model.dao.BookingDAO;
import model.dao.CustomerDAO;
import model.dao.VehicleDAO;
import model.entities.Booking;
import model.entities.Customer;
import model.entities.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BookingController {

    private final BookingDAO bookingDAO;
    private final VehicleDAO vehicleDAO;
    private final CustomerDAO customerDAO;

    public BookingController() {
        this.bookingDAO  = new BookingDAO();
        this.vehicleDAO  = new VehicleDAO();
        this.customerDAO = new CustomerDAO();
    }

    // Constructor για unit testing — δέχεται stub DAOs
    BookingController(BookingDAO bookingDAO, VehicleDAO vehicleDAO, CustomerDAO customerDAO) {
        this.bookingDAO  = bookingDAO;
        this.vehicleDAO  = vehicleDAO;
        this.customerDAO = customerDAO;
    }

    // UC2 — Δημιουργία κράτησης.
    // Βρίσκει τον customer από τον logged-in user, υπολογίζει κόστος,
    // αποθηκεύει την κράτηση και ορίζει το όχημα ως RESERVED.
    public void createBooking(User loggedInUser, int vehicleId,
                              LocalDate from, LocalDate to,
                              double dailyRate) throws SQLException {

        Customer customer = customerDAO.getCustomerByUserId(loggedInUser.getUserId());
        if (customer == null) throw new SQLException("Δεν βρέθηκε πελάτης για τον χρήστη: " + loggedInUser.getUsername());

        long days = to.toEpochDay() - from.toEpochDay();
        double totalCost = days * dailyRate;

        bookingDAO.createBooking(customer.getCustomerId(), vehicleId, from, to, totalCost);
        vehicleDAO.updateStatus(vehicleId, "RESERVED");
    }

    // UC3 — Ακύρωση κράτησης.
    // Θέτει την κράτηση CANCELLED και αποδεσμεύει το όχημα (AVAILABLE).
    public void cancelBooking(int bookingId, int vehicleId) throws SQLException {
        bookingDAO.cancelBooking(bookingId);
        vehicleDAO.updateStatus(vehicleId, "AVAILABLE");
    }

    // Ολοκλήρωση κράτησης (επιστροφή οχήματος από Secretary).
    // Θέτει την κράτηση COMPLETED και αποδεσμεύει το όχημα (AVAILABLE).
    public void completeBooking(int bookingId, int vehicleId) throws SQLException {
        bookingDAO.completeBooking(bookingId);
        vehicleDAO.updateStatus(vehicleId, "AVAILABLE");
    }

    // Κρατήσεις του logged-in customer
    public List<Booking> getMyBookings(User loggedInUser) throws SQLException {
        Customer customer = customerDAO.getCustomerByUserId(loggedInUser.getUserId());
        if (customer == null) return List.of();
        return bookingDAO.getBookingsByCustomer(customer.getCustomerId());
    }

    // Για Secretary — όλες οι ενεργές κρατήσεις
    public List<Booking> getAllActiveBookings() throws SQLException {
        return bookingDAO.getAllActiveBookings();
    }

    // Για Admin — όλες οι κρατήσεις ανεξαρτήτως status
    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }
}
