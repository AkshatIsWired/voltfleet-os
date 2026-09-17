package com.voltfleet.exception;

/**
 * Base checked exception for the VoltFleet OS platform.
 */
public class VoltFleetException extends Exception {
    private final long timestamp;

    public VoltFleetException(String message) {
        super(message);
        this.timestamp = System.currentTimeMillis();
    }

    public VoltFleetException(String message, Throwable cause) {
        super(message, cause);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
