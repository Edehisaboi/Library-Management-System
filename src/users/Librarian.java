package users;

import authentication.AccessLevel;

import java.time.LocalDate;
import java.util.Objects;

public class Librarian extends Base {
    private final LocalDate hireDate;
    private boolean active;

    public Librarian(String firstName, String lastName, String email, String password, LocalDate hireDate) {
        super(firstName, lastName, email, password);
        this.hireDate = Objects.requireNonNull(hireDate, "Hire date cannot be null!");
        this.active = true;
    }


    @Override
    public AccessLevel getAccessLevel() {
        return AccessLevel.LIBRARIAN;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public String toString() {
        return super.toString() + "\n"
             + "Role: " + getAccessLevel() + "\n"
             + "Staff ID: " + getID() + "\n"
             + "Hire Date: " + hireDate + "\n"
             + "Active: " + active;
    }
}
