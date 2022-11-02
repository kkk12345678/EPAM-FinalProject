package com.epam.kkorolkov.finalproject.db.entity;

import java.util.Objects;

public class User extends Entity {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isAdmin;
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
                getEmail().equals(user.getEmail()) &&
                getPassword().equals(user.getPassword()) &&
                getFirstName().equals(user.getFirstName()) &&
                getLastName().equals(user.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), getEmail(), getPassword(), getFirstName(), getLastName(), isAdmin, isBlocked);
    }
}
