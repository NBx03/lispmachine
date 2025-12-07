package ru.nsu.fit.lispmachine.domain;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public record LispCons(LispValue head, LispValue tail) implements LispValue {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        LispValue current = this;
        boolean first = true;

        while (current instanceof LispCons cons) {
            if (!first) sb.append(" ");
            sb.append(cons.head);
            current = cons.tail;
            first = false;
        }

        // Если список заканчивается не на Nil (точечная пара), выводим хвост
        if (!(current instanceof LispNil)) {
            sb.append(" . ").append(current);
        }

        sb.append(")");
        return sb.toString();
    }

    // Удобный итератор, чтобы бегать по списку в Java-циклах
    @Override
    public Iterator<LispValue> iterator() {
        List<LispValue> list = new ArrayList<>();
        LispValue current = this;
        while (current instanceof LispCons cons) {
            list.add(cons.head);
            current = cons.tail;
        }
        return list.iterator();
    }
}