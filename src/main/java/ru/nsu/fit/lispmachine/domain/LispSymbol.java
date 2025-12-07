package ru.nsu.fit.lispmachine.domain;

public record LispSymbol(String name) implements LispValue {
    @Override
    public String toString() {
        return name;
    }
}