package ru.nsu.fit.lispmachine;

import org.junit.jupiter.api.Test;
import ru.nsu.fit.lispmachine.domain.*;
import ru.nsu.fit.lispmachine.parser.Parser;
import ru.nsu.fit.lispmachine.runtime.*;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @Test
    void testSimpleAddition() {
        Parser parser = new Parser();
        Evaluator evaluator = new Evaluator();
        ExecutionContext context = new ExecutionContext();

        context.define("+", new NativeFunction(args -> {
            long sum = 0;
            for (LispValue arg : args) {
                if (arg instanceof LispNumber num) {
                    sum += num.value();
                }
            }
            return new LispNumber(sum);
        }));

        LispValue program = parser.parse("(+ 1 2 3)");
        LispValue result = evaluator.eval(program, context);

        assertEquals("6", result.toString());
    }
}