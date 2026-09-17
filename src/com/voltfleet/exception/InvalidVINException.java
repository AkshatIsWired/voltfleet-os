package com.voltfleet.exception;

/**
 * Unchecked runtime exception thrown when a VIN fails formatting or checksum validation.
 */
public class InvalidVINException extends IllegalArgumentException {
    private final String invalidVin;

    public InvalidVINException(String invalidVin, String reason) {
        super(String.format("Invalid VIN [%s]: %s", invalidVin, reason));
        this.invalidVin = invalidVin;
    }

    public String getInvalidVin() { return invalidVin; }
}
