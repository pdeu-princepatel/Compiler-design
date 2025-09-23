import java.util.*;

public class OperatorPrecedenceParser {

    static class Production {
        String lhs;
        List<String> rhs;

        Production(String lhs, List<String> rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    static List<Production> productions = new ArrayList<>();
    static Set<String> nonTerminals = new HashSet<>();
    static Set<String> terminals = new HashSet<>();

    static String[] terminalList = { "+", "*", "id", "(", ")", "$" };
    static Map<String, Integer> terminalIndex = new HashMap<>();

    // Manually defined operator precedence table
    static String[][] precedenceTable = {
            // + * id ( ) $
            { ">", "<", "<", "<", ">", ">" }, // +
            { ">", ">", "<", "<", ">", ">" }, // *
            { ">", ">", "", "", ">", ">" }, // id
            { "<", "<", "<", "<", "=", "" }, // (
            { ">", ">", "", "", ">", ">" }, // )
            { "<", "<", "<", "<", "", "acc" } // $
    };

    public static void main(String[] args) {
        defineGrammar();
        classifySymbols();
        fillTerminalIndices();

        System.out.println("--- Grammar Analysis ---");
        if (isOperatorPrecedenceGrammar()) {
            System.out.println("Grammar is Operator Precedence Grammar.\n");
            printPrecedenceTable();

            System.out.println("\n--- Parsing Expression ---");
            parseExpression("id + id * id");
        } else {
            System.out.println("Grammar is NOT Operator Precedence Grammar.");
        }
    }

    static void defineGrammar() {
        addProduction("E", Arrays.asList("E", "+", "E"));
        addProduction("E", Arrays.asList("E", "*", "E"));
        addProduction("E", Arrays.asList("(", "E", ")"));
        // addProduction("E", Arrays.asList("E", "E"));
        addProduction("E", Arrays.asList("id"));
    }

    static void addProduction(String lhs, List<String> rhs) {
        productions.add(new Production(lhs, rhs));
        nonTerminals.add(lhs);
    }

    static void classifySymbols() {
        for (Production p : productions) {
            for (String symbol : p.rhs) {
                if (!nonTerminals.contains(symbol)) {
                    terminals.add(symbol);
                }
            }
        }
        terminals.add("$");
    }

    static void fillTerminalIndices() {
        for (int i = 0; i < terminalList.length; i++) {
            terminalIndex.put(terminalList[i], i);
        }
    }

    static boolean isOperatorPrecedenceGrammar() {
        for (Production p : productions) {
            for (int i = 0; i < p.rhs.size() - 1; i++) {
                String A = p.rhs.get(i);
                String B = p.rhs.get(i + 1);
                if (nonTerminals.contains(A) && nonTerminals.contains(B)) {
                    System.out.println("Error: Adjacent non-terminals (" + A + " " + B + ")");
                    return false;
                }
            }
        }
        return true;
    }

    static void printPrecedenceTable() {
        System.out.println("\n Operator Precedence Table:\n");
        System.out.print("     ");
        for (String col : terminalList) {
            System.out.printf("%4s", col);
        }
        System.out.println();

        for (int i = 0; i < terminalList.length; i++) {
            System.out.printf("%4s", terminalList[i]);
            for (int j = 0; j < terminalList.length; j++) {
                String rel = precedenceTable[i][j];
                System.out.printf("%4s", (rel == null || rel.isEmpty()) ? " " : rel);
            }
            System.out.println();
        }
    }

    static String getRelation(String a, String b) {
        Integer i = terminalIndex.get(a);
        Integer j = terminalIndex.get(b);
        if (i == null || j == null)
            return null;
        return precedenceTable[i][j];
    }

    static void parseExpression(String expr) {
        System.out.println("\nParsing: " + expr);
        List<String> input = new ArrayList<>(Arrays.asList(expr.split(" ")));
        input.add("$");

        Stack<String> stack = new Stack<>();
        stack.push("$");

        int i = 0;

        while (true) {
            String a = input.get(i);
            String top = getTopTerminal(stack);
            String rel = getRelation(top, a);

            System.out.println("Stack: " + stack + "\tInput: " + input.subList(i, input.size()) + "\tRelation: " + rel);

            if (rel == null || rel.isEmpty()) {
                System.out.println(" Error: No precedence relation between '" + top + "' and '" + a + "'");
                return;
            }

            if (rel.equals("<") || rel.equals("=")) {
                stack.push(a);
                i++;
            } else if (rel.equals(">")) {
                reduce(stack);
            } else if (rel.equals("acc")) {
                if (stack.size() == 2 && stack.peek().equals("E")) {
                    System.out.println(" Accepted!");
                } else {
                    System.out.println(" Not accepted — stack not in valid accept state.");
                }
                return;
            }
        }
    }

    static String getTopTerminal(Stack<String> stack) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (terminals.contains(stack.get(i))) {
                return stack.get(i);
            }
        }
        return "$";
    }

    static void reduce(Stack<String> stack) {
        if (stack.isEmpty()) {
            System.out.println(" Reduction failed: Stack is empty.");
            return;
        }

        // Case: id -> E
        if (stack.peek().equals("id")) {
            stack.pop();
            stack.push("E");
            return;
        }

        // Case: ( E ) -> E
        if (stack.size() >= 3 &&
                stack.get(stack.size() - 3).equals("(") &&
                stack.get(stack.size() - 2).equals("E") &&
                stack.get(stack.size() - 1).equals(")")) {
            stack.pop();
            stack.pop();
            stack.pop();
            stack.push("E");
            return;
        }

        // Case: E + E or E * E -> E
        if (stack.size() >= 3) {
            String right = stack.pop();
            String op = stack.pop();
            String left = stack.pop();
            if (left.equals("E") && right.equals("E") && (op.equals("+") || op.equals("*"))) {
                stack.push("E");
                return;
            } else {
                System.out.println(" Invalid reduction: " + left + " " + op + " " + right);
                System.exit(1);
            }
        }

        System.out.println(" Error: No valid reduction found.");
        System.exit(1);
    }
}
