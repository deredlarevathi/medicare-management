package com.medicare.model;

import com.medicare.util.Json;

/** A staff member who can log in. Inherits id/name/phone from Person. */
public class User extends Person {
    private String username;
    private String role; // ADMIN, RECEPTIONIST or DOCTOR

    public User(int id, String username, String fullName, String role) {
        super(id, fullName, "");
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }

    @Override
    public String getRole() { return role; }

    @Override
    public String toJson() {
        return "{\"id\":" + getId()
             + ",\"username\":" + Json.quote(username)
             + ",\"name\":" + Json.quote(getName())
             + ",\"role\":" + Json.quote(role) + "}";
    }
}
