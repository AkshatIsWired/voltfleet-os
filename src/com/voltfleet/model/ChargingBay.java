package com.voltfleet.model;

/**
 * Represents an individual physical charging terminal bay in the fleet depot.
 */
public class ChargingBay {
    private final String bayId;
    private final ChargingTier tier;
    private Vehicle currentVehicle;
    private boolean isOccupied;
    private double totalEnergyDispensedKwh;

    public ChargingBay(String bayId, ChargingTier tier) {
        this.bayId = bayId;
        this.tier = tier;
        this.currentVehicle = null;
        this.isOccupied = false;
        this.totalEnergyDispensedKwh = 0.0;
    }

    public synchronized boolean connectVehicle(Vehicle vehicle) {
        if (this.isOccupied || vehicle == null) {
            return false;
        }
        this.currentVehicle = vehicle;
        this.isOccupied = true;
        vehicle.setStatus(VehicleStatus.CHARGING);
        return true;
    }

    public synchronized Vehicle disconnectVehicle() {
        if (!this.isOccupied) {
            return null;
        }
        Vehicle v = this.currentVehicle;
        this.currentVehicle = null;
        this.isOccupied = false;
        if (v.getStatus() == VehicleStatus.CHARGING) {
            v.setStatus(VehicleStatus.AVAILABLE);
        }
        return v;
    }

    public synchronized double deliverCharge(double kwh) {
        if (!this.isOccupied || this.currentVehicle == null) {
            return 0.0;
        }
        double needed = this.currentVehicle.getRemainingCapacityKwh();
        double actualDelivered = Math.min(needed, kwh);
        this.currentVehicle.charge(actualDelivered);
        this.totalEnergyDispensedKwh += actualDelivered;
        return actualDelivered;
    }

    public String getBayId() { return bayId; }
    public ChargingTier getTier() { return tier; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }
    public boolean isOccupied() { return isOccupied; }
    public double getTotalEnergyDispensedKwh() { return totalEnergyDispensedKwh; }

    @Override
    public String toString() {
        return String.format("Bay [%s] | %-16s | %5.1f kW | Status: %s",
                bayId, tier.name(), tier.getPowerOutputKw(), (isOccupied ? "OCCUPIED (" + currentVehicle.getVin() + ")" : "FREE"));
    }
}
