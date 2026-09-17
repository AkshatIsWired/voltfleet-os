package com.voltfleet.model;

/**
 * Lifecycle states of an electric commercial vehicle.
 */
public enum VehicleStatus {
    AVAILABLE("Vehicle is parked at depot and ready for dispatch"),
    EN_ROUTE("Vehicle is actively executing a delivery route"),
    QUEUED_CHARGING("Vehicle is waiting in depot queue for an open charging bay"),
    CHARGING("Vehicle is connected and actively drawing grid power"),
    MAINTENANCE("Vehicle is grounded for scheduled inspection or mechanical repair");

    private final String description;

    VehicleStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
