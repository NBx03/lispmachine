package ru.nsu.fit.lispmachine;

import org.junit.jupiter.api.Test;
import ru.nsu.fit.lispmachine.domain.LispValue;
import ru.nsu.fit.lispmachine.parser.Parser;
import ru.nsu.fit.lispmachine.runtime.*;

import static org.junit.jupiter.api.Assertions.*;

class FullTest {
    @Test
    void testRecursionFactorial() {
        Parser parser = new Parser();
        Evaluator evaluator = new Evaluator();
        ExecutionContext context = new ExecutionContext();
        Primitives.load(context);

        String code = """
            (define fact 
                (lambda (n) 
                    (if (= n 0) 
                        1 
                        (* n (fact (- n 1))))))
        """;

        evaluator.eval(parser.parse(code), context);

        LispValue result = evaluator.eval(parser.parse("(fact 5)"), context);

        assertEquals("120", result.toString());
    }
}