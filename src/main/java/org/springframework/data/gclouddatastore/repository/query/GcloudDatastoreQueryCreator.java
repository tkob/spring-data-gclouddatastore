package org.springframework.data.gclouddatastore.repository.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;

public class GcloudDatastoreQueryCreator
        extends AbstractQueryCreator<StructuredQuery.Builder<Entity>, StructuredQuery.Filter> {

    public GcloudDatastoreQueryCreator(
            PartTree tree,
            ParameterAccessor accessor) {
        super(tree, accessor);
    }

    @Override
    protected StructuredQuery.Filter create(
            Part part, Iterator<Object> parameters) {
        if (part.getType().getKeywords().contains("Equals")) {
            List<String> segments = new ArrayList<String>();
            Iterator<PropertyPath> propertyPathIter =
                part.getProperty().iterator();
            while (propertyPathIter.hasNext()) {
                segments.add(propertyPathIter.next().getSegment());
            }
            String property = String.join(".", segments);

            Object value = parameters.next();
            if (value == null) {
                return StructuredQuery.PropertyFilter.isNull(property);
            }
            else if (value instanceof Boolean) {
                return StructuredQuery.PropertyFilter.eq(property, (Boolean)value);
            }
            else if (value instanceof Double || value instanceof Float) {
                return StructuredQuery.PropertyFilter.eq(property, ((Number)value).doubleValue());
            }
            else if (value instanceof Number) {
                return StructuredQuery.PropertyFilter.eq(property, ((Number)value).longValue());
            }
            else if (value instanceof CharSequence) {
                return StructuredQuery.PropertyFilter.eq(property, ((CharSequence)value).toString());
            }
            else {
                throw new UnsupportedOperationException(
                    "Value type not supported: "
                    + value + " : " + value.getClass());
            }
        }
        else {
            throw new UnsupportedOperationException(
                "Part type not supported: " + part.getType());
        }
    }

    @Override
    protected StructuredQuery.Filter and(
            Part part,
            StructuredQuery.Filter filter,
            Iterator<Object> parameters) {
        return StructuredQuery.CompositeFilter.and(
            filter, create(part, parameters));
    }

    @Override
    protected StructuredQuery.Filter or(
            StructuredQuery.Filter filter1,
            StructuredQuery.Filter filter2) {
        throw new UnsupportedOperationException(
            "Or operator in query method not supported");
    }

    @Override
    protected StructuredQuery.Builder<Entity> complete(StructuredQuery.Filter filter, Sort sort) {
        return Query.newEntityQueryBuilder().setFilter(filter);
    }
}
