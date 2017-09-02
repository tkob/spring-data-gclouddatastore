/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gclouddatastore.repository;

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
	List<Person> findByLastNameAndFirstNameAllIgnoreCase(String lastName,
			String firstName);

	// Enabling static ORDER BY for a query
	List<Person> findByLastNameOrderByFirstNameAsc(String lastName);

	List<Person> findByLastNameOrderByFirstNameDesc(String lastName);

	Optional<Person> findFirstByBirthYear(int birthYear);
}
