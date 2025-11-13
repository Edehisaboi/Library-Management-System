package domain.user;

import util.Validation;

import java.util.UUID;
import java.util.Objects;

public abstract class User {
    private final UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User(String firstName, String lastName, String email, String password) {
        this.id = UUID.randomUUID();
        this.firstName = Validation.nonBlank(firstName, "firstName");
        this.lastName = Validation.nonBlank(lastName, "lastName");
        this.email = Validation.nonBlank(email, "email");
        this.password = Objects.requireNonNull(password, "password cannot be null");
    }

    public UUID getId() {
        return id;
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

    public void setFirstName(String firstName) {
        this.firstName = Objects.requireNonNull(firstName, "New first name cannot be null!");
    }

    public void setLastName(String lastName) {
        this.lastName = Objects.requireNonNull(lastName, "New last name cannot be null!");
    }

    public abstract String role();

    @Override
    public String toString() {
        return "ID: " + id + "\n"
                + "Name: " + firstName + " " + lastName + "\n"
                + "Email: " + email;
    }
}
