package ru.nsu.fit.lispmachine;

import org.junit.jupiter.api.Test;
import ru.nsu.fit.lispmachine.parser.Parser;
import ru.nsu.fit.lispmachine.runtime.*;
import ru.nsu.fit.lispmachine.domain.LispString;

import static org.junit.jupiter.api.Assertions.*;

class MegaTest {
    private final Parser parser = new Parser();
    private final Evaluator evaluator = new Evaluator();
    private final ExecutionContext context = new ExecutionContext();

    public MegaTest() {
        Primitives.load(context);
    }

    @Test
    void testMacro() {
        String code = """
            (defmacro add-one (x) 
                (list (quote +) x 1))
        """;
        evaluator.eval(parser.parse(code), context);
        var res = evaluator.eval(parser.parse("(add-one 10)"), context);
        assertEquals("11", res.toString());
    }

    @Test
    void testWhileAndLet() {
        evaluator.eval(parser.parse("(define i 3)"), context);
        evaluator.eval(parser.parse("(define res 0)"), context);

        String loop = """
            (while i 
                (let ((_ 0)) 
                     (set! res (+ res 10))
                     (set! i (- i 1))
                     (if (= i 0) (set! i false) i)
                ))
        """;
        evaluator.eval(parser.parse(loop), context);
        var res = evaluator.eval(parser.parse("res"), context);
        assertEquals("30", res.toString());
    }

    @Test
    void testTryCatch() {
        String code = "(try (throw \"Boom\") (catch e e))";
        var res = evaluator.eval(parser.parse(code), context);
        assertEquals("\"Boom\"", res.toString());
    }

    @Test
    void testJavaInterop() {
        String code = "(java-call \"java.lang.Math\" \"max\" 10 20)";
        var res = evaluator.eval(parser.parse(code), context);
        assertEquals("20", res.toString());
    }
}