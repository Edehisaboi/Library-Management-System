package cli.screens;

import cli.Navigator;
import cli.Screen;
import cli.ScreenId;
import util.KeyboardReader;

public class GuestDashboard implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final Navigator nav;

    public GuestDashboard(Navigator navigator) {
        this.nav = navigator;
    }

    @Override
    public void display() {
        while (true) {
            System.out.println("""
                    ===== GUEST DASHBOARD =====
                    1. Browse Available Books
                    2. Return to Main Menu
                    """.trim());

            int choice = kbr.getInt("Select an option", 1, 2);
            switch (choice) {
                case 1 -> nav.navigateTo(ScreenId.SEARCH_MEDIA);
                case 2 -> {
                    nav.navigateTo(ScreenId.MAIN);
                    return;
                }
            }
        }
    }
}
