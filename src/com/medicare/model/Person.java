package com.medicare.model;

import com.medicare.util.Jsonable;

/** Abstraction + encapsulation: common data for every person in the hospital. */
public abstract class Person implements Jsonable {
    private int id;
    private String name;
    private String phone;

    public Person(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    /** Each subclass states its own role (polymorphism). */
    public abstract String getRole();
}
