package org.springframework.data.datastore.repository.query;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPathFactoryBean;
import org.springframework.data.datastore.repository.Person;
import org.springframework.data.datastore.repository.PersonRepository;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.parser.PartTree;

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
                queryMethod.getResultProcessor().getReturnedType().getDomainType()),
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
        Query query = creator.createQuery();

        // Verify
        Assert.assertEquals(
            new Query(
                new Condition.EqualTo(
                    Arrays.asList("firstName"),
                    "John"),
                Optional.empty()),
            query);
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
        Query query = creator.createQuery();

        // Verify
        Assert.assertEquals(
            new Query(
                new Condition.And(
                    new Condition.EqualTo(
                        Arrays.asList("emailAddress"),
                        "john.doe@example.com"),
                    new Condition.EqualTo(
                        Arrays.asList("lastName"),
                        "Doe")),
                Optional.empty()),
            query);
    }
}
