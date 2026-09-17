package com.voltfleet.exception;

/**
 * Thrown when trying to dispatch a vehicle that is currently charging,
 * undergoing maintenance, or already on another active route.
 */
public class VehicleUnavailableException extends VoltFleetException {
    private final String vin;
    private final String currentStatus;

    public VehicleUnavailableException(String vin, String currentStatus) {
        super(String.format("Vehicle [%s] is currently unavailable for dispatch. Current Status: %s", vin, currentStatus));
        this.vin = vin;
        this.currentStatus = currentStatus;
    }

    public String getVin() { return vin; }
    public String getCurrentStatus() { return currentStatus; }
}
