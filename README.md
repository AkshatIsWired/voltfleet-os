# VoltFleet OS: Autonomous Electric Fleet Energy and Telematics Engine

[![Java](https://img.shields.io/badge/Java-SE%208%2B%20%2F%2011%20%2F%2017%20%2F%2020%2B-blue.svg)](https://www.oracle.com/java/)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)]()
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey.svg)]()
[![CLI](https://img.shields.io/badge/Interface-Command--Line%20(CLI)-orange.svg)]()

> Course project: Programming in Java (CSE2006)  
> Institution: VIT Bhopal University  
> Author: Akshat Sharma (Reg. No: 24BEC10124)  
> Faculty Evaluator: Dr. Vipin Jain  


## Project Documentation & Academic Reports
The complete 15-page academic project report is available in multiple formats within the docs/ directory:
- [Evaluated Course Project Report (PDF)](docs/VoltFleet_OS_Project_Report.pdf)
- [Evaluated Course Project Report (Word DOCX)](docs/VoltFleet_OS_Project_Report.docx)
- [Standalone Interactive HTML Report](docs/VoltFleet_OS_Project_Report.html)

## 1. Project overview
VoltFleet OS is a command-line Java application for dispatching commercial electric vehicle (EV) fleets, managing depot power distribution, and recording telemetry. It models several practical operational constraints:
- Vehicle energy calculations: Estimates energy consumption using category-specific equations that account for cargo weight, regenerative braking, aerodynamic drag, and climate control loads.
- Substation overload prevention: Monitors concurrent power demand across charging bays and prevents activations that would exceed the depot transformer limit of 250 kW.
- Telematics streaming: Uses background worker threads to produce simulated sensor streams with GPS coordinates, velocity, battery temperature, and state of charge.
- Persistent records: Exports fleet inventory to CSV files and logs operational events using Java buffered I/O streams.

## 2. System architecture and class hierarchy

`	ext
com.voltfleet
├── exception
│   ├── VoltFleetException.java            (Base checked exception with timestamp)
│   ├── BatteryDepletionException.java     (Range safety violation exception)
│   ├── GridOverloadException.java         (Depot substation overload safeguard)
│   ├── VehicleUnavailableException.java   (Asset status lock exception)
│   └── InvalidVINException.java           (Unchecked VIN format validation exception)
├── interfaces
│   ├── Dispatchable.java                  (Route assignment contract)
│   ├── EnergyChargeable.java              (Battery charging interface)
│   └── Auditable.java                     (Regulatory and carbon offset audit contract)
├── model
│   ├── Vehicle.java                       (Abstract base class with dynamic dispatch)
│   ├── DeliveryVan.java                   (Subclass: Stop-and-go regenerative logic)
│   ├── HeavyCargoTruck.java               (Subclass: High-payload aerodynamic scaling)
│   ├── PassengerShuttle.java              (Subclass: Continuous HVAC thermal load)
│   ├── ChargingBay.java                   (Depot hardware terminal entity)
│   ├── ChargingTier.java                  (Enum: AC Slow, DC Fast, Ultra-Rapid with rates)
│   ├── VehicleStatus.java                 (Enum: Operational lifecycle states)
│   └── TelematicsRecord.java              (Immutable telemetry DTO)
├── service
│   ├── DepotManager.java                  (Singleton depot controller & PriorityQueue)
│   ├── GridLoadBalancer.java              (2D power schedule matrix & overload ceiling)
│   └── TelematicsSimulator.java           (Multithreaded background sensor worker)
├── storage
│   └── FilePersistenceManager.java        (Character & Stream I/O for CSV and logs)
├── cli
│   └── VoltFleetApp.java                  (Main interactive terminal CLI & demo mode)
└── test
    └── VoltFleetTestSuite.java            (Self-contained unit & boundary test runner)
`

## 3. Core features and technical implementation
- Abstract class Vehicle defines common properties and the abstract method calculateRequiredEnergy, overridden by DeliveryVan, HeavyCargoTruck, and PassengerShuttle to reflect category physics.
- Interfaces (Dispatchable, EnergyChargeable, Auditable) decouple operational actions, charging behavior, and reporting.
- Custom checked exceptions (BatteryDepletionException, GridOverloadException, VehicleUnavailableException) pass diagnostic telemetry when operating thresholds are exceeded.
- LinkedHashMap preserves vehicle insertion order while providing constant-time lookups by VIN. A PriorityQueue prioritizes depleted vehicles for charging based on lowest state of charge.
- A 2D array in GridLoadBalancer maps charging bay assignments across physical terminals and hourly work shifts.
- File storage uses BufferedReader and BufferedWriter with try-with-resources blocks for structured CSV inventory export and audit trails.
- Background threads implementing Runnable stream periodic sensor readings without blocking terminal user operations.
- The project runs on standard Java SE with zero third-party JAR dependencies.

## 4. Setup and compilation guide

### Prerequisites
- Java Development Kit (JDK) 8 or higher (Tested on OpenJDK and Oracle JDK 11, 17, 20, 21).
- A standard terminal environment (Command Prompt, PowerShell, Bash, or Zsh).

### Step 1: Clone the repository
`ash
git clone https://github.com/AkshatIsWired/voltfleet-os.git
cd voltfleet-os
`

### Step 2: Compile the Java source files
On Windows (Command Prompt / PowerShell):
`cmd
build.bat
`
Or manually:
`cmd
mkdir bin
javac -encoding UTF-8 -d bin src\com\voltfleet\exception\*.java src\com\voltfleet\interfaces\*.java src\com\voltfleet\model\*.java src\com\voltfleet\service\*.java src\com\voltfleet\storage\*.java src\com\voltfleet\cli\*.java src\com\voltfleet\test\*.java
`

On Linux or macOS:
`ash
chmod +x run.sh
mkdir -p bin
javac -encoding UTF-8 -d bin 
`

## 5. Running the application

### Interactive terminal interface (Default)
Launch the interactive command-line console:
`cmd
run.bat
`
Or manually:
`ash
java -cp bin com.voltfleet.cli.VoltFleetApp
`

### Automated demo mode
Run the non-interactive test sequence:
`ash
java -cp bin com.voltfleet.cli.VoltFleetApp --demo
`

## 6. Running unit tests
Run the built-in test suite, which verifies VIN validation, energy equations, battery depletion interlocks, grid limits, multithreading, and CSV persistence:

`ash
java -ea -cp bin com.voltfleet.test.VoltFleetTestSuite
`
Or using the Windows launcher:
`cmd
run.bat --test
`

### Test output
`console
=================================================================
          RUNNING VOLTFLEET OS AUTOMATED UNIT TEST SUITE         
=================================================================
  [PASS] testVinValidation                   ... OK
  [PASS] testPolymorphicConsumption          ... OK
  [PASS] testBatteryDepletionInterlock       ... OK
  [PASS] testGridOverloadProtection          ... OK
  [PASS] testMultithreadedTelematics         ... OK
  [PASS] testCsvPersistence                  ... OK
=================================================================
TEST RESULTS: 6 / 6 PASSED (Success Rate: 100.0%)
=================================================================
`

## 7. Terminal execution output
The automated demo run produces this output trace:

`console
=========================== FLEET AUDIT SUMMARY ===========================
VIN: 1V1EVLASTMILE01 | Category: Last-Mile Van    | Model: Transit-Volt 350 | SoC:  91.8% | Odo:     0.0 km | Carbon Offset:    0.0 kg CO2 | Status: AVAILABLE
VIN: 1V1EVLASTMILE02 | Category: Last-Mile Van    | Model: Transit-Volt 350 | SoC:  21.8% | Odo:     0.0 km | Carbon Offset:    0.0 kg CO2 | Status: AVAILABLE
VIN: 1H1EVFREIGHT901 | Category: Heavy Cargo Semi | Model: VoltHauler Semi | SoC:  88.6% | Odo:     0.0 km | Carbon Offset:    0.0 kg CO2 | Status: AVAILABLE
VIN: 1P1EVSHUTTLE501 | Category: Transit Shuttle  | Model: MetroE-Shuttle 20 | SoC:  86.4% | Odo:     0.0 km | Carbon Offset:    0.0 kg CO2 | Status: AVAILABLE
---------------------------------------------------------------------------
Total Fleet Vehicles : 4
Depot Fleet Energy   : 501.50 / 630.00 kWh (Avg SoC: 79.6%)
Total Clean CO2 Saved: 0.00 kg CO2
===========================================================================

--- [TEST 2] ROUTE DISPATCH (NORMAL BOUNDARY CHECK) ---
Dispatching [1V1EVLASTMILE01] on Route RT-URBAN-12 (Distance: 45 km, Payload: 350 kg)...
Result: DISPATCH APPROVED. Vehicle status changed to EN_ROUTE.

--- [TEST 3] DISPATCH INTERVENTION: BATTERY DEPLETION EXCEPTION ---
Attempting to dispatch Low-Battery Vehicle [1V1EVLASTMILE02] on 120 km High-Payload Route...
SUCCESS: BatteryDepletionException caught and handled gracefully!
  -> Diagnostic Message: Battery Depletion Hazard for VIN [1V1EVLASTMILE02]: Required 25.03 kWh, but only 5.75 kWh available (Current SoC: 21.8%). Cannot safely dispatch.
  -> Enqueueing depleted vehicle into smart charging queue...

--- [TEST 4] GRID POWER ALLOCATION & OVERLOAD CEILING ---
======================== DEPOT CHARGING TERMINALS ========================
Bay [BAY-AC-01] | STANDARD_AC      |   7.4 kW | Status: FREE
Bay [BAY-AC-02] | STANDARD_AC      |   7.4 kW | Status: FREE
Bay [BAY-DC-01] | FAST_DC          |  50.0 kW | Status: FREE
Bay [BAY-DC-02] | FAST_DC          |  50.0 kW | Status: FREE
Bay [BAY-UR-01] | ULTRA_RAPID_DC   | 150.0 kW | Status: FREE
---------------------------------------------------------------------------
Depot Electrical Grid Load: 0.0 kW / 250.0 kW (Utilization: 0.0%)
===========================================================================
Connecting Heavy Truck to Ultra-Rapid 150 kW Bay...
Allocated BAY-UR-01. New load: 150.0 kW.
Connecting Passenger Shuttle to Fast DC 50 kW Bay...
Allocated BAY-DC-01. New load: 200.0 kW.
Attempting to connect Delivery Van to another 50 kW bay (Testing load limit)...
Allocated BAY-DC-02. Total load: 250.0 kW.

--- [TEST 5] MULTITHREADED TELEMATICS LIVE SENSOR INGESTION ---
  [SENSOR STREAM] [2026-09-17 14:19:15] VIN: 1V1EVLASTMILE01 | Speed:  67.8 km/h | SoC:  91.8% | Temp: 29.7°C | Pos: (23.0787, 76.8507)
  [SENSOR STREAM] [2026-09-17 14:19:15] VIN: 1V1EVLASTMILE01 | Speed:  57.9 km/h | SoC:  91.8% | Temp: 29.1°C | Pos: (23.0786, 76.8530)
  [SENSOR STREAM] [2026-09-17 14:19:15] VIN: 1V1EVLASTMILE01 | Speed:  47.7 km/h | SoC:  91.8% | Temp: 30.5°C | Pos: (23.0764, 76.8527)
  [SENSOR STREAM] [2026-09-17 14:19:15] VIN: 1V1EVLASTMILE01 | Speed:  61.8 km/h | SoC:  91.8% | Temp: 28.6°C | Pos: (23.0776, 76.8512)

--- [TEST 6] PERSISTENCE ENGINE: CSV EXPORT & AUDIT JOURNAL ---
SUCCESS: Fleet inventory exported to data/fleet_inventory.csv (4 records written).
Checking data directory: data/fleet_inventory.csv (Exists: true)
`

## 8. Academic course alignment
- Unit 1 (Java introduction and flow control): Console menus, data types, and loop structures in VoltFleetApp.java.
- Unit 2 (Java object-oriented programming): Inheritance hierarchies, dynamic dispatch, and encapsulation in Vehicle.java and its subclasses.
- Unit 3 (Abstract classes and interfaces): Abstract base methods and interface implementations in Dispatchable, EnergyChargeable, and Auditable.
- Unit 4 (Exception handling and multithreading): Custom exception hierarchy under VoltFleetException and background telemetry threads in TelematicsSimulator.java.
- Unit 5 (Collections, arrays, and file I/O): 2D array power grid scheduling in GridLoadBalancer.java, LinkedHashMap and PriorityQueue in DepotManager.java, and CSV buffered I/O in FilePersistenceManager.java.
