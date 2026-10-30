public class Bike implements Vehicle {
    // once vehicle is created i do not want to change the identity
    private  final String licensePlate;
    public Bike(String licensePlate){
        this.licensePlate=licensePlate;
    }
    @Override
    public String getLicensePlate() {
        return licensePlate ;
    }

    @Override
    public VehicleSize getSize() {
        return VehicleSize.SMALL;
    }
}
