package com.voltfleet.interfaces;

/**
 * Contract for fleet assets requiring regulatory compliance, carbon tracking,
 * and maintenance audit records.
 */
public interface Auditable {
    String generateAuditSummary();
    double calculateCarbonOffsetKg();
}
