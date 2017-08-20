package org.springframework.data.gclouddatastore.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;

import com.google.cloud.datastore.PathElement;

@AllArgsConstructor
public class Context implements AutoCloseable {

    private static ThreadLocal<Deque<PathElement>> localAncestorsStack =
        new ThreadLocal<Deque<PathElement>>() {
            @Override
            protected Deque<PathElement> initialValue() {
                return new LinkedList<PathElement>();
            }
        };

    public static Deque<PathElement> getAncestors() {
        return localAncestorsStack.get();
    }

    public static Context with(PathElement ancestor, PathElement... other) {
        List<PathElement> ancestors = new ArrayList<>(other.length + 1);
        ancestors.add(ancestor);
        ancestors.addAll(Arrays.asList(other));
        return with(ancestors);
    }

    public static Context with(Iterable<PathElement> ancestors) {
        Deque<PathElement> ancestorsStack = getAncestors();
        int count = 0;
        for (PathElement ancestor : ancestors) {
            ancestorsStack.addLast(ancestor);
            count++;
        }
        return new Context(count);
    }

    private int count;

    @Override
    public void close() {
        Deque<PathElement> ancestors = getAncestors();
        for (int i = 0; i < count; i++) {
            ancestors.removeLast();
        }
    }
}
