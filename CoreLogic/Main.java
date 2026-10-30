import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<ParkingSpot>compactSpots=new ArrayList<>(List.of(new CompactSpot(201)));
        List<ParkingSpot>regularSpots=new ArrayList<>(List.of(new RegularSpot(301)));
        List<ParkingSpot>oversizeSpots=new ArrayList<>(List.of(new OversizedSpot(401)));
        Map<VehicleSize,List<ParkingSpot>> mp=new HashMap<>();
        mp.put(VehicleSize.SMALL,compactSpots);
        mp.put(VehicleSize.MEDIUM,regularSpots);
        mp.put(VehicleSize.LARGE,oversizeSpots);
        ParkingManager pm=new ParkingManager(mp);
        List<FareStrategy>fps=new ArrayList<>();
        FareStrategy strategy =new BaseFareStrategy();
        fps.add(strategy);
        FareStrategy peakHourStrategy =new PeakHourFareStrategy();
        fps.add(peakHourStrategy);
        FareCalculator calculator=new FareCalculator(fps);
        // initiate parking lot facade
        ParkingLot pl=new ParkingLot(pm, calculator);
        // now we will create vehicle and do the testing
        Vehicle car=new Car("mh-2134");
        Vehicle bike=new Bike("bike271312");
        Vehicle truck=new Truck("truck271314");
        Ticket carTicket=pl.parkVehicle(car);
        Ticket bikeTicket=pl.parkVehicle(bike);
        // now try to park whrn regular spot is full
        Vehicle car2=new Car("car2713145");
        Vehicle car3=new Car("car2713146");
        Ticket car2Ticekt=pl.parkVehicle(car2);
        Ticket car3Ticekt=pl.parkVehicle(car3);


    }
}
