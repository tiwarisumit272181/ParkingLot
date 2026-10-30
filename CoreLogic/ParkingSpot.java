public interface ParkingSpot {
    int getSpotNumber();
    VehicleSize getParkingSpotSize();
    Vehicle occupy(Vehicle vehicle);
    void vacate();
    Vehicle getParkedVehicle();
    boolean isAvailable();
}
