package com.voltfleet.service;

import com.voltfleet.exception.GridOverloadException;
import com.voltfleet.model.ChargingBay;
import com.voltfleet.model.Vehicle;

import java.util.List;

/**
 * Grid Load Balancer regulating electrical power distribution across depot bays
 * to prevent substation transformer overload and avoid peak-demand utility penalties.
 */
public class GridLoadBalancer {
    public static final double DEFAULT_MAX_DEPOT_GRID_KW = 250.0; // 250 kW substation limit

    private final double maxGridCapacityKw;
    // 2D Array: Rows = 5 Charging Bays, Columns = 4 hourly dispatch shift slots (Unit 4: 2D Arrays)
    private final double[][] bayPowerScheduleMatrix;

    public GridLoadBalancer() {
        this(DEFAULT_MAX_DEPOT_GRID_KW);
    }

    public GridLoadBalancer(double maxGridCapacityKw) {
        this.maxGridCapacityKw = maxGridCapacityKw;
        this.bayPowerScheduleMatrix = new double[5][4];
    }

    public synchronized double getCurrentActiveGridLoadKw(List<ChargingBay> bays) {
        double currentTotal = 0.0;
        for (ChargingBay bay : bays) {
            if (bay.isOccupied()) {
                currentTotal += bay.getTier().getPowerOutputKw();
            }
        }
        return currentTotal;
    }

    public synchronized boolean allocateChargingBay(ChargingBay bay, Vehicle vehicle, List<ChargingBay> allBays)
            throws GridOverloadException {
        if (bay.isOccupied()) {
            return false;
        }

        double currentLoad = getCurrentActiveGridLoadKw(allBays);
        double requestedLoad = bay.getTier().getPowerOutputKw();

        // Check if power allocation would exceed grid capacity
        if (currentLoad + requestedLoad > maxGridCapacityKw) {
            throw new GridOverloadException(currentLoad, requestedLoad, maxGridCapacityKw);
        }

        return bay.connectVehicle(vehicle);
    }

    public synchronized void recordScheduleSlot(int bayIndex, int timeSlot, double powerKw) {
        if (bayIndex >= 0 && bayIndex < bayPowerScheduleMatrix.length &&
            timeSlot >= 0 && timeSlot < bayPowerScheduleMatrix[0].length) {
            bayPowerScheduleMatrix[bayIndex][timeSlot] = powerKw;
        }
    }

    public double[][] getBayPowerScheduleMatrix() {
        return bayPowerScheduleMatrix;
    }

    public double getMaxGridCapacityKw() {
        return maxGridCapacityKw;
    }
}
