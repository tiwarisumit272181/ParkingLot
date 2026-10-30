# Parking Lot System Architecture (Java Implementation)

This document contains the complete, production-ready Java implementation for the **Object-Oriented Parking Lot System**, structured into modular layers following clean code and SOLID design principles.

---

## Architecture Overview & Design Patterns

1. **Facade Pattern (`ParkingLot.java`)**: Acts as a single entry point for clients, simplifying interaction with underlying subsystems (`ParkingManager`, `FareCalculator`).
2. **Strategy Pattern (`FareStrategy`, `BaseFareStrategy`, `PeakHoursFareStrategy`)**: Decouples pricing logic from core operations, allowing dynamic rules (e.g., peak-hour dynamic pricing) without modifying core classes.
3. **Template / Base Class Pattern (`AbstractParkingSpot.java`)**: Reduces boilerplate by providing common state management (`occupy`, `vacate`, `isAvailable`) across spot types.
4. **Thread-Safe Allocation (`ParkingManager.java`)**: Uses `synchronized` method boundaries to ensure atomic spot assignment and prevent double-booking under multi-threaded concurrency.

---

## 1. Enums & Vehicle Hierarchy

### `VehicleSize.java`
```java
public enum VehicleSize {
    SMALL,
    MEDIUM,
    LARGE
}
```

### `Vehicle.java`
```java
public interface Vehicle {
    String getLicensePlate();
    VehicleSize getSize();
}
```

### `Car.java`
```java
public class Car implements Vehicle {
    private final String licensePlate;

    public Car(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.MEDIUM;
    }
}
```

### `Motorcycle.java`
```java
public class Motorcycle implements Vehicle {
    private final String licensePlate;

    public Motorcycle(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.SMALL;
    }
}
```

### `Truck.java`
```java
public class Truck implements Vehicle {
    private final String licensePlate;

    public Truck(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String getLicensePlate() {
        return licensePlate;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.LARGE;
    }
}
```

---

## 2. Parking Spot Hierarchy

### `ParkingSpot.java`
```java
public interface ParkingSpot {
    int getSpotNumber();
    VehicleSize getSize();
    boolean isAvailable();
    void occupy(Vehicle vehicle);
    void vacate();
    Vehicle getParkedVehicle();
}
```

### `AbstractParkingSpot.java`
```java
public abstract class AbstractParkingSpot implements ParkingSpot {
    private final int spotNumber;
    private final VehicleSize spotSize;
    private boolean isAvailable;
    private Vehicle parkedVehicle;

    public AbstractParkingSpot(int spotNumber, VehicleSize spotSize) {
        this.spotNumber = spotNumber;
        this.spotSize = spotSize;
        this.isAvailable = true;
        this.parkedVehicle = null;
    }

    @Override
    public int getSpotNumber() { return spotNumber; }

    @Override
    public VehicleSize getSize() { return spotSize; }

    @Override
    public boolean isAvailable() { return isAvailable; }

    @Override
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    @Override
    public void occupy(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isAvailable = false;
    }

    @Override
    public void vacate() {
        this.parkedVehicle = null;
        this.isAvailable = true;
    }
}
```

### `CompactSpot.java`
```java
public class CompactSpot extends AbstractParkingSpot {
    public CompactSpot(int spotNumber) {
        super(spotNumber, VehicleSize.SMALL);
    }
}
```

### `RegularSpot.java`
```java
public class RegularSpot extends AbstractParkingSpot {
    public RegularSpot(int spotNumber) {
        super(spotNumber, VehicleSize.MEDIUM);
    }
}
```

### `OversizedSpot.java`
```java
public class OversizedSpot extends AbstractParkingSpot {
    public OversizedSpot(int spotNumber) {
        super(spotNumber, VehicleSize.LARGE);
    }
}
```

---

## 3. Ticket Entity

