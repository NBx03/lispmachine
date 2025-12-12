package ru.nsu.fit.lispmachine.runtime;

import ru.nsu.fit.lispmachine.domain.LispValue;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private final Map<String, LispValue> scope = new HashMap<>();
    private final ExecutionContext parent;

    public ExecutionContext() {
        this(null);
    }

    public ExecutionContext(ExecutionContext parent) {
        this.parent = parent;
    }

    public void define(String name, LispValue value) {
        scope.put(name, value);
    }

    // Изменение существующей переменной (для set!)
    public void update(String name, LispValue value) {
        if (scope.containsKey(name)) {
            scope.put(name, value);
            return;
        }
        if (parent != null) {
            parent.update(name, value);
            return;
        }
        throw new RuntimeException("Cannot set! undefined variable: " + name);
    }

    public LispValue lookup(String name) {
        if (scope.containsKey(name)) {
            return scope.get(name);
        }
        if (parent != null) {
            return parent.lookup(name);
        }
        return null; // Возвращаем null если не нашли, обработка будет в Evaluator
    }
}