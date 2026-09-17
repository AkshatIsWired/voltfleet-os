package com.voltfleet.interfaces;

/**
 * Contract for battery-operated electric assets capable of receiving electrical energy.
 */
public interface EnergyChargeable {
    void charge(double kwhAdded);
    double getRemainingCapacityKwh();
    double getStateOfCharge();
}
