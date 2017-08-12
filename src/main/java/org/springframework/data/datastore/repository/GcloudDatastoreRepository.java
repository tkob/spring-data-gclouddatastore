package org.springframework.data.datastore.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;

public interface GcloudDatastoreRepository<T, ID extends Serializable>
    extends CrudRepository<T, ID> {

        Context with(PathElement ancestor, PathElement... other);

        Context with(Iterable<PathElement> ancestors);

        Iterable<T> query(Query<Entity> query);
}
