package domain.user;

/**
 * Represents a library staff member with administrative privileges.
 */
public final class Librarian extends User {
    private final String staffId;

    /**
     * Creates a new Librarian.
     *
     * @param firstName first name
     * @param lastName  last name
     * @param email     email address
     * @param password  login password
     */
    public Librarian(String firstName, String lastName, String email, String password) {
        this(firstName, lastName, email, password, null);
    }

    /**
     * Creates a new Librarian with a staff ID.
     *
     * @param firstName first name
     * @param lastName  last name
     * @param email     email address
     * @param password  login password
     * @param staffId   optional staff identifier
     */
    public Librarian(String firstName, String lastName, String email, String password, String staffId) {
        super(firstName, lastName, email, password);
        this.staffId = staffId == null ? "" : staffId.trim();
    }

    /**
     * Gets the staff ID.
     * 
     * @return the staff ID, or empty string if not set
     */
    public String getStaffId() {
        return staffId;
    }

    @Override
    public String role() {
        return "LIBRARIAN";
    }
}
