package com.voltfleet.exception;

/**
 * Thrown when a projected dispatch route would deplete vehicle battery
 * below the mandatory regulatory reserve threshold (e.g., 15%).
 */
public class BatteryDepletionException extends VoltFleetException {
    private final String vin;
    private final double currentSoC;
    private final double projectedEnergyRequired;
    private final double availableEnergy;

    public BatteryDepletionException(String vin, double currentSoC, double projectedEnergyRequired, double availableEnergy) {
        super(String.format("Battery Depletion Hazard for VIN [%s]: Required %.2f kWh, but only %.2f kWh available (Current SoC: %.1f%%). Cannot safely dispatch.",
                vin, projectedEnergyRequired, availableEnergy, currentSoC));
        this.vin = vin;
        this.currentSoC = currentSoC;
        this.projectedEnergyRequired = projectedEnergyRequired;
        this.availableEnergy = availableEnergy;
    }

    public String getVin() { return vin; }
    public double getCurrentSoC() { return currentSoC; }
    public double getProjectedEnergyRequired() { return projectedEnergyRequired; }
    public double getAvailableEnergy() { return availableEnergy; }
}
