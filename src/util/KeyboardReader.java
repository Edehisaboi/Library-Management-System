package util;

import java.util.InputMismatchException;
import java.util.Scanner;

public class KeyboardReader {
    private final Scanner scanner = new Scanner(System.in);

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

    public String getString(String message) {
        return getString(message, false);
    }

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

    public String getString(String message, String mode, int length) {
        return getString(message, mode, length, false);
    }

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

    public void pause(String prompt) {
        System.out.print("\n" + (prompt == null || prompt.isBlank() ? "Press Enter to continue..." : prompt));
        scanner.nextLine();
    }
}
