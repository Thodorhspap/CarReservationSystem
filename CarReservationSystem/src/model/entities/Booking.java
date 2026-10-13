package model.entities;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private int customerId;
    private int vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private double totalCost;

    public Booking() {}

    public Booking(int bookingId, int customerId, int vehicleId,
                   LocalDate startDate, LocalDate endDate,
                   String status, double totalCost) {
        this.bookingId  = bookingId;
        this.customerId = customerId;
        this.vehicleId  = vehicleId;
        this.startDate  = startDate;
        this.endDate    = endDate;
        this.status     = status;
        this.totalCost  = totalCost;
    }

    public int getBookingId()        { return bookingId; }
    public int getCustomerId()       { return customerId; }
    public int getVehicleId()        { return vehicleId; }
    public LocalDate getStartDate()  { return startDate; }
    public LocalDate getEndDate()    { return endDate; }
    public String getStatus()        { return status; }
    public double getTotalCost()     { return totalCost; }

    public void setBookingId(int bookingId)         { this.bookingId = bookingId; }
    public void setCustomerId(int customerId)       { this.customerId = customerId; }
    public void setVehicleId(int vehicleId)         { this.vehicleId = vehicleId; }
    public void setStartDate(LocalDate startDate)   { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate)       { this.endDate = endDate; }
    public void setStatus(String status)            { this.status = status; }
    public void setTotalCost(double totalCost)      { this.totalCost = totalCost; }

    @Override
    public String toString() {
        return "Booking #" + bookingId + " | Vehicle: " + vehicleId +
                " | " + startDate + " → " + endDate + " | " + status;
    }
}