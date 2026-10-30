import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParkingLot {
    private final ParkingManager parkingManager;
    private final FareCalculator fareCalculator;
    public ParkingLot(ParkingManager parkingManager, FareCalculator fareCalculator) {
        this.parkingManager = parkingManager;
        this.fareCalculator = fareCalculator;
    }
    public Ticket parkVehicle(Vehicle vehicle) {
        ParkingSpot spot=parkingManager.parkVehicle(vehicle);
        if(spot!=null){
           Ticket t=new Ticket(vehicle,spot);
            System.out.println("the vehicle with number plate "+vehicle.getLicensePlate()+"is parked on the spot "+spot.getSpotNumber()+"with size of the spot is "+spot.getParkingSpotSize());
            return t;
        }
        else{
            System.out.println("parking is full for the vehicle of size "+vehicle.getSize());
            return null;
        }
    }
    public BigDecimal leaveVehicle(Ticket ticket) {
        if(ticket!=null&&ticket.getExitTime()!=null){
            ticket.setExitTime(LocalDateTime.now());
            parkingManager.unparkVehicle(ticket.getVehicle());
            BigDecimal fare=fareCalculator.calculate(ticket);
            System.out.println("vehicle of number plate "+ticket.getVehicle().getLicensePlate()+" is going out ");
            return fare;
        }
        else {
            return BigDecimal.ZERO;
        }
    }
}
