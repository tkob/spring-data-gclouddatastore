package org.springframework.data.datastore.repository.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

public class GcloudDatastoreQueryCreator extends AbstractQueryCreator<Query, Condition> {
    public GcloudDatastoreQueryCreator(PartTree tree, ParameterAccessor accessor) {
        super(tree, accessor);
    }

    @Override
    protected Condition create(Part part, Iterator<Object> parameters) {
        if (part.getType().getKeywords().contains("Equals")) {
            List<String> segments = new ArrayList<String>();
            Iterator<PropertyPath> propertyPathIter =
                part.getProperty().iterator();
            while (propertyPathIter.hasNext()) {
                segments.add(propertyPathIter.next().getSegment());
            }

            Object value = parameters.next();
            if (value == null) {
                return new Condition.IsNull(segments);
            }
            else {
               return new Condition.EqualTo(segments, value);
            }
        }
        else {
            throw new UnsupportedOperationException(
                "Part type not supported: " + part.getType());
        }
    }

    @Override
    protected Condition and(Part part, Condition condition, Iterator<Object> parameters) {
        return new Condition.And(condition, create(part, parameters));
    }

    @Override
    protected Condition or(Condition condition1, Condition condition2) {
        throw new UnsupportedOperationException(
            "Or operator in query method not supported");
    }

    @Override
    protected Query complete(Condition condition, Sort sort) {
        return new Query(condition, sort == null ? Optional.empty() : Optional.of(sort));
    }
}
