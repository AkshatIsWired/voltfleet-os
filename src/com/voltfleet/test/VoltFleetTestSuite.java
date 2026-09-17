package com.voltfleet.test;

import com.voltfleet.exception.BatteryDepletionException;
import com.voltfleet.exception.GridOverloadException;
import com.voltfleet.exception.InvalidVINException;
import com.voltfleet.model.*;
import com.voltfleet.service.GridLoadBalancer;
import com.voltfleet.service.TelematicsSimulator;
import com.voltfleet.storage.FilePersistenceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Automated Verification & Unit Test Suite for VoltFleet OS.
 * Can be run directly from terminal without external test frameworks.
 */
public class VoltFleetTestSuite {
    private static int testsPassed = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("          RUNNING VOLTFLEET OS AUTOMATED UNIT TEST SUITE         ");
        System.out.println("=================================================================");

        runTest("testVinValidation", VoltFleetTestSuite::testVinValidation);
        runTest("testPolymorphicConsumption", VoltFleetTestSuite::testPolymorphicConsumption);
        runTest("testBatteryDepletionInterlock", VoltFleetTestSuite::testBatteryDepletionInterlock);
        runTest("testGridOverloadProtection", VoltFleetTestSuite::testGridOverloadProtection);
        runTest("testMultithreadedTelematics", VoltFleetTestSuite::testMultithreadedTelematics);
        runTest("testCsvPersistence", VoltFleetTestSuite::testCsvPersistence);

        System.out.println("=================================================================");
        System.out.printf("TEST RESULTS: %d / %d PASSED (Success Rate: %.1f%%)\n",
                testsPassed, totalTests, ((double) testsPassed / totalTests) * 100.0);
        System.out.println("=================================================================");
    }

    private static void runTest(String testName, TestCase test) {
        totalTests++;
        try {
            test.execute();
            System.out.printf("  [PASS] %-35s ... OK\n", testName);
            testsPassed++;
        } catch (Throwable t) {
            System.out.printf("  [FAIL] %-35s ... ERROR: %s\n", testName, t.getMessage());
            t.printStackTrace(System.out);
        }
    }

    @FunctionalInterface
    interface TestCase {
        void execute() throws Exception;
    }

    private static void testVinValidation() {
        // Valid VIN
        Vehicle v1 = new DeliveryVan("1V1EVVALID101", "Van", 80.0, 70.0, 10.0);
        assert v1.getVin().equals("1V1EVVALID101") : "VIN mismatch";

        // Invalid VIN with illegal char '@' or '#'
        boolean caught = false;
        try {
            new DeliveryVan("1V1EVINVALID#@", "Van", 80.0, 70.0, 10.0);
        } catch (InvalidVINException e) {
            caught = true;
        }
        assert caught : "Failed to throw InvalidVINException for illegal VIN format";
    }

    private static void testPolymorphicConsumption() {
        double dist = 100.0;
        double load = 500.0;

        DeliveryVan van = new DeliveryVan("1V1EVVAN001", "Van", 90.0, 80.0, 12.0);
        HeavyCargoTruck truck = new HeavyCargoTruck("1H1EVTRUCK01", "Truck", 300.0, 250.0, 10000.0);

        double vanEnergy = van.calculateRequiredEnergy(dist, load);
        double truckEnergy = truck.calculateRequiredEnergy(dist, load);

        assert truckEnergy > vanEnergy : "Heavy truck must consume more energy than light delivery van";
    }

    private static void testBatteryDepletionInterlock() throws Exception {
        DeliveryVan lowBatteryVan = new DeliveryVan("1V1LOWSOC01", "Van", 100.0, 16.0, 10.0); // 16% SoC (reserve is 15%)
        boolean caught = false;
        try {
            lowBatteryVan.dispatch("RT-IMPOSSIBLE", 150.0, 500.0);
        } catch (BatteryDepletionException e) {
            caught = true;
        }
        assert caught : "Safety interlock must prevent dispatching vehicle with inadequate energy";
    }

    private static void testGridOverloadProtection() throws Exception {
        GridLoadBalancer balancer = new GridLoadBalancer(160.0); // 160 kW ceiling
        List<ChargingBay> bays = new ArrayList<>();
        bays.add(new ChargingBay("BAY-1", ChargingTier.ULTRA_RAPID_DC)); // 150 kW
        bays.add(new ChargingBay("BAY-2", ChargingTier.FAST_DC));        // 50 kW

        DeliveryVan v1 = new DeliveryVan("1V1CARGO01", "Van", 100.0, 20.0, 10.0);
        DeliveryVan v2 = new DeliveryVan("1V1CARGO02", "Van", 100.0, 20.0, 10.0);

        // First allocation (150 kW <= 160 kW) -> OK
        boolean b1 = balancer.allocateChargingBay(bays.get(0), v1, bays);
        assert b1 : "First allocation must succeed";

        // Second allocation (150 + 50 = 200 kW > 160 kW) -> Must throw GridOverloadException
        boolean caught = false;
        try {
            balancer.allocateChargingBay(bays.get(1), v2, bays);
        } catch (GridOverloadException e) {
            caught = true;
        }
        assert caught : "GridLoadBalancer must prevent transformer overload";
    }

    private static void testMultithreadedTelematics() throws Exception {
        DeliveryVan van = new DeliveryVan("1V1STREAM01", "Van", 80.0, 60.0, 10.0);
        TelematicsSimulator sim = new TelematicsSimulator(van, 3, 50);
        Thread t = new Thread(sim);
        t.start();
        t.join(2000);

        assert !sim.isRunning() : "Thread must complete execution";
        assert sim.getEmittedRecords().size() == 3 : "Must emit exactly 3 telematics sensor pings";
    }

    private static void testCsvPersistence() throws IOException {
        File tempFile = File.createTempFile("voltfleet_test", ".csv");
        tempFile.deleteOnExit();

        List<Vehicle> initialList = new ArrayList<>();
        initialList.add(new DeliveryVan("1V1PERSIST01", "ModelVan", 80.0, 65.0, 12.0));
        initialList.add(new HeavyCargoTruck("1H1PERSIST02", "ModelSemi", 300.0, 280.0, 15000.0));

        FilePersistenceManager.saveFleetToCsv(initialList, tempFile);
        assert tempFile.exists() && tempFile.length() > 0 : "CSV file must be created and non-empty";

        List<Vehicle> loadedList = FilePersistenceManager.loadFleetFromCsv(tempFile);
        assert loadedList.size() == 2 : "Must correctly parse 2 vehicles back from CSV";
        assert loadedList.get(0).getVin().equals("1V1PERSIST01") : "VIN integrity check";
    }
}
