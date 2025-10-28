package screens;

import java.util.HashMap;
import java.util.Map;

public class Manager {
    private final Map<String, Screen> screens = new HashMap<>();

    public void register(String name, Screen screen) {
        screens.put(name, screen);
    }

    public void show(String name) {
        Screen screen = screens.get(name);
        if (screen != null) {
            screen.display();
        } else {
            System.out.println("Screen not found: " + name);
        }
    }
}
