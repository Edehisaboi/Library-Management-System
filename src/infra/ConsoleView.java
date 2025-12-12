package infra;

/**
 * Singleton View class responsible for all console input and output.
 * Delegates low-level reading to InputReader to keep the interface clean.
 */
public class ConsoleView {
    private final InputReader input;

    // Singleton pattern
    private static ConsoleView instance;

    private ConsoleView() {
        this.input = new InputReader();
    }

    /**
     * Gets the single instance of ConsoleView.
     * 
     * @return the ConsoleView instance
     */
    public static synchronized ConsoleView getInstance() {
        if (instance == null) {
            instance = new ConsoleView();
        }
        return instance;
    }

    /**
     * Prints a standard message to stdout.
     * 
     * @param message text to print
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints an error message to stderr.
     * 
     * @param message error text
     */
    public void showError(String message) {
        System.err.println("ERROR: " + message);
    }

    /**
     * Prompts the user for an integer within a range.
     *
     * @param message prompt text
     * @param min     minimum value
     * @param max     maximum value
     * @return user's selection
     */
    public int promptInt(String message, int min, int max) {
        return input.getInt(message, min, max);
    }

    /**
     * Prompts the user for a string.
     * 
     * @param message prompt text
     * @return user input
     */
    public String promptString(String message) {
        return input.getString(message);
    }

    /**
     * Prompts for a string, allowing blank input.
     * 
     * @param message    prompt text
     * @param allowBlank true to allow empty string
     * @return user input
     */
    public String promptString(String message, boolean allowBlank) {
        return input.getString(message, allowBlank);
    }

    /**
     * Prompts for a string with specific constraints.
     * 
     * @param message prompt text
     * @param mode    validation mode
     * @param length  length constraint
     * @return user input
     */
    public String promptString(String message, String mode, int length) {
        return input.getString(message, mode, length);
    }

    /**
     * Prompts for a name (alphabetic characters only).
     * 
     * @param message prompt text
     * @return validated name
     */
    public String promptName(String message) {
        return input.getAlphabeticString(message);
    }

    /**
     * Prompts for a valid email address.
     * 
     * @param message prompt text
     * @return validated email
     */
    public String promptEmail(String message) {
        return input.getEmail(message);
    }

    /**
     * Pauses the application flow until user acknowledges.
     */
    public void pause() {
        input.pause("");
    }
}
