package org.springframework.data.datastore.repository.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Sort;

import lombok.Value;

@Value
public class Query {
    private final Condition condition;
    private final Optional<Sort> sort;

    public Map<String, Object> build(StringBuilder sb) {
        sb.append("WHERE ");
        Map<String, Object> bindings = new HashMap<>();
        condition.build(sb, new AtomicInteger(), bindings);
        return bindings;
    }
}
