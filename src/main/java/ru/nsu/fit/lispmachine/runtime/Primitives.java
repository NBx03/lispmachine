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

        context.define("read", new NativeFunction(args -> {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                return new Parser().parse(line);
            }
            return LispNil.INSTANCE;
        }));

        context.define("list", new NativeFunction(args -> {
            LispValue current = LispNil.INSTANCE;
            for (int i = args.size() - 1; i >= 0; i--) {
                current = new LispCons(args.get(i), current);
            }
            return current;
        }));

        context.define("cons", new NativeFunction(args -> {
            return new LispCons(args.get(0), args.get(1));
        }));

        context.define("car", new NativeFunction(args -> {
            if (args.get(0) instanceof LispCons c) return c.head();
            return LispNil.INSTANCE;
        }));

        context.define("cdr", new NativeFunction(args -> {
            if (args.get(0) instanceof LispCons c) return c.tail();
            return LispNil.INSTANCE;
        }));

        context.define("<", new NativeFunction(args -> {
            long v1 = ((LispNumber)args.get(0)).value();
            long v2 = ((LispNumber)args.get(1)).value();
            // Возвращаем T если меньше, иначе Nil (False)
            return v1 < v2 ? new LispSymbol("T") : LispNil.INSTANCE;
        }));

        context.define(">", new NativeFunction(args -> {
            long v1 = ((LispNumber)args.get(0)).value();
            long v2 = ((LispNumber)args.get(1)).value();
            return v1 > v2 ? new LispSymbol("T") : LispNil.INSTANCE;
        }));

        context.define("java-call", new NativeFunction(args -> {
            try {
                String className = getString(args.get(0));
                String methodName = getString(args.get(1));

                Class<?> clazz = Class.forName(className);
                Object[] javaArgs = new Object[args.size() - 2];
                Class<?>[] paramTypes = new Class<?>[args.size() - 2];

                for(int i=2; i<args.size(); i++) {
                    if (args.get(i) instanceof LispNumber n) {
                        javaArgs[i-2] = n.value();
                        paramTypes[i-2] = long.class;
                    } else {
                        javaArgs[i-2] = getString(args.get(i));
                        paramTypes[i-2] = String.class;
                    }
                }

                Method method = clazz.getMethod(methodName, paramTypes);
                Object result = method.invoke(null, javaArgs);

                if (result instanceof Number n) return new LispNumber(n.longValue());
                return new LispString(result.toString());

            } catch (Exception e) {
                throw new RuntimeException("Java Interop Error: " + e.getMessage());
            }
        }));
    }
}