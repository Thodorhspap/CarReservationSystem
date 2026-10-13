package controller;

import model.entities.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stub.StubVehicleDAO;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleControllerTest {

    private VehicleController controller;
    private StubVehicleDAO    stubDAO;

    @BeforeEach
    void setUp() {
        stubDAO    = new StubVehicleDAO();
        controller = new VehicleController(stubDAO);
    }

    // ── calculateCost ──────────────────────────────────────────────────────────

    @Test
    void calculateCost_threeDays_returnsCorrectAmount() {
        Vehicle v  = new Vehicle(1, "ΑΒΓ-1234", "Toyota", "Yaris", "ECONOMY", "AVAILABLE", 0, 35.0);
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 4); // 3 μέρες

        double cost = controller.calculateCost(v, from, to);

        assertEquals(105.0, cost, 0.001, "3 μέρες × 35€ = 105€");
    }

    @Test
    void calculateCost_oneDay_returnsDailyRate() {
        Vehicle v  = new Vehicle(2, "ΔΕΖ-5678", "Hyundai", "Tucson", "SUV", "AVAILABLE", 0, 65.0);
        LocalDate from = LocalDate.of(2025, 8, 1);
        LocalDate to   = LocalDate.of(2025, 8, 2); // 1 μέρα

        double cost = controller.calculateCost(v, from, to);

        assertEquals(65.0, cost, 0.001, "1 μέρα × 65€ = 65€");
    }

    @Test
    void calculateCost_tenDays_returnsCorrectAmount() {
        Vehicle v  = new Vehicle(3, "ΗΘΙ-9012", "BMW", "3 Series", "LUXURY", "AVAILABLE", 0, 120.0);
        LocalDate from = LocalDate.of(2025, 9, 1);
        LocalDate to   = LocalDate.of(2025, 9, 11); // 10 μέρες

        double cost = controller.calculateCost(v, from, to);

        assertEquals(1200.0, cost, 0.001, "10 μέρες × 120€ = 1200€");
    }

    // ── searchAvailable — date validation ─────────────────────────────────────

    @Test
    void searchAvailable_nullFromDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.searchAvailable(null, LocalDate.of(2025, 7, 10), "ALL"));
    }

    @Test
    void searchAvailable_nullToDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.searchAvailable(LocalDate.of(2025, 7, 1), null, "ALL"));
    }

    @Test
    void searchAvailable_toDateBeforeFromDate_throwsIllegalArgumentException() {
        LocalDate from = LocalDate.of(2025, 7, 10);
        LocalDate to   = LocalDate.of(2025, 7, 5);

        assertThrows(IllegalArgumentException.class, () ->
                controller.searchAvailable(from, to, "ALL"));
    }

    @Test
    void searchAvailable_sameDates_throwsIllegalArgumentException() {
        LocalDate date = LocalDate.of(2025, 7, 10);

        assertThrows(IllegalArgumentException.class, () ->
                controller.searchAvailable(date, date, "ALL"));
    }

    // ── searchAvailable — filtering ────────────────────────────────────────────

    @Test
    void searchAvailable_categoryAll_returnsAllVehicles() throws Exception {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 10);

        List<Vehicle> results = controller.searchAvailable(from, to, "ALL");

        assertEquals(3, results.size(), "Αναμένονται 3 οχήματα από το stub");
    }

    @Test
    void searchAvailable_economyFilter_returnsOnlyEconomy() throws Exception {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 10);

        List<Vehicle> results = controller.searchAvailable(from, to, "ECONOMY");

        assertEquals(1, results.size());
        assertEquals("ECONOMY", results.get(0).getCategory());
    }

    @Test
    void searchAvailable_suvFilter_returnsOnlySuv() throws Exception {
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to   = LocalDate.of(2025, 7, 10);

        List<Vehicle> results = controller.searchAvailable(from, to, "SUV");

        assertTrue(results.stream().allMatch(v -> v.getCategory().equals("SUV")));
    }
}
