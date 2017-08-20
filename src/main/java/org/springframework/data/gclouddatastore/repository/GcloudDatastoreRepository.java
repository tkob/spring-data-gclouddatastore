package org.springframework.data.gclouddatastore.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;

public interface GcloudDatastoreRepository<T, ID extends Serializable>
    extends CrudRepository<T, ID> {

        Iterable<T> query(Query<Entity> query);
}
