package org.springframework.data.gclouddatastore.repository;

import java.lang.annotation.Annotation;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class GcloudDatastoreRepositoriesRegistrar
    extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class< ? extends Annotation> getAnnotation() {
        return EnableGcloudDatastoreRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new GcloudDatastoreRepositoryConfigurationExtension();
    }
}
