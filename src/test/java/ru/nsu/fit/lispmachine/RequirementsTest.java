package ru.nsu.fit.lispmachine;

import org.junit.jupiter.api.Test;
import ru.nsu.fit.lispmachine.domain.*;
import ru.nsu.fit.lispmachine.parser.Parser;
import ru.nsu.fit.lispmachine.runtime.*;

import static org.junit.jupiter.api.Assertions.*;

class RequirementsTest {
    private final Parser parser = new Parser();
    private final Evaluator evaluator = new Evaluator();
    private final ExecutionContext context = new ExecutionContext();

    @Test
    void testLet() {
        // (let ((x 10)) (+ x 5)) -> 15
        Primitives.load(context);
        LispValue result = evaluator.eval(parser.parse("(let ((x 10)) (+ x 5))"), context);
        assertEquals("15", result.toString());
    }

    @Test
    void testSet() {
        // (define x 10) (set! x 20) x -> 20
        evaluator.eval(parser.parse("(define x 10)"), context);
        evaluator.eval(parser.parse("(set! x 20)"), context);
        LispValue result = evaluator.eval(parser.parse("x"), context);
        assertEquals("20", result.toString());
    }

    @Test
    void testLogicAndOr() {
        // (and T T) -> T
        LispValue res1 = evaluator.eval(parser.parse("(and T T)"), context);
        assertEquals("T", res1.toString());

        // (or T (throw "Should not happen")) -> T
        LispValue res2 = evaluator.eval(parser.parse("(or T (throw 666))"), context);
        assertEquals("T", res2.toString());
    }

    @Test
    void testException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            evaluator.eval(parser.parse("(throw 123)"), context);
        });
        assertTrue(ex.getMessage().contains("123"));
    }
}