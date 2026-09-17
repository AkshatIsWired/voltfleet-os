package com.voltfleet.storage;

import com.voltfleet.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File Persistence Manager demonstrating both character-oriented (BufferedReader/Writer)
 * and stream I/O operations for fleet inventory and telematics journals.
 */
public class FilePersistenceManager {

    /**
     * Exports current fleet inventory to a structured CSV file using BufferedWriter.
     */
    public static void saveFleetToCsv(List<Vehicle> vehicles, File destinationFile) throws IOException {
        if (destinationFile.getParentFile() != null) {
            destinationFile.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile, false))) {
            writer.write("VIN,Category,Model,BatteryCapacityKwh,CurrentEnergyKwh,OdometerKm,Status");
            writer.newLine();

            for (Vehicle v : vehicles) {
                String line = String.format("%s,%s,%s,%.2f,%.2f,%.2f,%s",
                        v.getVin(),
                        v.getVehicleCategory(),
                        v.getModelName(),
                        v.getBatteryCapacityKwh(),
                        v.getCurrentEnergyKwh(),
                        v.getOdometerKm(),
                        v.getStatus().name());
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Reads and parses fleet inventory from a CSV file using BufferedReader.
     */
    public static List<Vehicle> loadFleetFromCsv(File sourceFile) throws IOException {
        List<Vehicle> loadedVehicles = new ArrayList<>();
        if (!sourceFile.exists()) {
            return loadedVehicles;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String header = reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String vin = parts[0].trim();
                    String category = parts[1].trim();
                    String model = parts[2].trim();
                    double capacity = Double.parseDouble(parts[3].trim());
                    double currentEnergy = Double.parseDouble(parts[4].trim());

                    Vehicle vehicle;
                    if (category.contains("Van")) {
                        vehicle = new DeliveryVan(vin, model, capacity, currentEnergy, 12.5);
                    } else if (category.contains("Semi") || category.contains("Truck")) {
                        vehicle = new HeavyCargoTruck(vin, model, capacity, currentEnergy, 15000.0);
                    } else {
                        vehicle = new PassengerShuttle(vin, model, capacity, currentEnergy, 18);
                    }
                    loadedVehicles.add(vehicle);
                }
            }
        }
        return loadedVehicles;
    }

    /**
     * Appends telematics audit streams to a persistent log file using FileWriter.
     */
    public static void appendAuditLog(String logEntry, File logFile) throws IOException {
        if (logFile.getParentFile() != null) {
            logFile.getParentFile().mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(logEntry);
            writer.newLine();
        }
    }
}
