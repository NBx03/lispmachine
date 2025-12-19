package ru.nsu.fit.lispmachine.runtime;

import ru.nsu.fit.lispmachine.domain.LispValue;
import java.util.List;

// Макрос хранит параметры и тело, как функция, но помечается отдельно
public record LispMacro(List<String> params, LispValue body) implements LispFunction {
    @Override
    public LispValue apply(List<LispValue> args, ExecutionContext context) {
        // Логика вызова макроса происходит внутри Evaluator,
        // здесь метод apply может использоваться для "раскрытия" (expansion)
        return null;
    }

    @Override
    public String toString() {
        return "<macro>";
    }
}