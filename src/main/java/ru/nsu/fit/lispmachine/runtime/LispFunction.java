package ru.nsu.fit.lispmachine.runtime;

import ru.nsu.fit.lispmachine.domain.LispValue;
import java.util.List;

public interface LispFunction extends LispValue {
    LispValue apply(List<LispValue> args, ExecutionContext context);
}