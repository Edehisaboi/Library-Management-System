package cli.screens;

import cli.Navigator;
import cli.Screen;
import cli.ScreenId;
import util.KeyboardReader;

public class MainMenu implements Screen {
    private final KeyboardReader reader = new KeyboardReader();
    private final Navigator nav;

    public MainMenu(Navigator navigator) {
        this.nav = navigator;
    }

    @Override
    public void display() {
        while (true) {
            System.out.println("""
                    ==== LIBRARY MANAGEMENT SYSTEM ====
                    1. Login
                    2. Register
                    3. Continue as Guest
                    4. Exit
                    """.trim());

            int choice = reader.getInt("Select an option", 1, 4);
            switch (choice) {
                case 1 -> nav.navigateTo(ScreenId.LOGIN);
                case 2 -> nav.navigateTo(ScreenId.REGISTER);
                case 3 -> nav.navigateTo(ScreenId.GUEST_DASHBOARD);
                case 4 -> {
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                }
            }
        }
    }
}
