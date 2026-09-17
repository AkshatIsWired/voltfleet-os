package com.voltfleet.model;

/**
 * Commercial EV charging rate tiers and tariff structures.
 */
public enum ChargingTier {
    STANDARD_AC(7.4, 0.12, "Depot Overnight AC Slow Charger"),
    FAST_DC(50.0, 0.24, "Depot Fast DC Intermediate Charger"),
    ULTRA_RAPID_DC(150.0, 0.42, "High-Throughput Ultra-Rapid DC Charger");

    private final double powerOutputKw;
    private final double tariffPerKwhUsd;
    private final String tierDescription;

    ChargingTier(double powerOutputKw, double tariffPerKwhUsd, String tierDescription) {
        this.powerOutputKw = powerOutputKw;
        this.tariffPerKwhUsd = tariffPerKwhUsd;
        this.tierDescription = tierDescription;
    }

    public double getPowerOutputKw() {
        return powerOutputKw;
    }

    public double getTariffPerKwhUsd() {
        return tariffPerKwhUsd;
    }

    public String getTierDescription() {
        return tierDescription;
    }

    /**
     * Calculates the estimated duration in hours to transfer a given amount of energy.
     */
    public double estimateDurationHours(double energyRequiredKwh) {
        if (energyRequiredKwh <= 0) return 0.0;
        return energyRequiredKwh / this.powerOutputKw;
    }

    /**
     * Calculates the estimated electricity cost for the charging session.
     */
    public double calculateCost(double energyKwh) {
        return energyKwh * this.tariffPerKwhUsd;
    }
}
