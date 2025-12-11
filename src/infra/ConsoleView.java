package infra;

public class ConsoleView {
    private final InputReader input;

    // Singleton pattern
    private static ConsoleView instance;

    private ConsoleView() {
        this.input = new InputReader();
    }

    public static synchronized ConsoleView getInstance() {
        if (instance == null) {
            instance = new ConsoleView();
        }
        return instance;
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.err.println("ERROR: " + message);
    }

    public int promptInt(String message, int min, int max) {
        return input.getInt(message, min, max);
    }

    public String promptString(String message) {
        return input.getString(message);
    }

    public String promptString(String message, boolean allowBlank) {
        return input.getString(message, allowBlank);
    }

    public String promptString(String message, String mode, int length) {
        return input.getString(message, mode, length);
    }

    public String promptName(String message) {
        return input.getAlphabeticString(message);
    }

    public String promptEmail(String message) {
        return input.getEmail(message);
    }

    public void pause() {
        input.pause("");
    }
}
