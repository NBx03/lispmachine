package ru.nsu.fit.lispmachine.parser;
import ru.nsu.fit.lispmachine.domain.*;
import java.util.*;
public class Parser {
    public LispValue parse(String input) {
        // TODO: Реализовать нормальный разбор строк позже
        String[] tokens = input.replace("(", " ( ").replace(")", " ) ").trim().split("\\s+");
        return readFrom(new ArrayDeque<>(Arrays.asList(tokens)));
    }
    private LispValue readFrom(Queue<String> tokens) {
        String token = tokens.poll();
        if ("(".equals(token)) {
            List<LispValue> list = new ArrayList<>();
            while (!")".equals(tokens.peek())) list.add(readFrom(tokens));
            tokens.poll();
            return new LispCons(list.get(0), LispNil.INSTANCE); 
        }
        return new LispSymbol(token);
    }
}
