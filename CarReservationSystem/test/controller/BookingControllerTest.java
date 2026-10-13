package controller;

import model.entities.Booking;
import model.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stub.StubBookingDAO;
import stub.StubCustomerDAO;
import stub.StubVehicleDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest {

    private BookingController controller;
    private StubBookingDAO    stubBookingDAO;
    private StubVehicleDAO    stubVehicleDAO;
    private StubCustomerDAO   stubCustomerDAO;

    // User που αντιστοιχεί στο customer1 (user_id=3) του sample data
    private final User customerUser = new User(3, "customer1", "customer123", "CUSTOMER");

    @BeforeEach
    void setUp() {
        stubBookingDAO  = new StubBookingDAO();
        stubVehicleDAO  = new StubVehicleDAO();
        stubCustomerDAO = new StubCustomerDAO();
        controller      = new BookingController(stubBookingDAO, stubVehicleDAO, stubCustomerDAO);
    }

    // ── createBooking ──────────────────────────────────────────────────────────

    @Test
    void createBooking_validInput_bookingIsCreated() throws SQLException {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 4); // 3 μέρες

        controller.createBooking(customerUser, 1, from, to, 35.0);

        List<Booking> bookings = stubBookingDAO.getAll();
        assertEquals(1, bookings.size(), "Πρέπει να δημιουργηθεί ακριβώς 1 κράτηση");
    }

    @Test
    void createBooking_validInput_totalCostIsCorrect() throws SQLException {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 4); // 3 μέρες × 35€ = 105€

        controller.createBooking(customerUser, 1, from, to, 35.0);

        Booking created = stubBookingDAO.getAll().get(0);
        assertEquals(105.0, created.getTotalCost(), 0.001, "Συνολικό κόστος: 3 × 35€ = 105€");
    }

    @Test
    void createBooking_validInput_vehicleStatusBecomesReserved() throws SQLException {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 4);

        controller.createBooking(customerUser, 1, from, to, 35.0);

        assertEquals(1,          stubVehicleDAO.lastUpdatedVehicleId);
        assertEquals("RESERVED", stubVehicleDAO.lastUpdatedStatus);
    }

    @Test
    void createBooking_customerNotFound_throwsSQLException() {
        User unknownUser = new User(999, "ghost", "pass", "CUSTOMER");
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 4);

        assertThrows(SQLException.class, () ->
                controller.createBooking(unknownUser, 1, from, to, 35.0));
    }

    @Test
    void createBooking_bookingHasActiveStatus() throws SQLException {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 3);

        controller.createBooking(customerUser, 1, from, to, 65.0);

        Booking created = stubBookingDAO.getAll().get(0);
        assertEquals("ACTIVE", created.getStatus());
    }

    // ── cancelBooking ──────────────────────────────────────────────────────────

    @Test
    void cancelBooking_existingBooking_statusIsCancelled() throws SQLException {
        // Πρώτα δημιουργούμε κράτηση
        controller.createBooking(customerUser, 1, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 4), 35.0);
        int bookingId = stubBookingDAO.getAll().get(0).getBookingId();

        controller.cancelBooking(bookingId, 1);

        assertEquals(bookingId, stubBookingDAO.lastCancelledId);
    }

    @Test
    void cancelBooking_existingBooking_vehicleBecomesAvailable() throws SQLException {
        controller.createBooking(customerUser, 2, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 5), 65.0);
        int bookingId = stubBookingDAO.getAll().get(0).getBookingId();

        controller.cancelBooking(bookingId, 2);

        assertEquals(2,           stubVehicleDAO.lastUpdatedVehicleId);
        assertEquals("AVAILABLE", stubVehicleDAO.lastUpdatedStatus);
    }

    // ── completeBooking ────────────────────────────────────────────────────────

    @Test
    void completeBooking_existingBooking_statusIsCompleted() throws SQLException {
        controller.createBooking(customerUser, 1, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 3), 35.0);
        int bookingId = stubBookingDAO.getAll().get(0).getBookingId();

        controller.completeBooking(bookingId, 1);

        assertEquals(bookingId, stubBookingDAO.lastCompletedId);
    }

    @Test
    void completeBooking_vehicleBecomesAvailable() throws SQLException {
        controller.createBooking(customerUser, 3, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7), 120.0);
        int bookingId = stubBookingDAO.getAll().get(0).getBookingId();

        controller.completeBooking(bookingId, 3);

        assertEquals(3,           stubVehicleDAO.lastUpdatedVehicleId);
        assertEquals("AVAILABLE", stubVehicleDAO.lastUpdatedStatus);
    }

    // ── getMyBookings ──────────────────────────────────────────────────────────

    @Test
    void getMyBookings_afterCreation_returnsBookingList() throws SQLException {
        controller.createBooking(customerUser, 1, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 4), 35.0);
        controller.createBooking(customerUser, 2, LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 3), 65.0);

        List<Booking> myBookings = controller.getMyBookings(customerUser);

        assertEquals(2, myBookings.size(), "Ο πελάτης πρέπει να έχει 2 κρατήσεις");
    }

    @Test
    void getMyBookings_unknownUser_returnsEmptyList() throws SQLException {
        User unknownUser = new User(999, "ghost", "pass", "CUSTOMER");

        List<Booking> result = controller.getMyBookings(unknownUser);

        assertTrue(result.isEmpty());
    }
}
