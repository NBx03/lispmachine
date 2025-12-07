package ru.nsu.fit.lispmachine.domain;

import java.util.Collections;
import java.util.Iterator;

public class LispNil implements LispValue, Iterable<LispValue> {
    // Единственный экземпляр
    public static final LispNil INSTANCE = new LispNil();

    private LispNil() {}

    @Override
    public String toString() {
        return "()";
    }

    @Override
    public Iterator<LispValue> iterator() {
        return Collections.emptyIterator();
    }
}