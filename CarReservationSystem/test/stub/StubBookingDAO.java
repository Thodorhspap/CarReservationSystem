package stub;

import model.dao.BookingDAO;
import model.entities.Booking;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StubBookingDAO extends BookingDAO {

    private final List<Booking> bookings = new ArrayList<>();
    private int nextId = 1;

    public int lastCancelledId  = -1;
    public int lastCompletedId  = -1;

    @Override
    public void createBooking(int customerId, int vehicleId,
                              LocalDate from, LocalDate to,
                              double totalCost) throws SQLException {
        bookings.add(new Booking(nextId++, customerId, vehicleId, from, to, "ACTIVE", totalCost));
    }

    @Override
    public void cancelBooking(int bookingId) throws SQLException {
        lastCancelledId = bookingId;
        bookings.stream()
                .filter(b -> b.getBookingId() == bookingId)
                .findFirst()
                .ifPresent(b -> b.setStatus("CANCELLED"));
    }

    @Override
    public void completeBooking(int bookingId) throws SQLException {
        lastCompletedId = bookingId;
        bookings.stream()
                .filter(b -> b.getBookingId() == bookingId)
                .findFirst()
                .ifPresent(b -> b.setStatus("COMPLETED"));
    }

    @Override
    public List<Booking> getBookingsByCustomer(int customerId) throws SQLException {
        return bookings.stream()
                .filter(b -> b.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> getAllActiveBookings() throws SQLException {
        return bookings.stream()
                .filter(b -> "ACTIVE".equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> getAllBookings() throws SQLException {
        return new ArrayList<>(bookings);
    }

    public List<Booking> getAll() {
        return bookings;
    }
}
