package com.voltfleet.service;

import com.voltfleet.exception.BatteryDepletionException;
import com.voltfleet.exception.VehicleUnavailableException;
import com.voltfleet.model.ChargingBay;
import com.voltfleet.model.ChargingTier;
import com.voltfleet.model.Vehicle;
import com.voltfleet.model.VehicleStatus;

import java.util.*;

/**
 * Singleton Depot Management Service coordinating fleet inventory,
 * dispatch operations, and charging queues.
 */
public class DepotManager {
    private static volatile DepotManager instance;

    private final Map<String, Vehicle> fleetMap;
    private final List<ChargingBay> bays;
    private final PriorityQueue<Vehicle> chargePriorityQueue;
    private final Stack<String> auditTrailStack;

    private DepotManager() {
        this.fleetMap = new LinkedHashMap<>();
        this.bays = new ArrayList<>();
        // PriorityQueue ordered by lowest State of Charge first
        this.chargePriorityQueue = new PriorityQueue<>(Comparator.comparingDouble(Vehicle::getStateOfCharge));
        this.auditTrailStack = new Stack<>();

        initializeDefaultBays();
    }

    public static DepotManager getInstance() {
        if (instance == null) {
            synchronized (DepotManager.class) {
                if (instance == null) {
                    instance = new DepotManager();
                }
            }
        }
        return instance;
    }

    private void initializeDefaultBays() {
        bays.add(new ChargingBay("BAY-AC-01", ChargingTier.STANDARD_AC));
        bays.add(new ChargingBay("BAY-AC-02", ChargingTier.STANDARD_AC));
        bays.add(new ChargingBay("BAY-DC-01", ChargingTier.FAST_DC));
        bays.add(new ChargingBay("BAY-DC-02", ChargingTier.FAST_DC));
        bays.add(new ChargingBay("BAY-UR-01", ChargingTier.ULTRA_RAPID_DC));
    }

    public synchronized void registerVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Cannot register null vehicle.");
        }
        fleetMap.put(vehicle.getVin(), vehicle);
        logAuditAction("REGISTER", "Enrolled " + vehicle.getVehicleCategory() + " [VIN: " + vehicle.getVin() + "]");
    }

    public Vehicle getVehicle(String vin) {
        return fleetMap.get(vin);
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(fleetMap.values());
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> available = new ArrayList<>();
        for (Vehicle v : fleetMap.values()) {
            if (v.getStatus() == VehicleStatus.AVAILABLE) {
                available.add(v);
            }
        }
        return available;
    }

    public synchronized void dispatchVehicle(String vin, String routeId, double distanceKm, double payloadKg)
            throws BatteryDepletionException, VehicleUnavailableException {
        Vehicle v = fleetMap.get(vin);
        if (v == null) {
            throw new IllegalArgumentException("Vehicle with VIN [" + vin + "] not found in depot registry.");
        }

        v.dispatch(routeId, distanceKm, payloadKg);
        logAuditAction("DISPATCH", String.format("Dispatched [%s] on Route %s (%.1f km, %.1f kg)", vin, routeId, distanceKm, payloadKg));
    }

    public synchronized void returnVehicleFromRoute(String vin, double actualKm, double actualEnergyKwh) {
        Vehicle v = fleetMap.get(vin);
        if (v != null && v.getStatus() == VehicleStatus.EN_ROUTE) {
            v.completeRoute(actualKm, actualEnergyKwh);
            logAuditAction("RETURN", String.format("[%s] Completed route. Odo: +%.1f km, Consumed: %.1f kWh, SoC: %.1f%%",
                    vin, actualKm, actualEnergyKwh, v.getStateOfCharge()));

            if (v.getStatus() == VehicleStatus.QUEUED_CHARGING) {
                enqueueForCharging(v);
            }
        }
    }

    public synchronized void enqueueForCharging(Vehicle vehicle) {
        if (!chargePriorityQueue.contains(vehicle)) {
            chargePriorityQueue.add(vehicle);
            vehicle.setStatus(VehicleStatus.QUEUED_CHARGING);
            logAuditAction("QUEUE_CHARGE", String.format("[%s] Added to charging queue with SoC %.1f%%",
                    vehicle.getVin(), vehicle.getStateOfCharge()));
        }
    }

    public List<ChargingBay> getChargingBays() {
        return Collections.unmodifiableList(bays);
    }

    public PriorityQueue<Vehicle> getChargePriorityQueue() {
        return chargePriorityQueue;
    }

    public void logAuditAction(String actionType, String details) {
        String logEntry = String.format("[%tF %<tT] %-12s : %s", System.currentTimeMillis(), actionType, details);
        auditTrailStack.push(logEntry);
    }

    public List<String> getRecentAuditLogs(int count) {
        List<String> logs = new ArrayList<>();
        int limit = Math.min(count, auditTrailStack.size());
        for (int i = auditTrailStack.size() - 1; i >= auditTrailStack.size() - limit; i--) {
            logs.add(auditTrailStack.get(i));
        }
        return logs;
    }
}
