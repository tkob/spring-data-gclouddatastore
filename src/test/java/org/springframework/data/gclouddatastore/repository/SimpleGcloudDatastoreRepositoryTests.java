/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.springframework.data.gclouddatastore.repository;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.PathElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gclouddatastore.repository.SimpleGcloudDatastoreRepositoryTests.TestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SimpleGcloudDatastoreRepositoryTests.class, TestConfig.class })
@Configuration
@EnableGcloudDatastoreRepositories
public class SimpleGcloudDatastoreRepositoryTests {

	@Autowired
	PersonRepository repo;

	@Test
	public void testCount1() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();

			// Exercise, Verify
			assertEquals(0L, this.repo.count());
		}
	}

	@Test
	public void testCount2() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(2L, this.repo.count());
		}
	}

	@Test
	public void testDeleteId() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise
			this.repo.delete(123L);

			// Verify
			assertThat(this.repo.findAll(), contains(new Person(456)));
		}
	}

	@Test
	public void testDeleteEntity() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise
			this.repo.delete(new Person(123));

			// Verify
			assertThat(this.repo.findAll(), contains(new Person(456)));
		}
	}

	@Test
	public void testDeleteEntities() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(
				Arrays.asList(new Person(123), new Person(456), new Person(789)));

			// Exercise
			this.repo.delete(Arrays.asList(new Person(123), new Person(789)));

			// Verify
			assertThat(this.repo.findAll(), contains(new Person(456)));
		}
	}

	@Test
	public void testDeleteAll() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise
			this.repo.deleteAll();

			// Verify
			assertThat(this.repo.findAll(), emptyIterable());
		}
	}

	@Test
	public void testExists1() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(new Person(123));

			// Exercise, Verify
			assertEquals(true, this.repo.exists(123L));
		}
	}

	@Test
	public void testExists2() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(new Person(123));

			// Exercise, Verify
			assertEquals(false, this.repo.exists(456L));
		}
	}

	@Test
	public void testFindAll1() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();

			// Exercise, Verify
			assertThat(this.repo.findAll(), emptyIterable());
		}
	}

	@Test
	public void testFindAll2() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertThat(this.repo.findAll(), contains(new Person(123), new Person(456)));
		}
	}

	@Test
	public void testFindAllIds1() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertThat(this.repo.findAll(Arrays.asList(123L)), contains(new Person(123)));
		}
	}

	@Test
	public void testFindAllIds2() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertThat(this.repo.findAll(Arrays.asList(123L, 456L)),
				contains(new Person(123), new Person(456)));
		}
	}

	@Test
	public void testFindOne1() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(null, this.repo.findOne(789L));
		}
	}

	@Test
	public void testFindOne2() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(new Person(123), this.repo.findOne(123L));
		}
	}

	@Test
	public void testSaveEntity() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();

			// Exercise
			this.repo.save(new Person(123));

			// Verify
			assertEquals(new Person(123), this.repo.findOne(123L));
		}
	}

	@Test
	public void testSaveEntities() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();

			// Exercise
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Verify
			assertThat(this.repo.findAll(Arrays.asList(123L, 456L)),
				contains(new Person(123), new Person(456)));
		}
	}

	@Test
	public void testQueryMethod_QueryForEntity() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(new Person(456), this.repo.findById(456L));
		}
	}

	@Test
	public void testQueryMethod_QueryForEntity_Optional() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(new Person(123), this.repo.findFirstById(123L)
				.get());
		}
	}

	@Test
	public void testQueryMethod_CollectionQuery() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(
				new Person(123L, "", "John", "Doe", 0, false),
				new Person(456L, "", "Jane", "Doe", 0, false)));

			// Exercise, Verify
			assertEquals(
				Arrays.asList(new Person(456L, "", "Jane", "Doe", 0, false)),
				this.repo.findByFirstName("Jane"));
		}
	}

	@Test
	public void testQueryMethod_StreamQuery() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(
				new Person(123L, "", "John", "Lennon", 0, false),
				new Person(456L, "", "John", "Mayer", 0, false)));

			// Exercise, Verify
			assertEquals(
				Arrays.asList(new Person(456L, "", "John", "Mayer", 0, false)),
				this.repo.findByLastName("Mayer")
					.collect(Collectors.toList()));
		}
	}

	@Test
	public void testQueryMethod_OrderBy() throws Exception {

		try(Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(
				new Person(1L, "", "Fela", "Kuti", 0, false),
				new Person(2L, "", "Tony", "Allen", 0, false),
				new Person(3L, "", "Seun", "Kuti", 0, false),
				new Person(4L, "", "Femi", "Kuti", 0, false)));

			// Exercise, Verify
			assertEquals(
				Arrays.asList(
					new Person(1L, "", "Fela", "Kuti", 0, false),
					new Person(4L, "", "Femi", "Kuti", 0, false),
					new Person(3L, "", "Seun", "Kuti", 0, false)),
				this.repo.findByLastNameOrderByFirstNameAsc("Kuti"));
		}
	}

	@Configuration
	public class TestConfig {

		@Bean
		public DatastoreOptions datastoreOptions() {

			return DatastoreOptions.getDefaultInstance();
		}
	}
}
