package cli;

import java.util.EnumMap;
import java.util.Map;

public class Navigator {
    private final Map<ScreenId, Screen> screens = new EnumMap<>(ScreenId.class);

    public void register(ScreenId id, Screen screen) {
        screens.put(id, screen);
    }

    public void navigateTo(ScreenId id) {
        Screen screen = screens.get(id);
        if (screen == null) {
            System.out.println("Screen not found: " + id);
            return;
        }
        clearScreen();
        screen.display();
    }

    private void clearScreen() {
        // System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
