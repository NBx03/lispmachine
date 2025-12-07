package ru.nsu.fit.lispmachine.domain;

public record LispNumber(long value) implements LispValue {
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}