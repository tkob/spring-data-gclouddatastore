package org.springframework.data.datastore.repository;

import java.util.Deque;

import lombok.AllArgsConstructor;

import com.google.cloud.datastore.PathElement;

@AllArgsConstructor
public class Context implements AutoCloseable {

    private Deque<PathElement> ancestors;
    private int count;

    @Override
    public void close() {
        for (int i = 0; i < count; i++) {
            ancestors.removeLast();
        }
    }
}
