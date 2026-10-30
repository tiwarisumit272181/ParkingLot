import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this will handle spot allocation vehicle tracking and release the spot when the vehicle exits
public class ParkingManager {
   private final Map<VehicleSize, List<ParkingSpot>> availableSpots;
   private final Map<Vehicle,ParkingSpot> vehicleSpotMap;
   public ParkingManager(Map<VehicleSize, List<ParkingSpot>> parkingSpots) {
       this.availableSpots = parkingSpots;
       this.vehicleSpotMap = new HashMap<>();
   }
   // first we will fetch loop through the all sizes and then we will see the spot assoicated with that if that sise id fit with the vehicle size and thwn we willse if any spot is avlaible to park that vehicle
  private synchronized ParkingSpot getParkingSpot(Vehicle v){
       for(VehicleSize size:VehicleSize.values()){
           List<ParkingSpot> spots = availableSpots.get(size);
           if(spots!=null){
               for(ParkingSpot spot:spots){
                   if(spot.getParkingSpotSize().ordinal()>=v.getSize().ordinal()&&spot.isAvailable()){
                       return spot;
                   }
               }
           }
       }
       return null;
  }
  public synchronized ParkingSpot parkVehicle(Vehicle v){
       ParkingSpot parkingSpot=getParkingSpot(v);
       if(parkingSpot!=null){
           parkingSpot.occupy(v);
           vehicleSpotMap.put(v,parkingSpot);
           availableSpots.get(v.getSize()).remove(parkingSpot);
           return parkingSpot;
       }
       return null;
  }
  public  synchronized void unparkVehicle(Vehicle v){
       ParkingSpot spot=vehicleSpotMap.remove(v);
       if(spot!=null){
           spot.vacate();
           availableSpots.get(spot.getParkingSpotSize()).add(spot);
       }
  }

}
