package users;

import authentication.AccessLevel;

import java.util.Objects;
import java.util.UUID;

public abstract class Base {
    private final UUID ID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public Base(String firstName, String lastName, String email, String password) {
        this.ID = UUID.randomUUID();
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null!");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null!");
        this.email = Objects.requireNonNull(email, "Email cannot be null!");
        this.password = Objects.requireNonNull(password, "Password cannot be null!");
    }

    public UUID getID() {
        return ID;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public AccessLevel getAccessLevel() {;
        return AccessLevel.GUEST;
    }

    public void setFirstName(String firstName) {
        this.firstName = Objects.requireNonNull(firstName, "New first name cannot be null!");
    }
    public void setLastName(String lastName) {
        this.lastName = Objects.requireNonNull(lastName, "New last name cannot be null!");
    }

    @Override
    public String toString() {
        return "ID: " + ID + "\n"
             + "Name: " + firstName + " " + lastName + "\n"
             + "Email: " + email;
    }
}
