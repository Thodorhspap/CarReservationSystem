package controller;

import model.dao.VehicleDAO;
import model.entities.Vehicle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class VehicleController {

    private final VehicleDAO vehicleDAO;

    public VehicleController() {
        this.vehicleDAO = new VehicleDAO();
    }

    // Constructor για unit testing — δέχεται stub DAO αντί για αληθινό
    VehicleController(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    // UC1 — Αναζήτηση διαθέσιμων οχημάτων για τις δοσμένες ημερομηνίες.
    // Αν category είναι null ή "ALL" επιστρέφει όλες τις κατηγορίες.
    public List<Vehicle> searchAvailable(LocalDate from, LocalDate to, String category)
            throws SQLException {

        if (from == null || to == null) throw new IllegalArgumentException("Οι ημερομηνίες είναι υποχρεωτικές.");
        if (!to.isAfter(from))         throw new IllegalArgumentException("Η ημερομηνία επιστροφής πρέπει να είναι μετά την παραλαβή.");

        List<Vehicle> results = vehicleDAO.getAvailableVehicles(from, to);

        if (category != null && !category.equals("ALL")) {
            results = results.stream()
                    .filter(v -> v.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        return results;
    }

    // Υπολογισμός κόστους κράτησης
    public double calculateCost(Vehicle vehicle, LocalDate from, LocalDate to) {
        long days = to.toEpochDay() - from.toEpochDay();
        return days * vehicle.getDailyRate();
    }

    // Για Admin — προσθήκη οχήματος
    public void addVehicle(Vehicle v) throws SQLException {
        vehicleDAO.addVehicle(v);
    }

    // Για Admin — διαγραφή οχήματος
    public void deleteVehicle(int vehicleId) throws SQLException {
        vehicleDAO.deleteVehicle(vehicleId);
    }

    // Για Secretary — ενημέρωση status
    public void updateStatus(int vehicleId, String status) throws SQLException {
        vehicleDAO.updateStatus(vehicleId, status);
    }

    public List<Vehicle> getAllVehicles() throws SQLException {
        return vehicleDAO.getAllVehicles();
    }
}
