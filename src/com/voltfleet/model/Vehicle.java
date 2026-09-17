package com.voltfleet.model;

import com.voltfleet.exception.BatteryDepletionException;
import com.voltfleet.exception.InvalidVINException;
import com.voltfleet.exception.VehicleUnavailableException;
import com.voltfleet.interfaces.Auditable;
import com.voltfleet.interfaces.Dispatchable;
import com.voltfleet.interfaces.EnergyChargeable;

import java.util.regex.Pattern;

/**
 * Abstract foundational class representing any commercial electric vehicle
 * in the VoltFleet OS ecosystem.
 */
public abstract class Vehicle implements Dispatchable, EnergyChargeable, Auditable {
    private static int totalFleetCount = 0;
    private static final Pattern VIN_PATTERN = Pattern.compile("^[A-Z0-9]{8,17}$");
    public static final double MIN_SAFETY_RESERVE_PERCENT = 15.0; // 15% reserve

    private final String vin;
    private final String modelName;
    private final double batteryCapacityKwh;
    private double currentEnergyKwh;
    private double odometerKm;
    private VehicleStatus status;
    private String currentRouteId;
    private double totalEnergyConsumedKwh;

    public Vehicle(String vin, String modelName, double batteryCapacityKwh, double initialEnergyKwh) {
        if (!isValidVIN(vin)) {
            throw new InvalidVINException(vin, "Must be 8-17 alphanumeric characters excluding I, O, Q.");
        }
        if (batteryCapacityKwh <= 0) {
            throw new IllegalArgumentException("Battery capacity must be greater than 0 kWh.");
        }

        this.vin = vin;
        this.modelName = modelName;
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.currentEnergyKwh = Math.min(Math.max(initialEnergyKwh, 0), batteryCapacityKwh);
        this.odometerKm = 0.0;
        this.status = VehicleStatus.AVAILABLE;
        this.currentRouteId = null;
        this.totalEnergyConsumedKwh = 0.0;

        totalFleetCount++;
    }

    public static boolean isValidVIN(String vin) {
        return vin != null && VIN_PATTERN.matcher(vin).matches();
    }

    public static int getTotalFleetCount() {
        return totalFleetCount;
    }

    // Abstract method: Implemented differently by DeliveryVan, HeavyTruck, and PassengerShuttle
    public abstract double calculateRequiredEnergy(double distanceKm, double payloadKg);

    public abstract String getVehicleCategory();

    @Override
    public void dispatch(String routeId, double distanceKm, double payloadKg)
            throws BatteryDepletionException, VehicleUnavailableException {
        if (this.status != VehicleStatus.AVAILABLE) {
            throw new VehicleUnavailableException(this.vin, this.status.name());
        }

        double requiredKwh = calculateRequiredEnergy(distanceKm, payloadKg);
        double safeThresholdKwh = (MIN_SAFETY_RESERVE_PERCENT / 100.0) * this.batteryCapacityKwh;
        double usableKwh = this.currentEnergyKwh - safeThresholdKwh;

        if (requiredKwh > usableKwh) {
            throw new BatteryDepletionException(this.vin, getStateOfCharge(), requiredKwh, Math.max(0, usableKwh));
        }

        this.status = VehicleStatus.EN_ROUTE;
        this.currentRouteId = routeId;
    }

    @Override
    public void completeRoute(double actualDistanceKm, double actualEnergyUsedKwh) {
        this.currentEnergyKwh = Math.max(0.0, this.currentEnergyKwh - actualEnergyUsedKwh);
        this.odometerKm += actualDistanceKm;
        this.totalEnergyConsumedKwh += actualEnergyUsedKwh;
        this.currentRouteId = null;

        // Automatically flag for charging if below reserve
        if (getStateOfCharge() < 25.0) {
            this.status = VehicleStatus.QUEUED_CHARGING;
        } else {
            this.status = VehicleStatus.AVAILABLE;
        }
    }

    @Override
    public void charge(double kwhAdded) {
        if (kwhAdded < 0) {
            throw new IllegalArgumentException("Cannot add negative energy amount.");
        }
        this.currentEnergyKwh = Math.min(this.batteryCapacityKwh, this.currentEnergyKwh + kwhAdded);
        if (getStateOfCharge() >= 95.0 && this.status == VehicleStatus.CHARGING) {
            this.status = VehicleStatus.AVAILABLE;
        }
    }

    @Override
    public double getStateOfCharge() {
        return (this.currentEnergyKwh / this.batteryCapacityKwh) * 100.0;
    }

    @Override
    public double getRemainingCapacityKwh() {
        return this.batteryCapacityKwh - this.currentEnergyKwh;
    }

    @Override
    public double calculateCarbonOffsetKg() {
        // Commercial diesel vehicle average: 0.27 kg CO2 per km. Electric grid average: 0.08 kg CO2 per kWh.
        double dieselEquivalentEmissions = this.odometerKm * 0.27;
        double evGridEmissions = this.totalEnergyConsumedKwh * 0.08;
        return Math.max(0.0, dieselEquivalentEmissions - evGridEmissions);
    }

    @Override
    public String generateAuditSummary() {
        return String.format("VIN: %-12s | Category: %-16s | Model: %-14s | SoC: %5.1f%% | Odo: %7.1f km | Carbon Offset: %6.1f kg CO2 | Status: %s",
                vin, getVehicleCategory(), modelName, getStateOfCharge(), odometerKm, calculateCarbonOffsetKg(), status);
    }

    // Getters and Setters
    public String getVin() { return vin; }
    public String getModelName() { return modelName; }
    public double getBatteryCapacityKwh() { return batteryCapacityKwh; }
    public double getCurrentEnergyKwh() { return currentEnergyKwh; }
    public double getOdometerKm() { return odometerKm; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    public String getCurrentRouteId() { return currentRouteId; }
    public double getTotalEnergyConsumedKwh() { return totalEnergyConsumedKwh; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - SoC: %.1f%% (%s)",
                vin, modelName, getVehicleCategory(), getStateOfCharge(), status);
    }
}
