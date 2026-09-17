package com.voltfleet.service;

import com.voltfleet.model.TelematicsRecord;
import com.voltfleet.model.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Background multi-threaded telematics simulation engine producing sensor streams
 * from en-route electric fleet vehicles.
 */
public class TelematicsSimulator implements Runnable {
    private final Vehicle vehicle;
    private final int totalPings;
    private final long intervalMs;
    private final List<TelematicsRecord> emittedRecords;
    private final Random random;
    private volatile boolean isRunning;

    public TelematicsSimulator(Vehicle vehicle, int totalPings, long intervalMs) {
        this.vehicle = vehicle;
        this.totalPings = totalPings;
        this.intervalMs = intervalMs;
        this.emittedRecords = Collections.synchronizedList(new ArrayList<>());
        this.random = new Random(vehicle.getVin().hashCode());
        this.isRunning = true;
    }

    @Override
    public void run() {
        double currentLat = 23.0768; // Base latitude
        double currentLon = 76.8513; // Base longitude

        for (int i = 1; i <= totalPings && isRunning; i++) {
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // Simulate realistic vehicle dynamics
            double speed = 35.0 + (random.nextDouble() * 45.0); // 35 - 80 km/h
            double batteryTemp = 28.0 + (random.nextDouble() * 12.0); // 28 - 40 °C
            currentLat += (random.nextDouble() - 0.5) * 0.005;
            currentLon += (random.nextDouble() - 0.5) * 0.005;

            String recordId = String.format("TLM-%s-%03d", vehicle.getVin().substring(0, Math.min(6, vehicle.getVin().length())), i);
            TelematicsRecord record = new TelematicsRecord(
                    recordId,
                    vehicle.getVin(),
                    speed,
                    vehicle.getStateOfCharge(),
                    batteryTemp,
                    currentLat,
                    currentLon
            );

            synchronized (emittedRecords) {
                emittedRecords.add(record);
            }
        }
        this.isRunning = false;
    }

    public void stopSimulation() {
        this.isRunning = false;
    }

    public List<TelematicsRecord> getEmittedRecords() {
        synchronized (emittedRecords) {
            return new ArrayList<>(emittedRecords);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
