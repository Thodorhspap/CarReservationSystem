package stub;

import model.dao.VehicleDAO;
import model.entities.Vehicle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Εκτελεί τις μεθόδους χωρίς πραγματική βάση δεδομένων.
// Χρησιμοποιείται αποκλειστικά στα unit tests.
public class StubVehicleDAO extends VehicleDAO {

    private final List<Vehicle> vehicles = new ArrayList<>();

    // Καταγράφει τις τελευταίες κλήσεις για assertions στα tests
    public int    lastUpdatedVehicleId = -1;
    public String lastUpdatedStatus    = null;

    public StubVehicleDAO() {
        vehicles.add(new Vehicle(1, "ΑΒΓ-1234", "Toyota",  "Yaris",    "ECONOMY", "AVAILABLE", 0, 35.0));
        vehicles.add(new Vehicle(2, "ΔΕΖ-5678", "Hyundai", "Tucson",   "SUV",     "AVAILABLE", 0, 65.0));
        vehicles.add(new Vehicle(3, "ΗΘΙ-9012", "BMW",     "3 Series", "LUXURY",  "AVAILABLE", 0, 120.0));
    }

    @Override
    public List<Vehicle> getAvailableVehicles(LocalDate from, LocalDate to) throws SQLException {
        return new ArrayList<>(vehicles);
    }

    @Override
    public List<Vehicle> getAllVehicles() throws SQLException {
        return new ArrayList<>(vehicles);
    }

    @Override
    public void updateStatus(int vehicleId, String status) throws SQLException {
        lastUpdatedVehicleId = vehicleId;
        lastUpdatedStatus    = status;
        vehicles.stream()
                .filter(v -> v.getVehicleId() == vehicleId)
                .findFirst()
                .ifPresent(v -> v.setStatus(status));
    }

    @Override
    public void addVehicle(Vehicle v) throws SQLException {
        vehicles.add(v);
    }

    @Override
    public void deleteVehicle(int vehicleId) throws SQLException {
        vehicles.removeIf(v -> v.getVehicleId() == vehicleId);
    }
}
