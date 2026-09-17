package com.voltfleet.interfaces;

import com.voltfleet.exception.BatteryDepletionException;
import com.voltfleet.exception.VehicleUnavailableException;

/**
 * Contract for any commercial fleet vehicle capable of receiving route dispatch orders.
 */
public interface Dispatchable {
    void dispatch(String routeId, double distanceKm, double payloadKg)
            throws BatteryDepletionException, VehicleUnavailableException;

    void completeRoute(double actualDistanceKm, double actualEnergyUsedKwh);
}
