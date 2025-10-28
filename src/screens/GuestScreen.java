package screens;

import utilis.KeyboardReader;

public class GuestScreen implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();

    @Override
    public void display() {
        System.out.println("\n=== GUEST MODE ===");
        System.out.println("1. Browse Available Books");
        System.out.println("2. Return to Main Menu");

        int choice = kbr.getInt("Select an option", 1, 2);
        switch (choice) {
            case 1 -> System.out.println("Browse books coming soon...");
            case 2 -> {
                System.out.println("Returning to main menu...");
                return;
            }
        }
    }
}
