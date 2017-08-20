package org.springframework.data.datastore.repository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends GcloudDatastoreRepository<Person, Long> {
    Person findById(long id);

    List<Person> findByFirstName(String firstName);

    List<Person> findByEmailAddressAndLastName(String emailAddress, String lastName);

    // Enables the distinct flag for the query
    List<Person> findDistinctPeopleByLastName(String lastName);
    List<Person> findPeopleDistinctByLastName(String lastName);

    // Enabling ignoring case for an individual property
    List<Person> findByLastNameIgnoreCase(String lastName);
    // Enabling ignoring case for all suitable properties
    List<Person> findByLastNameAndFirstNameAllIgnoreCase(String lastName, String firstName);

    // Enabling static ORDER BY for a query
    List<Person> findByLastNameOrderByFirstNameAsc(String lastName);
    List<Person> findByLastNameOrderByFirstNameDesc(String lastName);

    Optional<Person> findFirstByBirthYear(int birthYear);
}