### `Ticket.java`
```java
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(Vehicle vehicle, ParkingSpot parkingSpot) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.entryTime = LocalDateTime.now();
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public BigDecimal calculateParkingDuration() {
        LocalDateTime exit = Objects.requireNonNullElseGet(exitTime, LocalDateTime::now);
        long minutes = Duration.between(entryTime, exit).toMinutes();
        long chargedMinutes = Math.max(1, minutes);
        return new BigDecimal(chargedMinutes);
    }
}
```

---

## 4. Fare Calculation Subsystem (Strategy Pattern)

### `FareStrategy.java`
```java
import java.math.BigDecimal;

public interface FareStrategy {
    BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare);
}
```

### `BaseFareStrategy.java`
```java
import java.math.BigDecimal;

public class BaseFareStrategy implements FareStrategy {
    private static final BigDecimal SMALL_VEHICLE_RATE = new BigDecimal("1.0");
    private static final BigDecimal MEDIUM_VEHICLE_RATE = new BigDecimal("2.0");
    private static final BigDecimal LARGE_VEHICLE_RATE = new BigDecimal("3.0");

    @Override
    public BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare) {
        BigDecimal fare = inputFare;
        BigDecimal rate;
        
        switch (ticket.getVehicle().getSize()) {
            case MEDIUM:
                rate = MEDIUM_VEHICLE_RATE;
                break;
            case LARGE:
                rate = LARGE_VEHICLE_RATE;
                break;
            default:
                rate = SMALL_VEHICLE_RATE;
                break;
        }

        return fare.add(rate.multiply(ticket.calculateParkingDuration()));
    }
}
```

### `PeakHoursFareStrategy.java`
```java
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PeakHoursFareStrategy implements FareStrategy {
    private static final BigDecimal PEAK_HOURS_MULTIPLIER = new BigDecimal("1.5");

    @Override
    public BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare) {
        BigDecimal fare = inputFare;
        if (isPeakHours(ticket.getEntryTime())) {
            fare = fare.multiply(PEAK_HOURS_MULTIPLIER);
        }
        return fare;
    }

    private boolean isPeakHours(LocalDateTime time) {
        int hour = time.getHour();
        return (hour >= 7 && hour <= 10) || (hour >= 16 && hour <= 19);
    }
}
```

### `FareCalculator.java`
```java
import java.math.BigDecimal;
import java.util.List;

public class FareCalculator {
    private final List<FareStrategy> fareStrategies;

    public FareCalculator(List<FareStrategy> fareStrategies) {
        this.fareStrategies = fareStrategies;
    }

    public BigDecimal calculateFare(Ticket ticket) {
        BigDecimal fare = BigDecimal.ZERO;
        for (FareStrategy strategy : fareStrategies) {
            fare = strategy.calculateFare(ticket, fare);
        }
        return fare;
    }
}
```

---

## 5. Parking Manager Subsystem (Thread-Safe Allocation)

### `ParkingManager.java`
```java
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingManager {
    private final Map<VehicleSize, List<ParkingSpot>> availableSpots;
    private final Map<Vehicle, ParkingSpot> vehicleToSpotMap;

    public ParkingManager(Map<VehicleSize, List<ParkingSpot>> availableSpots) {
        this.availableSpots = availableSpots;
        this.vehicleToSpotMap = new HashMap<>();
    }

    public synchronized ParkingSpot findSpotForVehicle(Vehicle vehicle) {
        VehicleSize vehicleSize = vehicle.getSize();

        for (VehicleSize size : VehicleSize.values()) {
            if (size.ordinal() >= vehicleSize.ordinal()) {
                List<ParkingSpot> spots = availableSpots.get(size);
                if (spots != null) {
                    for (ParkingSpot spot : spots) {
                        if (spot.isAvailable()) {
                            return spot;
                        }
                    }
                }
            }
        }
        return null;
    }

    public synchronized ParkingSpot parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findSpotForVehicle(vehicle);
        if (spot != null) {
            spot.occupy(vehicle);
            vehicleToSpotMap.put(vehicle, spot);
            availableSpots.get(spot.getSize()).remove(spot);
            return spot;
        }
        return null;
    }

    public synchronized void unparkVehicle(Vehicle vehicle) {
        ParkingSpot spot = vehicleToSpotMap.remove(vehicle);
        if (spot != null) {
            spot.vacate();
            availableSpots.get(spot.getSize()).add(spot);
        }
    }
}
```

