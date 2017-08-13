package org.springframework.data.datastore.repository.query;

import lombok.Value;

public interface Condition {

    @Value
    public static class And implements Condition {
        private final Condition condition1;
        private final Condition condition2;
    }

    public static class Contains implements Condition {
    }

    @Value
    public static class EqualTo implements Condition {
        private final Iterable<String> property;
        private final Object value;
    }

    public static class GreaterThan {
    }

    public static class GreaterThanOrEqualTo implements Condition {
    }

    public static class HasAncestor implements Condition {
    }

    public static class HasDescendant implements Condition {
    }

    public static class In implements Condition {
    }

    @Value
    public static class IsNull implements Condition {
        private final Iterable<String> propertyPath;
    }

    public static class LessThan implements Condition {
    }

    public static class LessThanOrEqualTo implements Condition {
    }
}
