package model.dao;

import model.entities.Booking;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Δημιουργία κράτησης
    public void createBooking(int customerId, int vehicleId,
                              LocalDate from, LocalDate to,
                              double totalCost) throws SQLException {
        String sql = """
            INSERT INTO bookings (customer_id, vehicle_id, start_date, end_date, total_cost)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, vehicleId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ps.setDouble(5, totalCost);
            ps.executeUpdate();
        }
    }

    // Ακύρωση κράτησης
    public void cancelBooking(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    // Ολοκλήρωση κράτησης (επιστροφή οχήματος)
    public void completeBooking(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'COMPLETED' WHERE booking_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    // Όλες οι κρατήσεις ενός πελάτη
    public List<Booking> getBookingsByCustomer(int customerId) throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // Όλες οι ενεργές κρατήσεις
    public List<Booking> getAllActiveBookings() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = 'ACTIVE'";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Όλες οι κρατήσεις (για Admin)
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Βοηθητική μέθοδος
    private Booking mapRow(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("booking_id"),
                rs.getInt("customer_id"),
                rs.getInt("vehicle_id"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("status"),
                rs.getDouble("total_cost")
        );
    }
}