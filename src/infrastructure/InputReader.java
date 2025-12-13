package infrastructure;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Helper class for reading and validating user input from the console.
 * Wraps System.in operations to handle exceptions and enforce rules.
 */
public class InputReader {
    private final Scanner scanner = new Scanner(System.in);

    // Simple email regex: checks for @ and .
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    // Alphabetic only regex (allowing spaces/hyphens for names like "Mary-Jane")
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s-]+$");

    /**
     * Reads an integer from the user.
     * Loops until valid input is received.
     *
     * @param message prompt to display
     * @return the entered integer
     */
    public int getInt(String message) {
        int number;
        while (true) {
            System.out.print(message + ": ");
            try {
                number = scanner.nextInt();
                scanner.nextLine();
                return number;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Reads an integer within a specified range (inclusive).
     *
     * @param message prompt to display
     * @param min     minimum value
     * @param max     maximum value
     * @return the valid integer
     */
    public int getInt(String message, int min, int max) {
        int number;
        while (true) {
            number = getInt(message);
            if (number < min || number > max) {
                System.out.printf("Input must be between %d and %d.%n", min, max);
            } else {
                return number;
            }
        }
    }

    /**
     * Reads a single character from the user.
     *
     * @param message prompt to display
     * @return the character (uppercased)
     */
    public char getChar(String message) {
        while (true) {
            System.out.print(message + ": ");
            String input = scanner.nextLine().trim();
            if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                return Character.toUpperCase(input.charAt(0));
            }
            System.out.println("Invalid input. Please enter a single letter.");
        }
    }

    /**
     * Reads a non-blank string from the user.
     *
     * @param message prompt to display
     * @return the input string
     */
    public String getString(String message) {
        return getString(message, false);
    }

    /**
     * Reads a string from the user, optionally allowing blanks.
     *
     * @param message    prompt to display
     * @param allowBlank if true, empty input returns empty string
     * @return the input string
     */
    public String getString(String message, boolean allowBlank) {
        while (true) {
            System.out.print(message + ": ");
            String input = scanner.nextLine();
            if (allowBlank)
                return input;
            String trimmed = input.trim();
            if (!trimmed.isEmpty())
                return trimmed;
            System.out.println("Input cannot be blank.");
        }
    }

    /**
     * Reads a string with length constraints (max length).
     *
     * @param message prompt to display
     * @param mode    constraint mode ("MAX" or "MIN")
     * @param length  length threshold
     * @return valid string
     */
    public String getString(String message, String mode, int length) {
        return getString(message, mode, length, false);
    }

    /**
     * Reads a string with constraints.
     *
     * @param message    prompt to display
     * @param mode       "MAX" or "MIN" length check
     * @param length     constraint value
     * @param allowBlank true to allow empty input
     * @return valid string
     */
    public String getString(String message, String mode, int length, boolean allowBlank) {
        while (true) {
            String input = getString(message, allowBlank);
            String check = allowBlank ? input : input.trim();

            if (mode.equalsIgnoreCase("MAX") && check.length() > length) {
                System.out.printf("Input must not exceed %d characters.%n", length);
            } else if (mode.equalsIgnoreCase("MIN") && check.length() < length) {
                System.out.printf("Input must be at least %d characters.%n", length);
            } else {
                return allowBlank ? input : check;
            }
        }
    }

    /**
     * Reads a name (alphabetic characters, spaces, hyphens only).
     *
     * @param message prompt to display
     * @return validated name string
     */
    public String getAlphabeticString(String message) {
        while (true) {
            String input = getString(message);
            if (NAME_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println("Input must contain only letters, spaces, or hyphens.");
        }
    }

    /**
     * Reads and validates an email address format.
     *
     * @param message prompt to display
     * @return validated email string
     */
    public String getEmail(String message) {
        while (true) {
            String input = getString(message);
            if (EMAIL_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println("Invalid email format. Please try again.");
        }
    }

    /**
     * Pauses execution until the user presses Enter.
     *
     * @param prompt optional message to display before waiting
     */
    public void pause(String prompt) {
        System.out.print("\n" + (prompt == null || prompt.isBlank() ? "Press Enter to continue..." : prompt));
        scanner.nextLine();
    }
}
