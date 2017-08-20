package org.springframework.data.gclouddatastore.repository.query;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.data.gclouddatastore.repository.PersonRepository;
import org.springframework.data.gclouddatastore.repository.query.GcloudDatastoreQueryCreator;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.parser.PartTree;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.Query;

public class GcloudDatastoreQueryCreatorTest {

    private GcloudDatastoreQueryCreator createCreator(
            Class<?> repositoryClass,
            Method method,
            Object... values) {
        QueryMethod queryMethod = new QueryMethod(
            method,
            new DefaultRepositoryMetadata(repositoryClass),
            new SpelAwareProxyProjectionFactory());
        return new GcloudDatastoreQueryCreator(
            new PartTree(
                method.getName(),
                queryMethod
                    .getResultProcessor().getReturnedType().getDomainType()),
            new ParametersParameterAccessor(
                queryMethod.getParameters(),
                values));
    }

    @Test
    public void testSingleCondition() throws Exception {
        // Setup
        GcloudDatastoreQueryCreator creator = createCreator(
            PersonRepository.class,
            PersonRepository.class.getMethod("findByFirstName", String.class),
            "John");

        // Exercise
        StructuredQuery.Builder<Entity> queryBuilder = creator.createQuery();

        // Verify
        assertEquals(
            Query.newEntityQueryBuilder()
                .setFilter(
                    StructuredQuery.PropertyFilter.eq("firstName", "John"))
                .build(),
            queryBuilder.build());
    }

    @Test
    public void testAndCondition() throws Exception {
        // Setup
        GcloudDatastoreQueryCreator creator = createCreator(
            PersonRepository.class,
            PersonRepository.class.getMethod("findByEmailAddressAndLastName",
                String.class, String.class),
            "john.doe@example.com", "Doe");

        // Exercise
        StructuredQuery.Builder<Entity> queryBuilder = creator.createQuery();

        // Verify
        assertEquals(
            Query.newEntityQueryBuilder()
                .setFilter(
                    StructuredQuery.CompositeFilter.and(
                        StructuredQuery.PropertyFilter.eq(
                            "emailAddress", "john.doe@example.com"),
                        StructuredQuery.PropertyFilter.eq(
                            "lastName", "Doe")))
                .build(),
            queryBuilder.build());
    }
}
