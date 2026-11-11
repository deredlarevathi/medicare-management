package com.medicare.model;

import com.medicare.util.Json;

/** Inheritance: a Doctor is a Person. */
public class Doctor extends Person {
    private String specialization;

    public Doctor(int id, String name, String specialization, String phone) {
        super(id, name, phone);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }

    @Override
    public String getRole() { return "Doctor"; }

    @Override
    public String toJson() {
        return "{\"id\":" + getId()
             + ",\"name\":" + Json.quote(getName())
             + ",\"specialization\":" + Json.quote(specialization)
             + ",\"phone\":" + Json.quote(getPhone()) + "}";
    }
}
