package ru.nsu.fit.lispmachine.parser;

import ru.nsu.fit.lispmachine.domain.*;
import java.util.*;

public class Parser {

    public LispValue parse(String input) {
        if (input == null || input.isBlank()) throw new IllegalArgumentException("Empty input");

        List<String> tokens = tokenize(input);
        Queue<String> tokenQueue = new ArrayDeque<>(tokens);
        return readFrom(tokenQueue);
    }

    // Здесь мы будем учитывать строки и скобки при токенизации
    private List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inString) {
                buffer.append(c);
                if (c == '"') {
                    inString = false;
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
            } else {
                if (c == '"') {
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    }
                    inString = true;
                    buffer.append(c);
                } else if (c == '(' || c == ')') {
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                } else if (Character.isWhitespace(c)) {
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    }
                } else {
                    buffer.append(c);
                }
            }
        }

        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
        }

        return tokens;
    }

    private LispValue readFrom(Queue<String> tokens) {
        if (tokens.isEmpty()) throw new IllegalStateException("Unexpected EOF");
        String token = tokens.poll();

        if ("(".equals(token)) {
            List<LispValue> listBuffer = new ArrayList<>();
            while (!tokens.isEmpty() && !")".equals(tokens.peek())) {
                listBuffer.add(readFrom(tokens));
            }
            if (tokens.isEmpty()) throw new IllegalStateException("Missing ')'");
            tokens.poll();
            return listToCons(listBuffer);
        } else if (")".equals(token)) {
            throw new IllegalStateException("Unexpected ')'");
        } else {
            return parseAtom(token);
        }
    }

    private LispValue parseAtom(String token) {
        try {
            return new LispNumber(Long.parseLong(token));
        } catch (NumberFormatException e) {
            if (token.startsWith("\"")) {
                // Если строка в кавычках - убираем их для хранения
                String val = token;
                if (val.length() >= 2 && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                }
                return new LispString(val);
            }
            return new LispSymbol(token);
        }
    }

    private LispValue listToCons(List<LispValue> list) {
        LispValue current = LispNil.INSTANCE;
        for (int i = list.size() - 1; i >= 0; i--) {
            current = new LispCons(list.get(i), current);
        }
        return current;
    }
}