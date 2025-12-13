package domain.user;

import util.Validation;

import java.util.UUID;
import java.util.Objects;

/**
 * Represents a registered user of the library system.
 * This is an abstract base class for specific roles like Member and Librarian.
 */
public abstract class User {
    private final UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /**
     * Creates a new user with the specified details.
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email address
     * @param password  the user's login password
     */
    public User(String firstName, String lastName, String email, String password) {
        this.id = UUID.randomUUID();
        this.firstName = Validation.nonBlank(firstName, "firstName");
        this.lastName = Validation.nonBlank(lastName, "lastName");
        this.email = Validation.nonBlank(email, "email");
        this.password = Objects.requireNonNull(password, "password cannot be null");
    }

    /**
     * Gets the unique identifier for this user.
     * 
     * @return the UUID of the user
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the first name of the user.
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the user.
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the email address of the user.
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the user's first name.
     * 
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = Validation.nonBlank(firstName, "firstName");
    }

    /**
     * Updates the user's last name.
     * 
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = Validation.nonBlank(lastName, "lastName");
    }

    /**
     * Returns the role of the user.
     * 
     * @return a string representing the user's role
     */
    public abstract String role();

    @Override
    public String toString() {
        return "ID: " + id + "\n"
                + "Name: " + firstName + " " + lastName + "\n"
                + "Email: " + email;
    }
}
