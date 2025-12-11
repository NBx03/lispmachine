package ru.nsu.fit.lispmachine;

import org.junit.jupiter.api.Test;
import ru.nsu.fit.lispmachine.domain.LispCons;
import ru.nsu.fit.lispmachine.domain.LispValue;
import ru.nsu.fit.lispmachine.parser.Parser;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testParseNumber() {
        Parser parser = new Parser();
        LispValue result = parser.parse("123");
        assertEquals("123", result.toString());
    }

    @Test
    void testParseList() {
        Parser parser = new Parser();
        LispValue result = parser.parse("(+ 1 2)");
        // Ожидаем строковое представление: (+ 1 2)
        assertEquals("(+ 1 2)", result.toString());
        assertTrue(result instanceof LispCons);
    }

    @Test
    void testNestedList() {
        Parser parser = new Parser();
        LispValue result = parser.parse("(define x (* 2 5))");
        assertEquals("(define x (* 2 5))", result.toString());
    }
}