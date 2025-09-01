import java.util.*;

public class LL1Parser {
    // Parsing table for grammar after removing left recursion
    // E  -> id E'
    // E' -> + E E' | * E E' | ε

    static Map<String, Map<String, String>> parsingTable = new HashMap<>();
    static Set<String> terminals = new HashSet<>(Arrays.asList("id", "+", "*", ")", "$"));
    static Set<String> nonTerminals = new HashSet<>(Arrays.asList("E", "E'"));

    static {
        parsingTable.put("E", new HashMap<>());
        parsingTable.get("E").put("id", "id E'");

        parsingTable.put("E'", new HashMap<>());
        parsingTable.get("E'").put("+", "+ E E'");
        parsingTable.get("E'").put("*", "* E E'");
        parsingTable.get("E'").put(")", "ε");
        parsingTable.get("E'").put("$", "ε");
    }

    public static boolean parse(String input) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push("E");

        input = input.trim() + " $";
        String[] tokens = input.split("\\s+");
        int index = 0;

        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = tokens[index];

            if (top.equals("$")) {
                if (currentToken.equals("$")) {
                    System.out.println("Input accepted.");
                    return true;
                } else {
                    System.out.println("Error: Stack end reached but input remains at token '" + currentToken + "'");
                    return false;
                }
            }

            if (terminals.contains(top)) {
                if (top.equals(currentToken)) {
                    stack.pop();
                    index++;
                } else {
                    System.out.println("Error: Unexpected token '" + currentToken + "', expected '" + top + "'");
                    return false;
                }
            } else if (nonTerminals.contains(top)) {
                String production = parsingTable.getOrDefault(top, Collections.emptyMap()).get(currentToken);
                if (production == null) {
                    System.out.println("Error: No rule for [" + top + ", " + currentToken + "]");
                    return false;
                }

                stack.pop();
                if (!production.equals("ε")) {
                    String[] symbols = production.split("\\s+");
                    for (int i = symbols.length - 1; i >= 0; i--) {
                        stack.push(symbols[i]);
                    }
                }
            } else {
                System.out.println("Error: Invalid symbol on stack: " + top);
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String grammar = "E -> E+E | E*E | id";
        System.out.println("Original Grammar: " + grammar);
        System.out.println("Transformed Grammar (no left recursion):");
        System.out.println("E  -> id E'");
        System.out.println("E' -> + E E' | * E E' | ε\n");

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter input string (tokens separated by space, e.g., id + id * id):");
        String input = sc.nextLine();

        boolean result = parse(input);
        if (!result) {
            System.out.println("Input rejected.");
        }
    }
}
