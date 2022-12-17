package com.epam.kkorolkov.finalproject.db.entity;

import java.util.Objects;

/**
 * Represents a record in <i>users</i> table.
 */
public class User extends Entity {
    /** User's email */
    private String email;

    /** User's hashed password */
    private String password;

    /** User's first name */
    private String firstName;

    /** User's last name */
    private String lastName;

    /** Indicates whether a user has administrator's privileges */
    private boolean isAdmin;

    /** Indicates whether a user is blocked */
    private boolean isBlocked;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + this.getId() +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isAdmin=" + isAdmin +
                ", isBlocked=" + isBlocked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return this.getId() == user.getId() &&
                isAdmin == user.isAdmin &&
                isBlocked == user.isBlocked &&
                email.equals(user.getEmail()) &&
                password.equals(user.getPassword()) &&
                firstName.equals(user.getFirstName()) &&
                lastName.equals(user.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), email, password, firstName, lastName, isAdmin, isBlocked);
    }
}