---

## 6. Main System Facade & Test Execution

### `ParkingLot.java`
```java
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
        ParkingSpot spot = parkingManager.parkVehicle(vehicle);
        if (spot != null) {
            Ticket ticket = new Ticket(vehicle, spot);
            System.out.println("-> Vehicle [" + vehicle.getLicensePlate() + 
                               "] parked at Spot #" + spot.getSpotNumber() + 
                               " (" + spot.getSize() + ")");
            return ticket;
        } else {
            System.out.println("-> Parking FULL for vehicle size: " + vehicle.getSize());
            return null;
        }
    }

    public BigDecimal leaveVehicle(Ticket ticket) {
        if (ticket != null && ticket.getExitTime() == null) {
            ticket.setExitTime(LocalDateTime.now());
            parkingManager.unparkVehicle(ticket.getVehicle());
            BigDecimal fare = fareCalculator.calculateFare(ticket);
            
            System.out.println("<- Vehicle [" + ticket.getVehicle().getLicensePlate() + 
                               "] exited from Spot #" + ticket.getParkingSpot().getSpotNumber() + 
                               " | Total Fare: $" + fare);
            return fare;
        }
        return BigDecimal.ZERO;
    }
}
```

### `Main.java`
```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 1. Create Parking Spots
        List<ParkingSpot> compactSpots = new ArrayList<>(List.of(new CompactSpot(101)));
        List<ParkingSpot> regularSpots = new ArrayList<>(List.of(new RegularSpot(201)));
        List<ParkingSpot> oversizedSpots = new ArrayList<>(List.of(new OversizedSpot(301)));

        // 2. Map spots by size
        Map<VehicleSize, List<ParkingSpot>> availableSpots = new HashMap<>();
        availableSpots.put(VehicleSize.SMALL, compactSpots);
        availableSpots.put(VehicleSize.MEDIUM, regularSpots);
        availableSpots.put(VehicleSize.LARGE, oversizedSpots);

        // 3. Initialize Subsystems
        ParkingManager parkingManager = new ParkingManager(availableSpots);
        
        List<FareStrategy> fareStrategies = List.of(
            new BaseFareStrategy(),
            new PeakHoursFareStrategy()
        );
        FareCalculator fareCalculator = new FareCalculator(fareStrategies);

        // 4. Instantiate ParkingLot Facade
        ParkingLot parkingLot = new ParkingLot(parkingManager, fareCalculator);

        // 5. Test Vehicles
        Vehicle car = new Car("CAR-123");
        Vehicle bike = new Motorcycle("BIKE-999");
        Vehicle truck = new Truck("TRUCK-777");

        System.out.println("=== 1. PARKING VEHICLES ===");
        Ticket carTicket = parkingLot.parkVehicle(car);
        Ticket bikeTicket = parkingLot.parkVehicle(bike);
        Ticket truckTicket = parkingLot.parkVehicle(truck);

        // Best-Fit test: Park another car when regular spots are full
        Vehicle secondCar = new Car("CAR-456");
        Ticket secondCarTicket = parkingLot.parkVehicle(secondCar);

        System.out.println("\n=== 2. VEHICLE EXITS & FARE CALCULATION ===");
        if (carTicket != null) {
            parkingLot.leaveVehicle(carTicket);
        }
        if (bikeTicket != null) {
            parkingLot.leaveVehicle(bikeTicket);
        }
        if (secondCarTicket != null) {
            parkingLot.leaveVehicle(secondCarTicket);
        }
    }
}
```
