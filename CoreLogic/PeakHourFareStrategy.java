import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PeakHourFareStrategy  implements FareStrategy {
    private static final BigDecimal PEAK_HOUR_SURGE_RATE = new BigDecimal("1.5");
    @Override
    public BigDecimal getFare(Ticket ticket, BigDecimal inputFare) {
        // what i want is current price to onceremtn if we found that the entry time lying in the peak hour
        BigDecimal fare=inputFare;
        BigDecimal rate=BigDecimal.ZERO;
        if(isPeakHour(ticket.getEntryTime())){
            fare=PEAK_HOUR_SURGE_RATE;
        }
        return fare.add(rate.multiply(ticket.getDuration()));
    }
    private boolean isPeakHour(LocalDateTime t){
        int hour=t.getHour();
        return hour >= 7 && hour <= 10 || hour >= 16 && hour < 18;
    }
}
