package ru.nsu.fit.lispmachine;

import ru.nsu.fit.lispmachine.domain.LispValue;
import ru.nsu.fit.lispmachine.parser.Parser;
import ru.nsu.fit.lispmachine.runtime.Evaluator;
import ru.nsu.fit.lispmachine.runtime.ExecutionContext;
import ru.nsu.fit.lispmachine.runtime.Primitives;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Lisp Machine v1.0. Type (exit) to quit.");

        Parser parser = new Parser();
        Evaluator evaluator = new Evaluator();
        ExecutionContext context = new ExecutionContext();
        Primitives.load(context);

        Scanner scanner = new Scanner(System.in);
        StringBuilder commandBuffer = new StringBuilder();
        int balance = 0; // Баланс скобок: ( +1, ) -1

        System.out.print("> ");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.trim().equals("(exit)")) break;
            if (line.isBlank() && balance == 0) {
                System.out.print("> ");
                continue;
            }

            commandBuffer.append(line).append(" ");
            balance += countBalance(line);

            // Если баланс 0 (или меньше) — значит выражение закончено, выполняем
            // TODO: Реализовать баланс скобок
            }
        }
    }

    // Простой подсчет скобок для REPL
    private static int countBalance(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') count--;
        }
        return count;
    }
}
