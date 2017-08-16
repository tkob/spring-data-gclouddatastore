package org.springframework.data.datastore.repository.query;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Value;

public interface Condition {
    void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings);

    @Value
    public static class And implements Condition {
        private final Condition condition1;
        private final Condition condition2;

        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
            condition1.build(sb, counter, bindings);
            sb.append(" AND ");
            condition2.build(sb, counter, bindings);
        }
    }

    public static class Contains implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }

    @Value
    public static class EqualTo implements Condition {
        private final Iterable<String> property;
        private final Object value;

        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
            int i = counter.getAndIncrement();
            String paramName = "param" + i;
            sb.append(String.join(".", property));
            sb.append(" = @");
            sb.append(paramName);
            bindings.put(paramName, value);
        }
    }

    public static class GreaterThan implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }

    public static class GreaterThanOrEqualTo implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }

    public static class HasAncestor implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }

    public static class HasDescendant implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }

    public static class In implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }

    @Value
    public static class IsNull implements Condition {
        private final Iterable<String> propertyPath;

        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
            sb.append(String.join(".", propertyPath));
            sb.append(" IS NULL");
        }
    }

    public static class LessThan implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }

    public static class LessThanOrEqualTo implements Condition {
        @Override
        public void build(StringBuilder sb, AtomicInteger counter, Map<String, Object> bindings) {
        }
    }
}
