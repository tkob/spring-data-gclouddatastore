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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;

import org.springframework.data.gclouddatastore.repository.query.GcloudDatastoreQueryCreator;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.parser.PartTree;

public class GcloudDatastoreRepositoryFactory extends RepositoryFactorySupport {

	DatastoreOptions datastoreOptions = DatastoreOptions.getDefaultInstance();

	public GcloudDatastoreRepositoryFactory(DatastoreOptions datastoreOptions) {
		this.datastoreOptions = datastoreOptions;
	}

	@Override
	public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(
			Class<T> domainClass) {

		return new GcloudDatastoreEntityInformation<T, ID>(domainClass);
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SimpleGcloudDatastoreRepository.class;
	}

	@Override
	protected Object getTargetRepository(RepositoryInformation information) {
		EntityInformation<?, Serializable> entityInformation = getEntityInformation(
				information.getDomainType());
		return getTargetRepositoryViaReflection(information, entityInformation,
				this.datastoreOptions);
	}

	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(Key key,
			EvaluationContextProvider evaluationContextProvider) {

		return new QueryLookupStrategy() {
			@Override
			public RepositoryQuery resolveQuery(Method method,
					RepositoryMetadata metadata, ProjectionFactory factory,
					NamedQueries namedQueries) {

				QueryMethod queryMethod = new QueryMethod(method, metadata, factory);

				ResultProcessor resultProcessor = queryMethod.getResultProcessor();
				Class<?> domainType = resultProcessor.getReturnedType().getDomainType();
				PartTree tree = new PartTree(method.getName(), domainType);
				return new RepositoryQuery() {
					@Override
					public Object execute(Object[] parameters) {
						GcloudDatastoreQueryCreator queryCreator = new GcloudDatastoreQueryCreator(
								tree,
								new ParametersParameterAccessor(
										queryMethod.getParameters(), parameters),
								datastoreOptions);
						StructuredQuery.Builder<Entity> queryBuilder = queryCreator
								.createQuery();
						queryBuilder.setKind(domainType.getSimpleName());

						Unmarshaller unmarshaller = new Unmarshaller();
						Datastore datastore = datastoreOptions.getService();
						QueryResults<Entity> results = datastore
								.run(queryBuilder.build());

						try {
							if (queryMethod.isCollectionQuery()) {
								List<Object> result = new ArrayList<Object>();
								while (results.hasNext()) {
									Object entity = domainType.newInstance();
									unmarshaller.unmarshalToObject(results.next(),
											entity);
									result.add(entity);
								}
								return resultProcessor.processResult(result);
							}
							else if (queryMethod.isStreamQuery()) {

								Iterable<Object> iterable = new Iterable<Object>() {
									@Override
									public Iterator<Object> iterator() {
										return new Iterator<Object>() {
											@Override
											public boolean hasNext() {
												return results.hasNext();
											}

											@Override
											public Object next() {
												try {
													Object entity = domainType
															.newInstance();
													unmarshaller.unmarshalToObject(
															results.next(), entity);
													return entity;
												}
												catch (InstantiationException
														| IllegalAccessException e) {
													throw new IllegalStateException(e);
												}
											}
										};
									}
								};
								Stream<Object> result = StreamSupport
										.stream(iterable.spliterator(), false);
								return resultProcessor.processResult(result);
							}
							else if (queryMethod.isQueryForEntity()) {
								Object result;
								if (!results.hasNext()) {
									result = null;
								}
								else {
									result = domainType.newInstance();
									unmarshaller.unmarshalToObject(results.next(),
											result);
								}
								return resultProcessor.processResult(result);
							}
							throw new UnsupportedOperationException(
									"Query method not supported.");
						}
						catch (InstantiationException | IllegalAccessException e) {
							throw new IllegalStateException(e);
						}
					}

					@Override
					public QueryMethod getQueryMethod() {
						return queryMethod;
					}
				};
			}
		};
	}
}
