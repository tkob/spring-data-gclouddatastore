package org.springframework.data.gclouddatastore.repository;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.google.cloud.datastore.DatastoreOptions;

public class GcloudDatastoreRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
    extends RepositoryFactoryBeanSupport<T, S, ID> {

    DatastoreOptions datastoreOptions;

    public GcloudDatastoreRepositoryFactoryBean() {
        this.datastoreOptions = DatastoreOptions.getDefaultInstance();
    }

    public GcloudDatastoreRepositoryFactoryBean(DatastoreOptions datastoreOptions) {
        this.datastoreOptions = datastoreOptions;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new GcloudDatastoreRepositoryFactory(datastoreOptions);
    }
}
