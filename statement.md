# Project Statement: VoltFleet OS

## 1. Problem statement
Commercial logistics and transit operators face practical challenges when managing battery electric vehicle (EV) fleets:
1. Battery range depletion: Vehicle energy consumption depends on cargo weight, regenerative braking, aerodynamics, and climate control loads. Inaccurate range estimation leads to vehicles running out of charge mid-route.
2. Depot grid overloads: When multiple vehicles charge simultaneously at DC fast terminals, the combined electrical load can exceed substation transformer ratings, causing circuit breaker trips and peak utility surcharges.
3. Telematics and audit tracking: Fleet dispatchers require telemetry data to monitor state of charge, temperature, and speed, as well as verifiable records calculating carbon dioxide offsets.

## 2. Scope of the project
VoltFleet OS is a Java console application for electric vehicle fleet dispatch, depot power coordination, and telemetry tracking. The application includes:
- Energy consumption models for delivery vans, heavy semi-trucks, and passenger shuttles.
- Pre-dispatch checks that enforce a 15% state-of-charge safety buffer before a vehicle leaves the depot.
- Depot power allocation across AC and DC charging bays with a hard 250 kW ceiling.
- Multithreaded background sensor simulation producing velocity, temperature, and battery telemetry.
- File storage for fleet inventory and audit logs using Java character and buffered streams.

## 3. Target users
- Fleet dispatchers assigning daily delivery routes.
- Depot facility managers monitoring electrical load across charging terminals.
- Compliance managers tracking fossil fuel displacement and carbon reduction figures.

## 4. High-level features
- Dynamic route energy calculations based on payload and vehicle characteristics.
- Automatic dispatch prevention with custom exceptions when battery charge is insufficient.
- Queue-based charging priority for vehicles returning with the lowest battery levels.
- Thread-safe charging bay booking to prevent transformer overload.
- CSV export and import without external library dependencies.
