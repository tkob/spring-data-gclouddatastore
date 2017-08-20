package org.springframework.data.gclouddatastore.repository;

import lombok.Data;

import org.springframework.data.annotation.Id;

@Data
public class Person {
    public Person() {}
    public Person(long id) { this.id = id; }

    @Id
    private long id;

    private String emailAddress;

    private String firstName;

    private String lastName;

    private int birthYear;

    private boolean citizen;
}