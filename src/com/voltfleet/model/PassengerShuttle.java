package com.voltfleet.model;

/**
 * All-Electric Corporate & Transit Passenger Shuttle with active HVAC climate overhead.
 */
public class PassengerShuttle extends Vehicle {
    private static final double BASE_CONSUMPTION_KWH_PER_KM = 0.32;
    private static final double PASSENGER_WEIGHT_AVG_KG = 75.0;
    private static final double HVAC_OVERHEAD_RATE = 0.08; // 8% continuous thermal HVAC load

    private final int seatingCapacity;

    public PassengerShuttle(String vin, String modelName, double batteryCapacityKwh, double initialEnergyKwh, int seatingCapacity) {
        super(vin, modelName, batteryCapacityKwh, initialEnergyKwh);
        this.seatingCapacity = seatingCapacity;
    }

    @Override
    public double calculateRequiredEnergy(double distanceKm, double passengerCount) {
        double validPassengers = Math.min(passengerCount, seatingCapacity);
        double totalPassengerPayload = validPassengers * PASSENGER_WEIGHT_AVG_KG;
        double baseRate = BASE_CONSUMPTION_KWH_PER_KM + (totalPassengerPayload * 0.000035);
        double rateWithHVAC = baseRate * (1.0 + HVAC_OVERHEAD_RATE);
        return distanceKm * rateWithHVAC;
    }

    @Override
    public String getVehicleCategory() {
        return "Transit Shuttle";
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }
}
