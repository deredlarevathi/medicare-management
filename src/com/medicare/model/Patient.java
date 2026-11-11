package com.medicare.model;

import com.medicare.util.Json;

/** Inheritance: a Patient is a Person. */
public class Patient extends Person {
    private int age;
    private String gender;
    private String address;

    public Patient(int id, String name, int age, String gender, String phone, String address) {
        super(id, name, phone);
        this.age = age;
        this.gender = gender;
        this.address = address;
    }

    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }

    @Override
    public String getRole() { return "Patient"; }

    @Override
    public String toJson() {
        return "{\"id\":" + getId()
             + ",\"name\":" + Json.quote(getName())
             + ",\"age\":" + age
             + ",\"gender\":" + Json.quote(gender)
             + ",\"phone\":" + Json.quote(getPhone())
             + ",\"address\":" + Json.quote(address) + "}";
    }
}
