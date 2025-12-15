package ru.nsu.fit.lispmachine.runtime;

import ru.nsu.fit.lispmachine.domain.*;
import java.util.List;

public class UserDefinedFunction implements LispFunction {
    private final List<String> params;
    private final LispValue body;
    private final ExecutionContext closure;

    public UserDefinedFunction(List<String> params, LispValue body, ExecutionContext closure) {
        this.params = params;
        this.body = body;
        this.closure = closure;
    }

    @Override
    public LispValue apply(List<LispValue> args, ExecutionContext callSiteContext) {
        if (args.size() != params.size()) {
            throw new IllegalArgumentException("Expected " + params.size() + " args, got " + args.size());
        }

        ExecutionContext localContext = new ExecutionContext(closure);
        for (int i = 0; i < params.size(); i++) {
            localContext.define(params.get(i), args.get(i));
        }

        // ВАЖНО: Используем приватный метод evalBody через рефлексию или просто копируем логику.
        // Но так как evalBody приватный в Evaluator, проще создать Evaluator и вызвать eval
        // для каждого выражения в body.
        Evaluator evaluator = new Evaluator();
        LispValue result = LispNil.INSTANCE;

        // body - это список выражений (Cons), идем по нему
        if (body instanceof Iterable) {
            for (LispValue expr : (Iterable<LispValue>) body) {
                result = evaluator.eval(expr, localContext);
            }
        } else {
            result = evaluator.eval(body, localContext);
        }
        return result;
    }
}