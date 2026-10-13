package model.entities;

public class Vehicle {
    private int vehicleId;
    private String plate;
    private String brand;
    private String model;
    private String category;
    private String status;
    private int mileage;
    private double dailyRate;

    public Vehicle() {}

    public Vehicle(int vehicleId, String plate, String brand, String model,
                   String category, String status, int mileage, double dailyRate) {
        this.vehicleId = vehicleId;
        this.plate     = plate;
        this.brand     = brand;
        this.model     = model;
        this.category  = category;
        this.status    = status;
        this.mileage   = mileage;
        this.dailyRate = dailyRate;
    }

    public int getVehicleId()     { return vehicleId; }
    public String getPlate()      { return plate; }
    public String getBrand()      { return brand; }
    public String getModel()      { return model; }
    public String getCategory()   { return category; }
    public String getStatus()     { return status; }
    public int getMileage()       { return mileage; }
    public double getDailyRate()  { return dailyRate; }

    public void setVehicleId(int vehicleId)     { this.vehicleId = vehicleId; }
    public void setPlate(String plate)          { this.plate = plate; }
    public void setBrand(String brand)          { this.brand = brand; }
    public void setModel(String model)          { this.model = model; }
    public void setCategory(String category)    { this.category = category; }
    public void setStatus(String status)        { this.status = status; }
    public void setMileage(int mileage)         { this.mileage = mileage; }
    public void setDailyRate(double dailyRate)  { this.dailyRate = dailyRate; }

    @Override
    public String toString() {
        return plate + " | " + brand + " " + model + " | " + category + " | " + dailyRate + "€/day";
    }
}