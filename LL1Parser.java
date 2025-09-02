import java.util.*;

public class LL1Parser {
    static Map<String, Map<String, String>> parsingTable = new HashMap<>();
    static Set<String> terminals = new HashSet<>(Arrays.asList("id", "+", "*", ")","(", "$"));
    static Set<String> nonTerminals = new HashSet<>(Arrays.asList("E", "E'"));
    static String startSymbol = "E";

    static {
       
        parsingTable.put("E", new HashMap<>());
        parsingTable.get("E").put("id", "T E'");
        parsingTable.get("E").put("(", "T E'");

        parsingTable.put("E'", new HashMap<>());
        parsingTable.get("E'").put("+", "+ T E'");
        parsingTable.get("E'").put(")", "ε");
        parsingTable.get("E'").put("$", "ε");

        parsingTable.put("T", new HashMap<>());
        parsingTable.get("T").put("id", "F T'");
        parsingTable.get("T").put("(", "F T'");

        parsingTable.put("T'", new HashMap<>());
        parsingTable.get("T'").put(")", "ε");
        parsingTable.get("T'").put("+", "ε");
        parsingTable.get("T'").put("*", "* F T'");
        parsingTable.get("T'").put("$", "ε");

        parsingTable.put("F", new HashMap<>());
        parsingTable.get("F").put("id", "id");
        parsingTable.get("F").put("(", "( E )");

        // The original provided grammar in the code comment is inconsistent.
        // The code here now uses the standard expression grammar to produce a correct table.
        // Original grammar: E -> T E', E' -> + T E' | ε, T -> F T', T' -> * F T' | ε, F -> id | ( E )
        
        terminals = new HashSet<>(Arrays.asList("id", "+", "*", "(", ")", "$"));
        nonTerminals = new HashSet<>(Arrays.asList("E", "E'", "T", "T'", "F"));

    }

    public static void printParsingTable() {
        List<String> sortedTerminals = new ArrayList<>(terminals);
        Collections.sort(sortedTerminals);
        List<String> sortedNonTerminals = new ArrayList<>(nonTerminals);
        Collections.sort(sortedNonTerminals);

        System.out.println("\nCorrect Parsing Table:");
        System.out.printf("%-10s", "");
        for (String t : sortedTerminals) {
            System.out.printf("%-15s", t);
        }
        System.out.println();
        
        for (String nt : sortedNonTerminals) {
            System.out.printf("%-10s", nt);
            for (String t : sortedTerminals) {
                String rule = parsingTable.getOrDefault(nt, Collections.emptyMap()).get(t);
                if (rule != null) {
                    System.out.printf("%-15s", nt + "->" + rule);
                } else {
                    System.out.printf("%-15s", "");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static boolean parse(String input) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

        input = input.trim() + " $";
        String[] tokens = input.split("\\s+");
        int index = 0;

        System.out.printf("%-20s %-20s %s%n", "Stack", "Input", "Action");
        System.out.println("-------------------------------------------------------");

        while (true) {
            String top = stack.peek();
            String currentToken = tokens[index];

            System.out.printf("%-20s %-20s ", stack, Arrays.toString(Arrays.copyOfRange(tokens, index, tokens.length)));

            if (terminals.contains(top) || top.equals("$")) {
                if (top.equals(currentToken)) {
                    if (top.equals("$")) {
                        System.out.println("Match $ -> Input accepted.");
                        return true;
                    }
                    System.out.println("Match " + top);
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

                System.out.println("Apply " + top + " -> " + production);
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
    }

    public static void main(String[] args) {
        System.out.println("Original Grammar: E -> E+E | E*E | id");
        System.out.println("Transformed Grammar (no left recursion):");
        System.out.println("E -> T E'");
        System.out.println("E' -> + T E' | ε");
        System.out.println("T -> F T'");
        System.out.println("T' -> * F T' | ε");
        System.out.println("F -> id | ( E )");

        printParsingTable();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter input string (tokens separated by space, e.g., id + id * id):");
        String input = sc.nextLine();

        if (parse(input)) {
            // Nothing to do, result is already printed
        } else {
            System.out.println("Input rejected.");
        }
        sc.close();
    }
}