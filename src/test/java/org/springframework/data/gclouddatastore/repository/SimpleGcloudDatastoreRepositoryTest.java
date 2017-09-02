package org.springframework.data.gclouddatastore.repository;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.cloud.datastore.PathElement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=SimpleGcloudDatastoreRepositoryTest.class)
@Configuration
@EnableGcloudDatastoreRepositories
public class SimpleGcloudDatastoreRepositoryTest {

    @Autowired
    PersonRepository repo;

    @Test
    public void testCount1() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();

            // Exercize, Verify
            assertEquals(0L, repo.count());
        }
    }

    @Test
    public void testCount2() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize, Verify
            assertEquals(2L, repo.count());
        }
    }

    @Test
    public void testDeleteId() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize
            repo.delete(123L);
            
            // Verify
            assertThat(repo.findAll(), contains(new Person(456)));
        }
    }

    @Test
    public void testDeleteEntity() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize
            repo.delete(new Person(123));
            
            // Verify
            assertThat(repo.findAll(), contains(new Person(456)));
        }
    }

    @Test
    public void testDeleteEntities() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456), new Person(789)));

            // Exercize
            repo.delete(Arrays.asList(new Person(123), new Person(789)));
            
            // Verify
            assertThat(repo.findAll(), contains(new Person(456)));
        }
    }

    @Test
    public void testDeleteAll() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize
            repo.deleteAll();
            
            // Verify
            assertThat(repo.findAll(), emptyIterable());
        }
    }

    @Test
    public void testExists1() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(new Person(123));

            // Exercize, Verify
            assertEquals(true, repo.exists(123L));
        }
    }

    @Test
    public void testExists2() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(new Person(123));

            // Exercize, Verify
            assertEquals(false, repo.exists(456L));
        }
    }

    @Test
    public void testFindAll1() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();

            // Exercize, Verify
            assertThat(repo.findAll(), emptyIterable());
        }
    }

    @Test
    public void testFindAll2() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize, Verify
            assertThat(repo.findAll(), contains(new Person(123), new Person(456)));
        }
    }

    @Test
    public void testFindAllIds1() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize, Verify
            assertThat(
                repo.findAll(Arrays.asList(123L)),
                contains(new Person(123)));
        }
    }

    @Test
    public void testFindAllIds2() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize, Verify
            assertThat(
                repo.findAll(Arrays.asList(123L, 456L)),
                contains(new Person(123), new Person(456)));
        }
    }

    @Test
    public void testFindOne1() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize, Verify
            assertEquals(
                null,
                repo.findOne(789L));
        }
    }

    @Test
    public void testFindOne2() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            // Exercize, Verify
            assertEquals(
                new Person(123),
                repo.findOne(123L));
        }
    }

    @Test
    public void testSaveEntity() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();

            // Exercize
            repo.save(new Person(123));

            //  Verify
            assertEquals(
                new Person(123),
                repo.findOne(123L));
        }
    }

    @Test
    public void testSaveEntities() throws Exception {
        try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
            // Setup
            repo.deleteAll();

            // Exercize
            repo.save(Arrays.asList(new Person(123), new Person(456)));

            //  Verify
            assertThat(
                repo.findAll(Arrays.asList(123L, 456L)),
                contains(new Person(123), new Person(456)));
        }
    }
}
