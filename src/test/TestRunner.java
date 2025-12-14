package test;

import test.functional.FunctionalTests;
import test.unit.UnitTests;

/**
 * Main entry point for running all tests.
 * Orchestrates the execution of Unit and Functional test suites.
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Starting Automated Tests...\n");

        try {
            UnitTests.runAll();
            FunctionalTests.runAll();
        } catch (Exception e) {
            System.err.println("Critical Error in Test Runner: ");
            e.printStackTrace();
        }

        System.out.println("All Tests Finished.");
    }
}
