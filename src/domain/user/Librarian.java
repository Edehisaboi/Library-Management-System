package domain.user;

public final class Librarian extends User {
    private final String staffId;

    public Librarian(String firstName, String lastName, String email, String password) {
        this(firstName, lastName, email, password, null);
    }

    public Librarian(String firstName, String lastName, String email, String password, String staffId) {
        super(firstName, lastName, email, password);
        this.staffId = staffId == null ? "" : staffId.trim();
    }

    public String getStaffId() {
        return staffId;
    }

    @Override
    public String role() {
        return "LIBRARIAN";
    }
}
