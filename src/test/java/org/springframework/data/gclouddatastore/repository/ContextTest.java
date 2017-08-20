package org.springframework.data.gclouddatastore.repository;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.google.cloud.datastore.PathElement;

public class ContextTest {
    @Test
    public void testWith1() {
        // Exercise
        try (Context ctx = Context.with(Arrays.asList(
                PathElement.of("Kind", 1)))) {
            // Verify
            assertThat(Context.getAncestors(), contains(
                PathElement.of("Kind", 1)));
        }
    }

    @Test
    public void testWith2() {
        // Exercise
        try (Context ctx = Context.with(Arrays.asList(
                PathElement.of("Kind", 1),
                PathElement.of("Kind", 2)))) {
            // Verify
            assertThat(Context.getAncestors(), contains(
                PathElement.of("Kind", 1),
                PathElement.of("Kind", 2)));
        }
    }

    @Test
    public void testWithVararg1() {
        // Exercise
        try (Context ctx = Context.with(
                PathElement.of("Kind", 1))) {
            // Verify
            assertThat(Context.getAncestors(), contains(
                PathElement.of("Kind", 1)));
        }
    }

    @Test
    public void testWithVararg2() {
        // Exercise
        try (Context ctx = Context.with(
                PathElement.of("Kind", 1),
                PathElement.of("Kind", 2))) {
            // Verify
            assertThat(Context.getAncestors(), contains(
                PathElement.of("Kind", 1),
                PathElement.of("Kind", 2)));
        }
    }

    @Test
    public void testClose1() {
        // Setup, Exercise
        try (Context ctx = Context.with(
                PathElement.of("Kind", 1))) {
        }

        // Verify
        assertThat(Context.getAncestors(), empty());
    }

    @Test
    public void testClose2() {
        // Setup, Exercise
        try (Context ctx = Context.with(
                PathElement.of("Kind", 1),
                PathElement.of("Kind", 1))) {
        }

        // Verify
        assertThat(Context.getAncestors(), empty());
    }
}
