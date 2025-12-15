# Library Management System

A console-based Library Management System implemented in pure Java. This application allows for the management of library media (Books, CDs, DVDs), handling user authentication, inventory tracking, loan transactions, and fine calculations.

## Features

### User Roles

- **Guest**: Search the catalog without logging in.
- **Member**:
  - Search and browse the catalog.
  - Borrow available items (Books, CDs, DVDs).
  - View active loans and due dates.
  - Return items and pay fines.
- **Librarian (Admin)**:
  - Manage Inventory: Add physical copies to existing titles.
  - Manage Users: View member lists and block/unblock members.
  - Reports: View currently overdue loans.

### Core Functionality

- **Catalog System**: specific metadata for Books (ISBN, Publisher), CDs (Tracks, Duration), and DVDs (Region, Rating).
- **Loan Rules**:
  - Standard 7-day loan period.
  - Concurrent loan limits per member.
  - Eligibility checks (blocking members with fines or expired memberships).
- **Fines**: Automatic calculation of overdue fines ($0.50 per day).
- **In-Memory Storage**: Fast operation with data seeded from CSV files on startup.

## Project Architecture

The codebase follows a Clean Architecture / Layered approach:

- **`src/domain`**: Core entities (`User`, `MediaItem`, `Loan`, `Holding`) containing business rules.
- **`src/repo`**: Repository interfaces and in-memory implementations (`HashMap` based).
- **`src/services`**: Application logic (`CatalogService`, `LoanService`) orchestrating the domain and repos.
- **`src/controllers`**: Handles user interaction and menu workflows (`AuthController`, `LibraryController`, `AdminController`).
- **`src/policies`**: Strategy patterns for logic like `LoanRule` and `FinePolicy`.
- **`src/infrastructure`**: Low-level I/O handling (`ConsoleView`, `InputReader`).

## Prerequisites

- **Java JDK 17** or higher.

## How to Run

Since this project does not use a build tool like Maven or Gradle, you can compile and run it using the standard `javac` and `java` commands.

### 1. Compile

Open a terminal in the project root directory and run:

```bash
mkdir out
javac -d out -sourcepath src src/Main.java
```

### 2. Run Application

Start the main application loop:

```bash
java -cp out Main
```

### 3. Default Login Credentials

The system initializes with data from CSVs. You can register a new account or use the registration flow in the app.

- **Member Login**: Register a new account via the menu (Option 3).
- **Librarian Login**: Register a new account via the menu (Option 4).

## Running Tests

The project includes a custom, zero-dependency test framework located in `src/test`.

1.  **Compile Tests**:

    ```bash
    javac -d out -sourcepath src src/test/TestRunner.java
    ```

2.  **Run Test Suite**:
    ```bash
    java -cp out test.TestRunner
    ```

This will execute both **Unit Tests** (isolated logic) and **Functional Tests** (end-to-end scenarios) and report the results to the console.
