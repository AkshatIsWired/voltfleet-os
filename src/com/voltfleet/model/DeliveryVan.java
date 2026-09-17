package com.voltfleet.model;

/**
 * Commercial Urban Electric Delivery Van optimized for last-mile logistics
 * with high-efficiency regenerative stop-and-go braking.
 */
public class DeliveryVan extends Vehicle {
    private static final double BASE_CONSUMPTION_KWH_PER_KM = 0.21;
    private static final double PAYLOAD_FACTOR = 0.000045; // kWh per km per kg
    private static final double REGENERATIVE_BRAKING_DISCOUNT = 0.88; // 12% energy recapture in urban cycles

    private final double maxCargoVolumeCubicMeters;

    public DeliveryVan(String vin, String modelName, double batteryCapacityKwh, double initialEnergyKwh, double maxCargoVolumeCubicMeters) {
        super(vin, modelName, batteryCapacityKwh, initialEnergyKwh);
        this.maxCargoVolumeCubicMeters = maxCargoVolumeCubicMeters;
    }

    @Override
    public double calculateRequiredEnergy(double distanceKm, double payloadKg) {
        double rawRate = BASE_CONSUMPTION_KWH_PER_KM + (payloadKg * PAYLOAD_FACTOR);
        double netRate = rawRate * REGENERATIVE_BRAKING_DISCOUNT;
        return distanceKm * netRate;
    }

    @Override
    public String getVehicleCategory() {
        return "Last-Mile Van";
    }

    public double getMaxCargoVolumeCubicMeters() {
        return maxCargoVolumeCubicMeters;
    }
}
