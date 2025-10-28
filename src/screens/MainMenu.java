package screens;

import utilis.KeyboardReader;


public class MainMenu implements Screen {
    private final KeyboardReader reader = new KeyboardReader();
    private final Manager manager;

    public MainMenu(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void display() {
        while (true) {
            System.out.println(
                    """
                    ==== LIBRARY MANAGEMENT SYSTEM ====
                    1. Login
                    2. Register
                    3. Continue as Guest
                    4. Exit
                    """
            );

            int choice = reader.getInt("Select an option", 1, 4);
            switch (choice) {
                case 1 -> manager.show("login");
                case 2 -> manager.show("register");
                case 3 -> manager.show("guest");
                case 4 -> {
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                }
            }
        }
    }
}
