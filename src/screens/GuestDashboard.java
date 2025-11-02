package screens;

import utilis.KeyboardReader;

public class GuestDashboard implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final Manager manager;

    public GuestDashboard(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void display() {
        while (true) {
            System.out.println(
                    """
                    ===== GUEST DASHBOARD =====
                    1. Browse Available Books
                    2. Return to Main Menu
                    """
                );

            int choice = kbr.getInt("Select an option", 1, 2);
            switch (choice) {
                case 1 -> manager.show("search");
                case 2 -> {
                    manager.show("main");
                    return;
                }
            }
        }
    }
}
