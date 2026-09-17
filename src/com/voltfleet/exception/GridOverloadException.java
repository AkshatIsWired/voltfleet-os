package com.voltfleet.exception;

/**
 * Thrown when attempting to activate a charging bay that would cause
 * the depot's total electrical power draw to exceed municipal grid transformer limits.
 */
public class GridOverloadException extends VoltFleetException {
    private final double currentLoadKw;
    private final double requestedLoadKw;
    private final double maxDepotCapacityKw;

    public GridOverloadException(double currentLoadKw, double requestedLoadKw, double maxDepotCapacityKw) {
        super(String.format("CRITICAL GRID OVERLOAD: Requested %.1f kW would push total load to %.1f kW, exceeding depot grid capacity of %.1f kW!",
                requestedLoadKw, (currentLoadKw + requestedLoadKw), maxDepotCapacityKw));
        this.currentLoadKw = currentLoadKw;
        this.requestedLoadKw = requestedLoadKw;
        this.maxDepotCapacityKw = maxDepotCapacityKw;
    }

    public double getCurrentLoadKw() { return currentLoadKw; }
    public double getRequestedLoadKw() { return requestedLoadKw; }
    public double getMaxDepotCapacityKw() { return maxDepotCapacityKw; }
}
