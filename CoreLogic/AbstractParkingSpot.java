// abstract class is liye banaya hai jisse doosre loog jo hain wo wo isko use kar payein baaki sab mehtod sam hi hai to baar baar kyon likhna pade

public class AbstractParkingSpot implements ParkingSpot {
    private final int spotNumber;
    private final VehicleSize parkingSpotSize;
    private Vehicle parkedVehicle;
    private boolean isAvailable;
    public  AbstractParkingSpot(int spotNumber, VehicleSize parkingSpotSize) {
        this.spotNumber = spotNumber;
        this.parkingSpotSize = parkingSpotSize;
        parkedVehicle = null;
        isAvailable = true;
    }
    @Override
    public int getSpotNumber() {
     return spotNumber;
    }

    @Override
    public VehicleSize getParkingSpotSize() {
        return parkingSpotSize;
    }

    @Override
    public Vehicle occupy(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isAvailable = false;
        return parkedVehicle;
    }

    @Override
    public void vacate() {
      this.parkedVehicle = null;
      this.isAvailable = true;
    }

    @Override
    public Vehicle getParkedVehicle() {
        return this.parkedVehicle;
    }

    @Override
    public boolean isAvailable() {
        return this.isAvailable;
    }
}
