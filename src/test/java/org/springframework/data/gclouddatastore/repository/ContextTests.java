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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class ContextTests {

	@Test
	public void testWith1() {
		// Exercise
		try (Context ctx = Context.with(Arrays.asList(PathElement.of("Kind", 1)))) {
			// Verify
			assertThat(Context.getAncestors(), contains(PathElement.of("Kind", 1)));
		}
	}

	@Test
	public void testWith2() {
		// Exercise
		try (Context ctx = Context.with(
				Arrays.asList(PathElement.of("Kind", 1), PathElement.of("Kind", 2)))) {
			// Verify
			assertThat(Context.getAncestors(),
					contains(PathElement.of("Kind", 1), PathElement.of("Kind", 2)));
		}
	}

	@Test
	public void testWithVararg1() {
		// Exercise
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
			// Verify
			assertThat(Context.getAncestors(), contains(PathElement.of("Kind", 1)));
		}
	}

	@Test
	public void testWithVararg2() {
		// Exercise
		try (Context ctx = Context.with(PathElement.of("Kind", 1),
				PathElement.of("Kind", 2))) {
			// Verify
			assertThat(Context.getAncestors(),
					contains(PathElement.of("Kind", 1), PathElement.of("Kind", 2)));
		}
	}

	@Test
	public void testClose1() {
		// Setup, Exercise
		try (Context ctx = Context.with(PathElement.of("Kind", 1))) {
		}

		// Verify
		assertThat(Context.getAncestors(), empty());
	}

	@Test
	public void testClose2() {
		// Setup, Exercise
		try (Context ctx = Context.with(PathElement.of("Kind", 1),
				PathElement.of("Kind", 1))) {
		}

		// Verify
		assertThat(Context.getAncestors(), empty());
	}
}
