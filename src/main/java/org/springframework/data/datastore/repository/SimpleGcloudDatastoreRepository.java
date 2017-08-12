package org.springframework.data.datastore.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.util.Assert;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.KeyQuery;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

public class SimpleGcloudDatastoreRepository<T, ID extends Serializable>
    implements GcloudDatastoreRepository<T, ID>{
    private static final Logger log = LoggerFactory
            .getLogger(SimpleGcloudDatastoreRepository.class);

    private static final int BUFFER_SIZE = 50;

    DatastoreOptions datastoreOptions = DatastoreOptions.getDefaultInstance();

    Marshaller marshaller = new Marshaller();
    Unmarshaller unmarshaller = new Unmarshaller();

    ThreadLocal<Deque<PathElement>> localAncestorsStack =
        new ThreadLocal<Deque<PathElement>>() {
            @Override
            protected Deque<PathElement> initialValue() {
                return new LinkedList<PathElement>();
            }
        };

    @Override
    public Context with(PathElement ancestor, PathElement... other) {
        List<PathElement> ancestors = new ArrayList<>(other.length + 1);
        ancestors.add(ancestor);
        ancestors.addAll(Arrays.asList(other));
        return with(ancestors);
    }

    @Override
    public Context with(Iterable<PathElement> ancestors) {
        Deque<PathElement> ancestorsStack = localAncestorsStack.get();
        int count = 0;
        for (PathElement ancestor : ancestors) {
            ancestorsStack.addLast(ancestor);
            count++;
        }
        return new Context(ancestorsStack, count);
    }

    final EntityInformation<T, ID> entityInformation;
    final String kind;

    public SimpleGcloudDatastoreRepository(
        EntityInformation<T, ID> entityInformation) {
            Assert.notNull(entityInformation,
                "EntityInformation must not be null!");
            this.entityInformation = entityInformation;
            this.kind = entityInformation.getJavaType().getSimpleName();
    }

    public Key getKey(ID id) {
        Datastore datastore = datastoreOptions.getService();

        KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
        Iterable<PathElement> ancestors = localAncestorsStack.get();
        keyFactory.addAncestors(ancestors);

        Key key;
        if (id instanceof Number) {
            key = keyFactory.newKey(((Number) id).longValue());
        }
        else {
            key = keyFactory.newKey(id.toString());
        }
        return key;
    }

    @Override
    public long count() {
        Datastore datastore = datastoreOptions.getService();
        KeyQuery query = Query.newKeyQueryBuilder().setKind(kind).build();
        QueryResults<?> results = datastore.run(query);
        long count = 0;
        while (results.hasNext()) {
            results.next();
            count++;
        }
        return count;
    }

    private void deleteKeys(Iterable<Key> keys) {
        Datastore datastore = datastoreOptions.getService();

        List<Key> buffer = new ArrayList<>(BUFFER_SIZE);
        for (Key key : keys) {
            buffer.add(key);

            if (buffer.size() >= BUFFER_SIZE) {
                datastore.delete(buffer.toArray(new Key[buffer.size()]));
                buffer.clear();
            }
        }
        if (buffer.size() > 0) {
            datastore.delete(buffer.toArray(new Key[buffer.size()]));
        }
    }

    @Override
    public void delete(ID id) {
        deleteKeys(Arrays.asList(getKey(id)));
    }

    @Override
    public void delete(T entity) {
        delete(Arrays.asList(entity));
    }

    @Override
    public void delete(Iterable< ? extends T> entities) {
        deleteKeys(new Iterable<Key>() {
            @Override
            public Iterator<Key> iterator() {
                Iterator<? extends T> entityIter = entities.iterator();
                return new Iterator<Key>() {
                    @Override
                    public boolean hasNext() {
                        return entityIter.hasNext();
                    }

                    @Override
                    public Key next() {
                        T entity = entityIter.next();
                        ID id = entityInformation.getId(entity);
                        return getKey(id);
                    }
                };
            }
        });
    }

    @Override
    public void deleteAll() {
        Datastore datastore = datastoreOptions.getService();
        KeyQuery query = Query.newKeyQueryBuilder().setKind(kind).build();
        deleteKeys(new Iterable<Key>() {
            @Override
            public Iterator<Key> iterator() {
                return datastore.run(query);
            }
        });
    }

    @Override
    public boolean exists(ID id) {
        return findOne(id) != null;
    }

    @Override
    public Iterable<T> query(Query<Entity> query) {
        Datastore datastore = datastoreOptions.getService();
        QueryResults<Entity> results = datastore.run(query);
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return results.hasNext();
                    }

                    @Override
                    public T next() {
                        try {
                            T entity = entityInformation.getJavaType().newInstance();
                            unmarshaller.unmarshalToObject(results.next(), entity);
                            return entity;
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new IllegalStateException();
                        }
                    }
                };
            }
        };
    }
    @Override
    public Iterable<T> findAll() {
        EntityQuery query =
            Query.newEntityQueryBuilder().setKind(kind).build();
        return query(query);
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<ID> idIter = ids.iterator();
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return idIter.hasNext();
                    }

                    @Override
                    public T next() {
                        return findOne(idIter.next());
                    }
                };
            }
        };
    }

    @Override
    public T findOne(ID id) {
        Datastore datastore = datastoreOptions.getService();
        return unmarshaller.unmarshal(
                datastore.get(getKey(id)), entityInformation.getJavaType());
    }

    @Override
    public <S extends T> S save(S entity) {
        save(Arrays.asList(entity));
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        Datastore datastore = datastoreOptions.getService();

        List<FullEntity<? extends IncompleteKey>> buffer = new ArrayList<>();

        for (S entity : entities) {
            ID id = entityInformation.getId(entity);
            Key key = getKey(id);

            buffer.add(marshaller.toEntity(entity, key));
            if (buffer.size() >= BUFFER_SIZE) {
                datastore.put(buffer.toArray(new FullEntity[buffer.size()]));
                buffer.clear();
            }
        }
        if (buffer.size() > 0) {
            datastore.put(buffer.toArray(new FullEntity[buffer.size()]));
        }

        return entities;
    }

}