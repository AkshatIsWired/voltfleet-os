package com.voltfleet.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable data transfer object representing a real-time sensor telematics ping.
 */
public final class TelematicsRecord {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String recordId;
    private final String vin;
    private final LocalDateTime timestamp;
    private final double speedKmh;
    private final double stateOfCharge;
    private final double batteryTempCelsius;
    private final double latitude;
    private final double longitude;

    public TelematicsRecord(String recordId, String vin, double speedKmh, double stateOfCharge,
                            double batteryTempCelsius, double latitude, double longitude) {
        this.recordId = recordId;
        this.vin = vin;
        this.timestamp = LocalDateTime.now();
        this.speedKmh = speedKmh;
        this.stateOfCharge = stateOfCharge;
        this.batteryTempCelsius = batteryTempCelsius;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toCsvLine() {
        return String.format("%s,%s,%s,%.1f,%.1f,%.1f,%.4f,%.4f",
                recordId, vin, timestamp.format(FORMATTER), speedKmh, stateOfCharge, batteryTempCelsius, latitude, longitude);
    }

    public String getRecordId() { return recordId; }
    public String getVin() { return vin; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getSpeedKmh() { return speedKmh; }
    public double getStateOfCharge() { return stateOfCharge; }
    public double getBatteryTempCelsius() { return batteryTempCelsius; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return String.format("[%s] VIN: %s | Speed: %5.1f km/h | SoC: %5.1f%% | Temp: %4.1f°C | Pos: (%.4f, %.4f)",
                timestamp.format(FORMATTER), vin, speedKmh, stateOfCharge, batteryTempCelsius, latitude, longitude);
    }
}
