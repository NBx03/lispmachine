package ru.nsu.fit.lispmachine.runtime;

import ru.nsu.fit.lispmachine.domain.LispValue;
import java.util.List;
import java.util.function.Function;

public record NativeFunction(Function<List<LispValue>, LispValue> body) implements LispFunction {
    @Override
    public LispValue apply(List<LispValue> args, ExecutionContext context) {
        return body.apply(args);
    }

    @Override
    public String toString() {
        return "<native-function>";
    }
}