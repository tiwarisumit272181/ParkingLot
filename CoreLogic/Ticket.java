import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Ticket {
    private final String TicketId;
    private final Vehicle Vehicle;
    private  final ParkingSpot parkingSpot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    Ticket(Vehicle v, ParkingSpot p){
        Vehicle = v;
        parkingSpot = p;
        entryTime = LocalDateTime.now();
        TicketId= UUID.randomUUID().toString();
    }
    public String getTicketId(){
        return TicketId;
    }
    public Vehicle getVehicle(){
        return Vehicle;
    }
    public ParkingSpot getParkingSpot(){
        return parkingSpot;
    }
    public LocalDateTime getEntryTime(){
        return entryTime;
    }
    public LocalDateTime getExitTime(){
        return exitTime;
    }
    public void setExitTime(LocalDateTime exitTime){
        this.exitTime = exitTime;
    }
    public BigDecimal getDuration(){
        LocalDateTime now= Objects.requireNonNullElseGet(exitTime, LocalDateTime::now);
        long minutes= Duration.between(entryTime, now).toMinutes();
        long chargeMinutes=Math.max(minutes,1);
        return BigDecimal.valueOf(chargeMinutes);
    }

}
