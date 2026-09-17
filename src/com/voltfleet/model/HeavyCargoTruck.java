package com.voltfleet.model;

/**
 * Heavy-Duty Class 8 Electric Semi-Truck for inter-depot linehaul freight.
 */
public class HeavyCargoTruck extends Vehicle {
    private static final double BASE_CONSUMPTION_KWH_PER_KM = 0.85;
    private static final double HEAVY_PAYLOAD_FACTOR = 0.000085; // kWh per km per kg
    private static final double HIGHWAY_AERODYNAMIC_DRAG = 1.15; // 15% aerodynamic highway penalty

    private final double maxPayloadCapacityKg;

    public HeavyCargoTruck(String vin, String modelName, double batteryCapacityKwh, double initialEnergyKwh, double maxPayloadCapacityKg) {
        super(vin, modelName, batteryCapacityKwh, initialEnergyKwh);
        this.maxPayloadCapacityKg = maxPayloadCapacityKg;
    }

    @Override
    public double calculateRequiredEnergy(double distanceKm, double payloadKg) {
        double effectivePayload = Math.min(payloadKg, maxPayloadCapacityKg);
        double rate = (BASE_CONSUMPTION_KWH_PER_KM + (effectivePayload * HEAVY_PAYLOAD_FACTOR)) * HIGHWAY_AERODYNAMIC_DRAG;
        return distanceKm * rate;
    }

    @Override
    public String getVehicleCategory() {
        return "Heavy Cargo Semi";
    }

    public double getMaxPayloadCapacityKg() {
        return maxPayloadCapacityKg;
    }
}
