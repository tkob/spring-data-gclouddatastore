package org.springframework.data.datastore.repository;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class GcloudDatastoreRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
    extends RepositoryFactoryBeanSupport<T, S, ID> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new GcloudDatastoreRepositoryFactory();
    }
}
