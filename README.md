<div align="center">

<img src="docs/assets/voltfleet_logo_transparent.png" alt="VoltFleet OS Logo" width="160" />

# VoltFleet OS

### Autonomous Electric Vehicle Fleet Energy, Range Safety, and Grid Load Balancing Engine

[![Java](https://img.shields.io/badge/Java-SE%208%20%7C%2011%20%7C%2017%20%7C%2021-007396?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Build Status](https://img.shields.io/badge/Build-Passing-10b981.svg)]()
[![Tests](https://img.shields.io/badge/Tests-6%20%2F%206%20Passed%20(100%25)-success.svg)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Course](https://img.shields.io/badge/Course-CSE2006%20(Programming%20in%20Java)-8b5cf6.svg)]()
[![Institution](https://img.shields.io/badge/Institution-VIT%20Bhopal%20University-1e3a8a.svg)](https://vitbhopal.ac.in)

<p align="center">
  <a href="#1-project-overview">Overview</a> •
  <a href="#2-academic-metadata--reports">Project Reports</a> •
  <a href="#3-system-architecture">Architecture</a> •
  <a href="#4-domain-models--vehicle-physics">Domain Models</a> •
  <a href="#5-substation-grid-balancing">Grid Balancing</a> •
  <a href="#6-syllabus-mapping">Syllabus Mapping</a> •
  <a href="#7-compilation--execution">Quickstart</a> •
  <a href="#8-unit-tests--verification">Test Suite</a>
</p>

</div>

---

## 1. Project Overview

VoltFleet OS is a standalone Java core engine designed for commercial electric vehicle (EV) fleet dispatching, depot substation load management, and real-time telemetry streaming.

Commercial EV fleet operations face four distinct operational constraints:
- Vehicle energy consumption varies dynamically with cargo payload, regenerative braking recapture, aerodynamic drag, and climate control draw, making static range estimates inaccurate.
- Charging multiple vehicles concurrently can overload depot substation transformers, requiring active power throttling.
- High-frequency telematics streams (GPS, temperature, velocity) must be processed asynchronously to keep dispatcher interfaces responsive.
- Regulatory carbon offset auditing requires verified disk persistence of energy metrics and fleet logs.

VoltFleet OS models and solves each of these constraints in standard Java SE with zero external dependencies.

---

## 2. Academic Metadata & Reports

| Specification | Project Details |
| :--- | :--- |
| **Course Code & Title** | CSE2006: Programming in Java |
| **Academic Component** | Evaluated Course Project (Flipped Course Submission) |
| **Institution** | Vellore Institute of Technology (VIT), Bhopal University |
| **Student Author** | Akshat Sharma |
| **Registration Number** | `24BEC10124` |
| **College Email** | [akshat.24bec10124@vitbhopal.ac.in](mailto:akshat.24bec10124@vitbhopal.ac.in) |
| **GitHub Email** | [akshatsharma231007@gmail.com](mailto:akshatsharma231007@gmail.com) |
| **Academic Session** | Fall Semester 2026 to 2027 |
| **Official Repository** | [github.com/AkshatIsWired/voltfleet-os](https://github.com/AkshatIsWired/voltfleet-os) |

### Project Documentation
The complete formal academic project report is available inside the `docs/` folder:
- [Evaluated Course Project Report (PDF)](docs/VoltFleet_OS_Project_Report.pdf): Complete 15-page academic project report containing system architecture, domain models, mathematical formulations, concrete Java source listings, terminal logs, and boundary value testing tables.
- [Project Report Source (Word Document)](docs/VoltFleet_OS_Project_Report.docx): Formatted Word submission document.

---

## 3. System Architecture

VoltFleet OS is structured into seven decoupled packages under `com.voltfleet`:

```text
com.voltfleet
├── exception
│   ├── VoltFleetException.java            (Base checked exception with ISO timestamp)
│   ├── BatteryDepletionException.java     (Range safety interlock violation)
│   ├── GridOverloadException.java         (250 kW substation transformer overload defense)
│   ├── VehicleUnavailableException.java   (Asset operational status lock)
│   └── InvalidVINException.java           (Unchecked 17-character VIN syntax exception)
├── interfaces
│   ├── Dispatchable.java                  (Route feasibility and dispatch contract)
│   ├── EnergyChargeable.java              (Depot bay connection and charging interface)
│   └── Auditable.java                     (Carbon offset and lifecycle auditing contract)
├── model
│   ├── Vehicle.java                       (Abstract base class with dynamic dispatch)
│   ├── DeliveryVan.java                   (Urban stop-and-go with 0.88x regen recapture)
│   ├── HeavyCargoTruck.java               (Freight logistics with 1.15x drag penalty)
│   ├── PassengerShuttle.java              (Continuous HVAC passenger climate load)
│   ├── ChargingBay.java                   (Depot charging terminal entity)
│   ├── ChargingTier.java                  (Enum: STANDARD_AC, FAST_DC, ULTRA_RAPID_DC)
│   ├── VehicleStatus.java                 (Enum: AVAILABLE, EN_ROUTE, CHARGING, MAINTENANCE)
│   └── TelematicsRecord.java              (Immutable telemetry sensor DTO)
├── service
│   ├── DepotManager.java                  (Singleton controller with LinkedHashMap & PriorityQueue)
│   ├── GridLoadBalancer.java              (2D power schedule matrix & synchronized allocation)
│   └── TelematicsSimulator.java           (Multithreaded background sensor streamer)
├── storage
│   ├── FilePersistenceManager.java        (Character & Stream buffered I/O persistence)
├── cli
│   └── VoltFleetApp.java                  (Interactive terminal console & automated demo runner)
└── test
    └── VoltFleetTestSuite.java            (Self-contained unit & boundary verification suite)
```

---

## 4. Domain Models & Vehicle Physics

Each vehicle subclass extends `Vehicle` and overrides the polymorphic energy model:

$$\text{Energy}_{\text{Required}} (\text{kWh}) = \text{Distance} \times [\;\text{BaseRate} + (\text{Payload} \times C_p)\;] \times K_{\text{env}}$$

| Vehicle Type | Class Name | Battery Capacity | Base Consumption | Payload Penalty ($C_p$) | Environmental Factor ($K_{\text{env}}$) |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **Last-Mile Delivery Van** | `DeliveryVan` | 75.0 kWh | 0.21 kWh/km | `+0.00008 kWh/km/kg` | **0.88x** (Urban regenerative braking recapture) |
| **Heavy Cargo Semi-Truck** | `HeavyCargoTruck` | 300.0 kWh | 0.85 kWh/km | `+0.00005 kWh/km/kg` | **1.15x** (Aerodynamic highway wind drag) |
| **Transit Passenger Shuttle** | `PassengerShuttle` | 90.0 kWh | 0.32 kWh/km | `+0.00006 kWh/km/kg` | **1.08x** (Continuous passenger cabin HVAC load) |

### Safety Interlock Rules
Every vehicle must retain at least 15% of its total battery pack capacity upon route completion. If $\text{Energy}_{\text{Required}} > \text{CurrentEnergy} - (0.15 \times \text{Capacity})$, dispatch is rejected and a `BatteryDepletionException` is thrown. Rejected vehicles are automatically placed into a `PriorityQueue<Vehicle>` ordered by lowest State of Charge (SoC).

---

## 5. Substation Grid Balancing

The depot electrical infrastructure is safeguarded by `GridLoadBalancer`, which enforces a strict transformer ceiling:

$$\sum_{i=1}^{N} \text{PowerDraw}_i \le 250.0\text{ kW}$$

| Bay Identifier | Charging Tier | Nominal Power | Primary Target Vehicles |
| :--- | :--- | :---: | :--- |
| `BAY-AC-01` | `STANDARD_AC` | 7.4 kW | Overnight trickle charge for vans |
| `BAY-AC-02` | `STANDARD_AC` | 7.4 kW | Overnight trickle charge for vans |
| `BAY-DC-01` | `FAST_DC` | 50.0 kW | Intermediate turnaround for shuttles |
| `BAY-DC-02` | `FAST_DC` | 50.0 kW | Intermediate turnaround for vans & shuttles |
| `BAY-UR-01` | `ULTRA_RAPID_DC` | 150.0 kW | Rapid megawatt turnaround for heavy trucks |

If an operator attempts to activate a bay that would push cumulative power past 250.0 kW, the engine blocks the allocation and throws a `GridOverloadException`.

---

## 6. Syllabus Mapping

VoltFleet OS maps directly to the CSE2006 (Programming in Java) curriculum:

| Unit | Syllabus Core Topic | Concrete Implementation in VoltFleet OS |
| :---: | :--- | :--- |
| **Unit 1** | **Java Introduction & Flow Control**<br>Variables, types, operators, if/else, switch, while, for loops | Interactive console menu loop, regex VIN validation, route calculation expressions, and status switch expressions in `VoltFleetApp.java`. |
| **Unit 2** | **Object-Oriented Programming**<br>Classes, objects, inheritance, constructor chaining, encapsulation | Abstract base class `Vehicle`, constructor chaining with `super()`, encapsulation with protected members, and polymorphic subclassing in `DeliveryVan`, `HeavyCargoTruck`, `PassengerShuttle`. |
| **Unit 3** | **Abstract Classes & Interfaces**<br>Abstract methods, interface contracts, polymorphism | Interface decoupling via `Dispatchable`, `EnergyChargeable`, and `Auditable`. Multiple interface implementation on core vehicle domain models. |
| **Unit 4** | **Exception Handling & Multithreading**<br>Checked/unchecked exceptions, thread life cycle, synchronization | Custom exception hierarchy rooted at `VoltFleetException`. Background worker thread `TelematicsSimulator` implementing `Runnable`, synchronized grid allocation in `GridLoadBalancer`. |
| **Unit 5** | **Collections, Arrays & File I/O**<br>Arrays, Lists, Maps, PriorityQueues, character/byte stream I/O | `LinkedHashMap` for O(1) VIN lookups, `PriorityQueue` for charging prioritization, 2D array grid scheduling matrix, and CSV export via `BufferedWriter` with try-with-resources. |

---

## 7. Compilation & Execution

### Prerequisites
- Java Development Kit (JDK) 8 or higher (tested on OpenJDK 11, 17, 21, and Oracle JDK 21).
- Terminal environment on Windows, Linux, or macOS.

### Clone the Repository
```bash
git clone https://github.com/AkshatIsWired/voltfleet-os.git
cd voltfleet-os
```

### Build the Project

#### On Windows:
Using the automated build script:
```cmd
build.bat
```
Or manually via `javac`:
```cmd
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\com\voltfleet\exception\*.java src\com\voltfleet\interfaces\*.java src\com\voltfleet\model\*.java src\com\voltfleet\service\*.java src\com\voltfleet\storage\*.java src\com\voltfleet\cli\*.java src\com\voltfleet\test\*.java
```

#### On Linux / macOS:
```bash
chmod +x run.sh
mkdir -p bin
javac -encoding UTF-8 -d bin src/com/voltfleet/exception/*.java src/com/voltfleet/interfaces/*.java src/com/voltfleet/model/*.java src/com/voltfleet/service/*.java src/com/voltfleet/storage/*.java src/com/voltfleet/cli/*.java src/com/voltfleet/test/*.java
```

---

### Running the Application

#### Option A: Interactive Command-Line Console (Default)
Launch the operator cockpit interface:
```bash
# Windows
run.bat

# Linux / macOS
./run.sh

# Or via direct Java command
java -cp bin com.voltfleet.cli.VoltFleetApp
```

#### Option B: Automated Comprehensive Evaluation Demo
Run the complete end-to-end evaluation scenario showing route feasibility checks, exception handling, grid load balancing, multithreaded telematics, and CSV persistence:
```bash
java -cp bin com.voltfleet.cli.VoltFleetApp --demo
```

---

## 8. Unit Tests & Verification

VoltFleet OS includes an automated test runner (`VoltFleetTestSuite`) verifying functional requirements and boundary conditions:

```bash
# Run unit tests with assertions enabled
java -ea -cp bin com.voltfleet.test.VoltFleetTestSuite
```
Or via the Windows launcher:
```cmd
run.bat --test
```

### Test Suite Results
```text
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
```

| Test Case ID | Test Target | Test Scenario | Expected Outcome | Result |
| :---: | :--- | :--- | :--- | :---: |
| `TC-01` | VIN Regular Expression | Invalid syntax (`1V1INVALID!`) | Throws `InvalidVINException` | **PASS** |
| `TC-02` | Polymorphic Consumption | 45 km Van vs 120 km Truck | Dynamic method dispatch calculations | **PASS** |
| `TC-03` | Battery Depletion Safety | Dispatch with SoC < 15% reserve | Throws `BatteryDepletionException` | **PASS** |
| `TC-04` | Substation Load Interlock | Aggregate bay demand > 250 kW | Throws `GridOverloadException` | **PASS** |
| `TC-05` | Multithreaded Telematics | Background sensor ingestion | 4 packets received via worker thread | **PASS** |
| `TC-06` | CSV File Stream I/O | Persistence to `data/fleet_inventory.csv` | File written with headers and verified | **PASS** |

---

## 9. Sample Execution Trace

```text
=========================== FLEET AUDIT SUMMARY ===========================
VIN: 1V1EVLASTMILE01 | Category: Last-Mile Van    | Model: Transit-Volt 350 | SoC:  91.8% | Status: AVAILABLE
VIN: 1V1EVLASTMILE02 | Category: Last-Mile Van    | Model: Transit-Volt 350 | SoC:  21.8% | Status: AVAILABLE
VIN: 1H1EVFREIGHT901 | Category: Heavy Cargo Semi | Model: VoltHauler Semi | SoC:  88.6% | Status: AVAILABLE
VIN: 1P1EVSHUTTLE501 | Category: Transit Shuttle  | Model: MetroE-Shuttle 20 | SoC:  86.4% | Status: AVAILABLE
---------------------------------------------------------------------------
Total Fleet Vehicles : 4
Depot Fleet Energy   : 501.50 / 630.00 kWh (Avg SoC: 79.6%)
===========================================================================

[TEST 2] ROUTE DISPATCH: Dispatching [1V1EVLASTMILE01] on Route RT-URBAN-12 (45 km, 350 kg)...
Result: DISPATCH APPROVED. Vehicle status changed to EN_ROUTE.

[TEST 3] SAFETY INTERLOCK: Dispatching Low-Battery [1V1EVLASTMILE02] on 120 km Route...
SUCCESS: BatteryDepletionException caught and handled!
  -> Message: Battery Depletion Hazard for VIN [1V1EVLASTMILE02]: Required 25.03 kWh, but only 5.75 kWh usable.
  -> Automatically enqueued into smart charging queue!

[TEST 4] GRID POWER BALANCING:
Allocating BAY-UR-01 (150 kW) to Heavy Truck... Load: 150.0 kW / 250.0 kW
Allocating BAY-DC-01 (50 kW) to Passenger Shuttle... Load: 200.0 kW / 250.0 kW
Allocating BAY-DC-02 (50 kW) to Delivery Van... Load: 250.0 kW / 250.0 kW (100% capacity)
Attempting to allocate BAY-AC-01 (7.4 kW)...
SUCCESS: GridOverloadException caught! Substation protected from 257.4 kW brownout.

[TEST 5] MULTITHREADED SENSOR TELEMETRY:
  [SENSOR STREAM] [2026-09-17 14:19:15] VIN: 1V1EVLASTMILE01 | Speed: 67.8 km/h | SoC: 91.8% | Temp: 29.7°C
  [SENSOR STREAM] [2026-09-17 14:19:15] VIN: 1V1EVLASTMILE01 | Speed: 57.9 km/h | SoC: 91.8% | Temp: 29.1°C

[TEST 6] PERSISTENCE ENGINE:
SUCCESS: Fleet inventory exported to data/fleet_inventory.csv (4 records written).
```

---

## 10. License & Academic Integrity

This project is developed as part of the continuous evaluation for course **CSE2006: Programming in Java** at **VIT Bhopal University**.

Licensed under the [MIT License](LICENSE).
