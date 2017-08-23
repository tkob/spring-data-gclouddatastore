package org.springframework.data.gclouddatastore.repository;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;

public class GcloudDatastoreRepositoryFactory
    extends RepositoryFactorySupport {
    private static final Logger log = LoggerFactory
            .getLogger(GcloudDatastoreRepositoryFactory.class);

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
        EntityInformation<?, Serializable> entityInformation =
            getEntityInformation(information.getDomainType());
        return getTargetRepositoryViaReflection(
            information, entityInformation,datastoreOptions);
    }

    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(
            Key key,
            EvaluationContextProvider evaluationContextProvider) {
        return new QueryLookupStrategy() {
            @Override
            public RepositoryQuery resolveQuery(
                    Method method,
                    RepositoryMetadata metadata,
                    ProjectionFactory factory,
                    NamedQueries namedQueries) {
                QueryMethod queryMethod =
                    new QueryMethod(method, metadata, factory);
                ResultProcessor resultProcessor = queryMethod.getResultProcessor();
                Class<?> domainType = resultProcessor.getReturnedType().getDomainType();
                PartTree tree = new PartTree(method.getName(), domainType);
                return new RepositoryQuery() {
                    @Override
                    public Object execute(Object[] parameters) {
                        GcloudDatastoreQueryCreator queryCreator =
                            new GcloudDatastoreQueryCreator(
                                tree,
                                new ParametersParameterAccessor(
                                    queryMethod.getParameters(),
                                    parameters));
                        StructuredQuery.Builder<Entity> queryBuilder = queryCreator.createQuery();
                        queryBuilder.setKind(domainType.getSimpleName());

                        Unmarshaller unmarshaller = new Unmarshaller();
                        Datastore datastore = datastoreOptions.getService();
                        QueryResults<Entity> results = datastore.run(queryBuilder.build());
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
                                            Object entity = domainType.newInstance();
                                            unmarshaller.unmarshalToObject(results.next(), entity);
                                            return entity;
                                        } catch (InstantiationException | IllegalAccessException e) {
                                            throw new IllegalStateException(e);
                                        }
                                    }
                                };
                            }
                        };

                        Stream<Object> result = StreamSupport.stream(iterable.spliterator(), false);
                        return resultProcessor.processResult(result);
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
