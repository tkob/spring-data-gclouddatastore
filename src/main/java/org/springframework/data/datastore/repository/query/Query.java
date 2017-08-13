package org.springframework.data.datastore.repository.query;

import java.util.Optional;

import org.springframework.data.domain.Sort;

import lombok.Value;

@Value
public class Query {
    private final Condition condition;
    private final Optional<Sort> sort;
}
