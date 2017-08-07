package com.wcs.designxsd.xdesign.configurator;

import java.io.Serializable;

/**
 *
 * @author lali
 */
public class Person implements Serializable {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String BIRTH = "birth";

    private String firstName;
    private String lastName;
    private int birth;

    public Person() {
    }

    public Person(String firstName, String lastName, int birth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
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

    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }
}
