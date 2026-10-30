import java.math.BigDecimal;

public class BaseFareStrategy implements FareStrategy {
     private  static final  BigDecimal SMALL_VEHICLE_RATE= new BigDecimal("1.0");
     private static final BigDecimal MEDIUM_VEHICLE_RATE= new BigDecimal("2.0");
     private static  final  BigDecimal LARGE_VEHICLE_RATE= new BigDecimal("3.0");
    @Override
    public BigDecimal getFare(Ticket ticket, BigDecimal inputFare) {
        BigDecimal fare=inputFare;
        BigDecimal rate;
        // what i want i want to check if the current bvehicle belong to which size then we will get thr rate then after that we will get the according to time we will multiply woth tjhe multiplie and add that to base price
        switch (ticket.getVehicle().getSize()){
            case VehicleSize.SMALL:
                rate=SMALL_VEHICLE_RATE;
                break;

            case VehicleSize.MEDIUM:
                rate=MEDIUM_VEHICLE_RATE;
                break;
            case VehicleSize.LARGE:
                rate =LARGE_VEHICLE_RATE;
                break;
            default:
                rate=SMALL_VEHICLE_RATE;
                break;
        }
        return fare.add(ticket.getDuration().multiply(rate));
    }
}
