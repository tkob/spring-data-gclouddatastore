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

import java.util.Arrays;

import com.google.cloud.datastore.PathElement;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleGcloudDatastoreRepositoryTests.class)
@Configuration
@EnableGcloudDatastoreRepositories
public class SimpleGcloudDatastoreRepositoryTests {

	@Autowired
	PersonRepository repo;

	@Test
	public void testCount1() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();

			// Exercise, Verify
			assertEquals(0L, this.repo.count());
		}
	}

	@Test
	public void testCount2() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(2L, this.repo.count());
		}
	}

	@Test
	public void testDeleteId() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
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
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
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
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
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
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
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
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(new Person(123));

			// Exercise, Verify
			assertEquals(true, this.repo.exists(123L));
		}
	}

	@Test
	public void testExists2() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(new Person(123));

			// Exercise, Verify
			assertEquals(false, this.repo.exists(456L));
		}
	}

	@Test
	public void testFindAll1() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();

			// Exercise, Verify
			assertThat(this.repo.findAll(), emptyIterable());
		}
	}

	@Test
	public void testFindAll2() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertThat(this.repo.findAll(), contains(new Person(123), new Person(456)));
		}
	}

	@Test
	public void testFindAllIds1() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertThat(this.repo.findAll(Arrays.asList(123L)), contains(new Person(123)));
		}
	}

	@Test
	public void testFindAllIds2() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
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
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(null, this.repo.findOne(789L));
		}
	}

	@Test
	public void testFindOne2() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Exercise, Verify
			assertEquals(new Person(123), this.repo.findOne(123L));
		}
	}

	@Test
	public void testSaveEntity() throws Exception {
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
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
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Setup
			this.repo.deleteAll();

			// Exercise
			this.repo.save(Arrays.asList(new Person(123), new Person(456)));

			// Verify
			assertThat(this.repo.findAll(Arrays.asList(123L, 456L)),
					contains(new Person(123), new Person(456)));
		}
	}
}
