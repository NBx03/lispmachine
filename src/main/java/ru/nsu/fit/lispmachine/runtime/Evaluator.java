package ru.nsu.fit.lispmachine.runtime;

import ru.nsu.fit.lispmachine.domain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Evaluator {

    public LispValue eval(LispValue value, ExecutionContext context) {
        try {
            // Атомы
            if (value instanceof LispNumber) return value;
            if (value instanceof LispString) return value;
            if (value instanceof LispNil) return value;
            if (value instanceof LispFunction) return value;

            if (value instanceof LispSymbol symbol) {
                if (symbol.name().equals("true") || symbol.name().equals("T")) return new LispSymbol("T");
                if (symbol.name().equals("false")) return LispNil.INSTANCE;

                LispValue val = context.lookup(symbol.name());
                if (val == null) throw new RuntimeException("Symbol not defined: " + symbol.name());
                return val;
            }

            // Списки
            if (value instanceof LispCons cons) {
                LispValue head = cons.head();

                if (head instanceof LispSymbol sym) {
                    LispValue potentialMacro = context.lookup(sym.name());
                    if (potentialMacro instanceof LispMacro macro) {
                        List<LispValue> rawArgs = toList(cons.tail());
                        LispValue expanded = applyMacro(macro, rawArgs, context);
                        return eval(expanded, context);
                    }

                    switch (sym.name()) {
                        case "quote": return handleQuote(cons);
                        case "define": return handleDefine(cons, context);
                        case "defmacro": return handleDefMacro(cons, context);
                        case "set!": return handleSet(cons, context);
                        case "if": return handleIf(cons, context);
                        case "lambda": return handleLambda(cons, context);
                        case "let": return handleLet(cons, context);
                        case "binding": return handleBinding(cons, context);
                        case "while": return handleWhile(cons, context);
                        case "throw": return handleThrow(cons, context);
                        case "try": return handleTry(cons, context);
                        case "and": return handleAnd(cons, context);
                        case "or": return handleOr(cons, context);
                    }
                }

                LispValue evaledHead = eval(head, context);
                if (evaledHead instanceof LispFunction function) {
                    List<LispValue> args = new ArrayList<>();
                    LispValue current = cons.tail();
                    while (current instanceof LispCons argCons) {
                        args.add(eval(argCons.head(), context));
                        current = argCons.tail();
                    }
                    return function.apply(args, context);
                } else {
                    throw new RuntimeException("Attempt to call non-function: " + evaledHead);
                }
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Прочие формы

    private LispValue evalBody(LispValue bodyNodes, ExecutionContext context) {
        LispValue result = LispNil.INSTANCE;
        if (bodyNodes instanceof Iterable) {
            for (LispValue expr : (Iterable<LispValue>) bodyNodes) {
                result = eval(expr, context);
            }
        } else {
            result = eval(bodyNodes, context);
        }
        return result;
    }

    private LispValue handleQuote(LispCons cons) {
        return ((LispCons) cons.tail()).head();
    }

    private LispValue handleDefine(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispSymbol name = (LispSymbol) parts.get(0);
        LispValue res = eval(parts.get(1), context);
        context.define(name.name(), res);
        return name;
    }

    private LispValue handleDefMacro(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispSymbol name = (LispSymbol) parts.get(0);
        List<String> params = extractParams(parts.get(1));
        LispValue body = parts.get(2);
        context.define(name.name(), new LispMacro(params, body));
        return name;
    }

    private LispValue applyMacro(LispMacro macro, List<LispValue> args, ExecutionContext context) {
        ExecutionContext macroCtx = new ExecutionContext(context);
        for(int i=0; i<macro.params().size(); i++) {
            macroCtx.define(macro.params().get(i), args.get(i));
        }
        return eval(macro.body(), macroCtx);
    }

    private LispValue handleSet(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispSymbol name = (LispSymbol) parts.get(0);
        LispValue res = eval(parts.get(1), context);
        context.update(name.name(), res);
        return res;
    }

    private LispValue handleIf(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispValue cond = eval(parts.get(0), context);
        if (!(cond instanceof LispNil)) {
            return eval(parts.get(1), context);
        } else {
            return parts.size() > 2 ? eval(parts.get(2), context) : LispNil.INSTANCE;
        }
    }

    private LispValue handleWhile(LispCons cons, ExecutionContext context) {
        LispValue args = cons.tail();
        if (!(args instanceof LispCons argsCons)) return LispNil.INSTANCE;
        LispValue condExpr = argsCons.head();
        LispValue bodyExprs = argsCons.tail();

        LispValue lastResult = LispNil.INSTANCE;
        while(!(eval(condExpr, context) instanceof LispNil)) {
            lastResult = evalBody(bodyExprs, context);
        }
        return lastResult;
    }

    private LispValue handleTry(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispValue tryBody = parts.get(0);
        LispValue catchBlock = parts.size() > 1 ? parts.get(1) : null;
        try {
            return eval(tryBody, context);
        } catch (RuntimeException e) {
            if (catchBlock instanceof LispCons catchCons) {
                List<LispValue> catchParts = toList(catchCons);
                if (catchParts.size() == 3 && ((LispSymbol)catchParts.get(0)).name().equals("catch")) {
                    String exVar = ((LispSymbol)catchParts.get(1)).name();
                    LispValue handlerBody = catchParts.get(2);
                    ExecutionContext catchCtx = new ExecutionContext(context);
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();

                    if (msg != null && msg.startsWith("Lisp Error: ")) {
                        msg = msg.substring(12);
                    }

                    if (msg != null && msg.startsWith("\"") && msg.endsWith("\"")) {
                        msg = msg.substring(1, msg.length() - 1);
                    }

                    catchCtx.define(exVar, new LispString(msg == null ? "error" : msg));
                    return eval(handlerBody, catchCtx);
                }
            }
            throw e;
        }
    }

    private LispValue handleBinding(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispValue bindings = parts.get(0);
        LispValue body = parts.get(1);
        Map<String, LispValue> oldValues = new HashMap<>();
        List<String> boundNames = new ArrayList<>();

        if (bindings instanceof Iterable) {
            for (LispValue pair : (Iterable<LispValue>) bindings) {
                if (pair instanceof LispCons p) {
                    String name = ((LispSymbol) p.head()).name();
                    LispValue newVal = eval(((LispCons) p.tail()).head(), context);
                    LispValue oldVal = context.lookup(name);
                    if (oldVal != null) oldValues.put(name, oldVal);
                    boundNames.add(name);
                    context.define(name, newVal);
                }
            }
        }
        try {
            return eval(body, context);
        } finally {
            for (String name : boundNames) {
                if (oldValues.containsKey(name)) {
                    context.define(name, oldValues.get(name));
                }
            }
        }
    }

    private LispValue handleLambda(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispValue params = parts.get(0);
        LispValue body = ((LispCons)cons.tail()).tail();
        return new UserDefinedFunction(extractParams(params), body, context);
    }

    private LispValue handleLet(LispCons cons, ExecutionContext context) {
        List<LispValue> parts = toList(cons.tail());
        LispValue bindings = parts.get(0);
        LispValue body = ((LispCons)cons.tail()).tail();
        ExecutionContext newContext = new ExecutionContext(context);
        if (bindings instanceof Iterable) {
            for (LispValue binding : (Iterable<LispValue>) bindings) {
                if (binding instanceof LispCons pair) {
                    LispSymbol varName = (LispSymbol) pair.head();
                    LispValue varVal = eval(((LispCons) pair.tail()).head(), context);
                    newContext.define(varName.name(), varVal);
                }
            }
        }
        return evalBody(body, newContext);
    }

    private LispValue handleAnd(LispCons cons, ExecutionContext context) {
        LispValue current = cons.tail();
        LispValue lastResult = new LispSymbol("T");
        while (current instanceof LispCons c) {
            lastResult = eval(c.head(), context);
            if (lastResult instanceof LispNil) return LispNil.INSTANCE;
            current = c.tail();
        }
        return lastResult;
    }

    private LispValue handleOr(LispCons cons, ExecutionContext context) {
        LispValue current = cons.tail();
        while (current instanceof LispCons c) {
            LispValue result = eval(c.head(), context);
            if (!(result instanceof LispNil)) return result;
            current = c.tail();
        }
        return LispNil.INSTANCE;
    }

    private LispValue handleThrow(LispCons cons, ExecutionContext context) {
        LispValue msg = eval(((LispCons) cons.tail()).head(), context);
        throw new RuntimeException("Lisp Error: " + msg.toString());
    }

    private List<String> extractParams(LispValue paramsVal) {
        List<String> paramNames = new ArrayList<>();
        if (paramsVal instanceof Iterable) {
            for (LispValue p : (Iterable<LispValue>) paramsVal) {
                if (p instanceof LispSymbol s) paramNames.add(s.name());
            }
        }
        return paramNames;
    }

    private List<LispValue> toList(LispValue list) {
        List<LispValue> result = new ArrayList<>();
        if (list instanceof Iterable) {
            for (LispValue v : (Iterable<LispValue>) list) result.add(v);
        }
        return result;
    }
}