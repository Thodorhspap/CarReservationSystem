# Car Reservation System

A desktop car rental app written in Java (Swing + MySQL) for a Software Engineering university course.
Users log in with one of three roles, and each role gets its own dashboard.

## Features

**Customer**
- Search for available vehicles and see the cost for the chosen dates
- Book a vehicle
- View and cancel your bookings

**Secretary**
- See all active bookings and mark them completed or cancelled
- Update a vehicle's status (available, reserved, maintenance, damaged)

**Admin**
- Manage the fleet: add, delete and change the status of vehicles
- Oversee all bookings

## Tech

- Java 21, Swing UI themed with [FlatLaf](https://www.formdev.com/flatlaf/)
- MySQL through JDBC (MySQL Connector/J)
- MVC structure: `model` (entities + DAOs), `controller`, `view`
- JUnit tests for the controllers, using stub DAOs (no database needed)

## Project structure

```
CarReservationSystem/
  src/
    controller/   Auth, Booking and Vehicle controllers
    model/        Entities (User, Customer, Vehicle, Booking) and DAOs
    view/         Login and the three dashboards
    util/         Database connection and UI theme
  test/           Controller tests and stub DAOs
  run.ps1         Compile and run script
lib/              MySQL connector and FlatLaf jars
Sqlfile-texnologia-logismikou.sql   Database schema and sample data
run.bat           Double-click to compile and run (Windows)
```

## Getting started

1. Install **Java 21** (or newer) and **MySQL**.
2. Create the database by running the SQL script:
   ```
   mysql -u root -p < Sqlfile-texnologia-logismikou.sql
   ```
3. The app connects as MySQL user `root` with an empty password. If your setup is different, edit
   `CarReservationSystem/src/util/DatabaseConnection.java`.
4. Run `run.bat` (or `CarReservationSystem/run.ps1`). It compiles the code and starts the app.

## Demo accounts

The SQL script creates these sample logins:

| Role | Username | Password |
|---|---|---|
| Admin | `admin1` | `admin123` |
| Secretary | `secretary1` | `secretary123` |
| Customer | `customer1` | `customer123` |

## Running the tests

The tests in `CarReservationSystem/test` use JUnit 5 and run without a database.
Open the project in VS Code (with the Java Test Runner extension) or IntelliJ IDEA,
mark `test` as a test folder, add JUnit 5, and run them from there.

## Known limitations

- Passwords are stored and compared as plain text. Hashing them (for example with BCrypt) would be the next step.
