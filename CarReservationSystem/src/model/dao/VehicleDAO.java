package model.dao;

import model.entities.Vehicle;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // Όλα τα οχήματα
    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Διαθέσιμα οχήματα για συγκεκριμένες ημερομηνίες
    public List<Vehicle> getAvailableVehicles(LocalDate from, LocalDate to) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = """
            SELECT * FROM vehicles
            WHERE status = 'AVAILABLE'
            AND vehicle_id NOT IN (
                SELECT vehicle_id FROM bookings
                WHERE status = 'ACTIVE'
                AND NOT (end_date < ? OR start_date > ?)
            )
            """;
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // Προσθήκη οχήματος
    public void addVehicle(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicles (plate, brand, model, category, daily_rate) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, v.getPlate());
            ps.setString(2, v.getBrand());
            ps.setString(3, v.getModel());
            ps.setString(4, v.getCategory());
            ps.setDouble(5, v.getDailyRate());
            ps.executeUpdate();
        }
    }

    // Ενημέρωση status οχήματος
    public void updateStatus(int vehicleId, String status) throws SQLException {
        String sql = "UPDATE vehicles SET status = ? WHERE vehicle_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, vehicleId);
            ps.executeUpdate();
        }
    }

    // Διαγραφή οχήματος
    public void deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getInstance().prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        }
    }

    // Βοηθητική μέθοδος — μετατρέπει γραμμή DB σε Vehicle object
    private Vehicle mapRow(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("vehicle_id"),
                rs.getString("plate"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getString("category"),
                rs.getString("status"),
                rs.getInt("mileage"),
                rs.getDouble("daily_rate")
        );
    }
}