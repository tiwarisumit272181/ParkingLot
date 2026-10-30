public class Car implements Vehicle {
   private  final String licensePlate;
   public Car(String licensePlate){
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
