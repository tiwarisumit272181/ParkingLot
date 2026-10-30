
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Nested
class ParkingManagerTest {

    private ParkingManager parkingManager;

    @BeforeEach
    void setUp() {
        // Initialize fresh spots before each test run
        List<ParkingSpot> compactSpots = new ArrayList<>(List.of(new CompactSpot(101), new CompactSpot(102)));
        List<ParkingSpot> regularSpots = new ArrayList<>(List.of(new RegularSpot(201), new RegularSpot(202)));
        List<ParkingSpot> oversizedSpots = new ArrayList<>(List.of(new OversizedSpot(301)));

        Map<VehicleSize, List<ParkingSpot>> availableSpots = new HashMap<>();
        availableSpots.put(VehicleSize.SMALL, compactSpots);
        availableSpots.put(VehicleSize.MEDIUM, regularSpots);
        availableSpots.put(VehicleSize.LARGE, oversizedSpots);

        parkingManager = new ParkingManager(availableSpots);
    }

    @Test
    @DisplayName("Single vehicle should be allocated correct spot and unparked successfully")
    void testParkAndUnparkSingleVehicle() {
        Vehicle car = new Car("CAR-001");

        // Park vehicle
        ParkingSpot assignedSpot = parkingManager.parkVehicle(car);
        assertNotNull(assignedSpot, "Spot should be assigned");
        assertEquals(VehicleSize.MEDIUM, assignedSpot.getParkingSpotSize());
        assertFalse(assignedSpot.isAvailable(), "Spot should be marked as occupied");

        // Unpark vehicle
        parkingManager.unparkVehicle(car);
        assertTrue(assignedSpot.isAvailable(), "Spot should be available after unparking");
    }

    @Test
    @DisplayName("Best-Fit allocation: Medium vehicle falls back to Large spot when Medium spots are full")
    void testBestFitAllocation() {
        Vehicle car1 = new Car("CAR-001");
        Vehicle car2 = new Car("CAR-002");
        Vehicle car3 = new Car("CAR-003"); // Should fall back to Oversized Spot #301

        parkingManager.parkVehicle(car1); // Takes Spot #201
        parkingManager.parkVehicle(car2); // Takes Spot #202

        ParkingSpot spotForCar3 = parkingManager.parkVehicle(car3);
        assertNotNull(spotForCar3, "Car 3 should get a spot");
        assertEquals(VehicleSize.LARGE, spotForCar3.getParkingSpotSize(), "Should fall back to LARGE spot");
        assertEquals(301, spotForCar3.getSpotNumber());
    }

    @Test
    @DisplayName("Concurrent Parking Test: Synchronized methods prevent double-booking under race conditions")
    void testConcurrentParkingNoDoubleBooking() throws InterruptedException, ExecutionException {
        int numberOfThreads = 20; // 20 threads simultaneously competing for spots
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1); // Ensures all threads fire at the exact same instant

        List<Future<ParkingSpot>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final String plate = "CAR-" + i;
            Callable<ParkingSpot> task = () -> {
                startLatch.await(); // Hold execution until signal is given
                Vehicle vehicle = new Car(plate);
                return parkingManager.parkVehicle(vehicle);
            };
            futures.add(executorService.submit(task));
        }

        // Release all 20 threads simultaneously
        startLatch.countDown();

        Set<Integer> assignedSpotNumbers = Collections.synchronizedSet(new HashSet<>());
        int successfulParks = 0;
        int failedParks = 0;

        for (Future<ParkingSpot> future : futures) {
            ParkingSpot spot = future.get();
            if (spot != null) {
                successfulParks++;
                // Verify uniqueness of allocated spot numbers
                boolean isUnique = assignedSpotNumbers.add(spot.getSpotNumber());
                assertTrue(isUnique, "Race condition detected! Spot #" + spot.getSpotNumber() + " double-booked!");
            } else {
                failedParks++;
            }
        }

        executorService.shutdown();

        // Total spots fitting a Car (MEDIUM) = 2 Medium + 1 Large = 3 spots total
        assertEquals(3, successfulParks, "Exactly 3 cars should park before lot is full");
        assertEquals(17, failedParks, "17 vehicles should be rejected gracefully");
        assertEquals(3, assignedSpotNumbers.size(), "All 3 assigned spot numbers must be distinct");
    }

    @Test
    @DisplayName("Concurrent Park and Unpark Test: Ensures memory consistency during simultaneous entry/exit")
    void testConcurrentParkAndUnpark() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final String plate = "VEHICLE-" + i;
            final int index = i;
            executorService.submit(() -> {
                try {
                    Vehicle vehicle = (index % 2 == 0) ? new Car(plate) : new Bike(plate);
                    ParkingSpot spot = parkingManager.parkVehicle(vehicle);
                    if (spot != null) {
                        Thread.sleep(10); // Simulate parking duration
                        parkingManager.unparkVehicle(vehicle);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "All threads should complete within 5 seconds without deadlocks");
        executorService.shutdown();
    }
}
