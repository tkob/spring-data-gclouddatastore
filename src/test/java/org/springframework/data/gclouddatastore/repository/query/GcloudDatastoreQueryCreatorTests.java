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

package org.springframework.data.gclouddatastore.repository.query;

import java.lang.reflect.Method;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;
import org.junit.Test;

import org.springframework.data.gclouddatastore.repository.PersonRepository;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.parser.PartTree;

import static org.junit.Assert.assertEquals;

public class GcloudDatastoreQueryCreatorTests {

	private GcloudDatastoreQueryCreator createCreator(Class<?> repositoryClass,
			Method method, Object... values) {

		QueryMethod queryMethod = new QueryMethod(method,
				new DefaultRepositoryMetadata(repositoryClass),
				new SpelAwareProxyProjectionFactory());
		return new GcloudDatastoreQueryCreator(
				new PartTree(method.getName(),
						queryMethod.getResultProcessor().getReturnedType()
								.getDomainType()),
				new ParametersParameterAccessor(queryMethod.getParameters(), values),
				DatastoreOptions.getDefaultInstance());
	}

	@Test
	public void testSingleCondition() throws Exception {
		// Setup
		GcloudDatastoreQueryCreator creator = createCreator(PersonRepository.class,
				PersonRepository.class.getMethod("findByFirstName", String.class),
				"John");

		// Exercise
		StructuredQuery.Builder<Entity> queryBuilder = creator.createQuery();

		// Verify
		assertEquals(Query.newEntityQueryBuilder()
				.setFilter(StructuredQuery.PropertyFilter.eq("firstName", "John"))
				.build(), queryBuilder.build());
	}

	@Test
	public void testAndCondition() throws Exception {
		// Setup
		GcloudDatastoreQueryCreator creator = createCreator(PersonRepository.class,
				PersonRepository.class.getMethod("findByEmailAddressAndLastName",
						String.class, String.class),
				"john.doe@example.com", "Doe");

		// Exercise
		StructuredQuery.Builder<Entity> queryBuilder = creator.createQuery();

		// Verify
		assertEquals(
				Query.newEntityQueryBuilder()
						.setFilter(StructuredQuery.CompositeFilter.and(
								StructuredQuery.PropertyFilter.eq("emailAddress",
										"john.doe@example.com"),
								StructuredQuery.PropertyFilter.eq("lastName", "Doe")))
						.build(),
				queryBuilder.build());
	}
}
