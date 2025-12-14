package ru.nsu.fit.lispmachine.runtime;

import ru.nsu.fit.lispmachine.domain.*;
import ru.nsu.fit.lispmachine.parser.Parser;

import java.util.Objects;
import java.util.Scanner;
import java.lang.reflect.Method;

public class Primitives {

    private static String getString(LispValue v) {
        if (v instanceof LispString s) return s.value();
        if (v instanceof LispSymbol s) return s.name();
        return v.toString();
    }

    public static void load(ExecutionContext context) {
        context.define("+", new NativeFunction(args -> {
            long sum = 0;
            for (LispValue arg : args) sum += ((LispNumber) arg).value();
            return new LispNumber(sum);
        }));
        context.define("-", new NativeFunction(args -> {
            long res = ((LispNumber) args.get(0)).value();
            for (int i = 1; i < args.size(); i++) res -= ((LispNumber) args.get(i)).value();
            return new LispNumber(res);
        }));
        context.define("*", new NativeFunction(args -> {
            long res = 1;
            for (LispValue arg : args) res *= ((LispNumber) arg).value();
            return new LispNumber(res);
        }));
        context.define("=", new NativeFunction(args -> {
            LispValue v1 = args.get(0);
            LispValue v2 = args.get(1);
            return Objects.equals(v1.toString(), v2.toString()) ? new LispSymbol("T") : LispNil.INSTANCE;
        }));
        context.define("print", new NativeFunction(args -> {
            for(LispValue arg : args) System.out.print(arg + " ");
            System.out.println();
            return LispNil.INSTANCE;
        }));

        

        

        

        

        

        

        

        
    }
}