package ru.nsu.fit.lispmachine.domain;

public record LispString(String value) implements LispValue {
    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}