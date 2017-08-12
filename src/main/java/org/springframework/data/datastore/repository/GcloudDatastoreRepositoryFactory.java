package org.springframework.data.datastore.repository;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

public class GcloudDatastoreRepositoryFactory
    extends RepositoryFactorySupport {

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
        return getTargetRepositoryViaReflection(information, entityInformation);
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
                return new RepositoryQuery() {
                    @Override
                    public Object execute(Object[] parameters) {
                        throw new UnsupportedOperationException(
                            "Query methods not supported yet");
                    }

                    @Override
                    public QueryMethod getQueryMethod() {
                        return new QueryMethod(method, metadata, factory);
                    }
                };
            }
        };
    }

}
