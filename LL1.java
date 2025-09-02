import java.util.*;

public class LL1 {

    static Map<String, List<String>> grammar = new LinkedHashMap<>();
    static Map<String, List<String>> transformedGrammar = new LinkedHashMap<>();
    static Map<String, Set<String>> first = new HashMap<>();
    static Map<String, Set<String>> follow = new HashMap<>();
    static Map<String, Map<String, String>> parsingTable = new HashMap<>();

    static Set<String> terminals = new HashSet<>();
    static Set<String> nonTerminals = new HashSet<>();
    static String startSymbol = "E";

    static {
        // Input grammar (with potential left recursion)
        grammar.put("E", Arrays.asList("E + T", "T"));
        grammar.put("T", Arrays.asList("T * F", "F"));
        grammar.put("F", Arrays.asList("( E )", "id"));

        nonTerminals.addAll(grammar.keySet());
    }

    // Remove immediate left recursion
    static void removeLeftRecursion() {
        for (String A : grammar.keySet()) {
            List<String> alpha = new ArrayList<>();
            List<String> beta = new ArrayList<>();

            for (String production : grammar.get(A)) {
                if (production.startsWith(A + " ")) {
                    alpha.add(production.substring((A + " ").length()));  // remove A from the beginning
                } else {
                    beta.add(production);
                }
            }

            if (!alpha.isEmpty()) {
                String A1 = A + "'";
                nonTerminals.add(A1);
                List<String> newA = new ArrayList<>();
                List<String> newA1 = new ArrayList<>();

                for (String b : beta) {
                    newA.add(b + " " + A1);
                }

                for (String a : alpha) {
                    newA1.add(a + " " + A1);
                }
                newA1.add("ε");

                transformedGrammar.put(A, newA);
                transformedGrammar.put(A1, newA1);
            } else {
                transformedGrammar.put(A, grammar.get(A));
            }
        }

        // Recompute terminals
        for (List<String> productions : transformedGrammar.values()) {
            for (String prod : productions) {
                for (String symbol : prod.split("\\s+")) {
                    if (!symbol.equals("ε") && !transformedGrammar.containsKey(symbol)) {
                        terminals.add(symbol);
                    }
                }
            }
        }
        terminals.add("$");
    }

    // Compute FIRST sets
    static void computeFirst() {
        for (String symbol : nonTerminals) {
            first.put(symbol, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (String nt : nonTerminals) {
                for (String prod : transformedGrammar.get(nt)) {
                    String[] symbols = prod.split("\\s+");
                    boolean nullable = true;

                    for (String sym : symbols) {
                        Set<String> firstSet;
                        if (terminals.contains(sym)) {
                            firstSet = new HashSet<>(Collections.singleton(sym));
                        } else if (sym.equals("ε")) {
                            firstSet = new HashSet<>(Collections.singleton("ε"));
                        } else {
                            firstSet = first.get(sym);
                        }

                        Set<String> targetSet = first.get(nt);
                        int before = targetSet.size();
                        for (String f : firstSet) {
                            if (!f.equals("ε")) {
                                targetSet.add(f);
                            }
                        }

                        if (!firstSet.contains("ε")) {
                            nullable = false;
                            break;
                        }

                        if (targetSet.size() > before) {
                            changed = true;
                        }
                    }

                    if (nullable) {
                        if (first.get(nt).add("ε")) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    // Compute FOLLOW sets
    static void computeFollow() {
        for (String nt : nonTerminals) {
            follow.put(nt, new HashSet<>());
        }
        follow.get(startSymbol).add("$");

        boolean changed;
        do {
            changed = false;
            for (String lhs : transformedGrammar.keySet()) {
                for (String prod : transformedGrammar.get(lhs)) {
                    String[] symbols = prod.split("\\s+");
                    for (int i = 0; i < symbols.length; i++) {
                        String B = symbols[i];
                        if (!nonTerminals.contains(B)) continue;

                        Set<String> trailer = new HashSet<>();
                        boolean nullable = true;
                        for (int j = i + 1; j < symbols.length; j++) {
                            String beta = symbols[j];
                            Set<String> firstBeta = new HashSet<>();
                            if (terminals.contains(beta)) {
                                firstBeta.add(beta);
                                nullable = false;
                            } else {
                                firstBeta.addAll(first.get(beta));
                                if (!firstBeta.contains("ε")) {
                                    nullable = false;
                                }
                                firstBeta.remove("ε");
                            }

                            trailer.addAll(firstBeta);

                            if (!nullable) break;
                        }

                        if (nullable || i == symbols.length - 1) {
                            trailer.addAll(follow.get(lhs));
                        }

                        int before = follow.get(B).size();
                        follow.get(B).addAll(trailer);
                        if (follow.get(B).size() > before) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    // Build LL(1) Parsing Table
    static void buildParsingTable() {
        for (String nt : transformedGrammar.keySet()) {
            parsingTable.put(nt, new HashMap<>());
            for (String prod : transformedGrammar.get(nt)) {
                Set<String> firstSet = firstOfProduction(prod);
                for (String terminal : firstSet) {
                    if (!terminal.equals("ε")) {
                        parsingTable.get(nt).put(terminal, prod);
                    }
                }
                if (firstSet.contains("ε")) {
                    for (String f : follow.get(nt)) {
                        parsingTable.get(nt).put(f, prod);
                    }
                }
            }
        }
    }

    static Set<String> firstOfProduction(String prod) {
        Set<String> result = new HashSet<>();
        String[] symbols = prod.split("\\s+");
        boolean nullable = true;

        for (String sym : symbols) {
            Set<String> firstSet;
            if (terminals.contains(sym)) {
                firstSet = new HashSet<>(Collections.singleton(sym));
            } else if (sym.equals("ε")) {
                firstSet = new HashSet<>(Collections.singleton("ε"));
            } else {
                firstSet = first.get(sym);
            }

            result.addAll(firstSet);
            if (!firstSet.contains("ε")) {
                nullable = false;
                break;
            }
            result.remove("ε");
        }

        if (nullable) result.add("ε");
        return result;
    }

    public static void printSetMap(String title, Map<String, Set<String>> map) {
        System.out.println("\n" + title + ":");
        for (String key : map.keySet()) {
            System.out.println(key + " = " + map.get(key));
        }
    }

    public static void printParsingTable() {
        List<String> sortedTerminals = new ArrayList<>(terminals);
        Collections.sort(sortedTerminals);
        List<String> sortedNonTerminals = new ArrayList<>(nonTerminals);
        Collections.sort(sortedNonTerminals);

        System.out.println("\nLL(1) Parsing Table:");
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
    }

    public static boolean parse(String input) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

        input = input.trim() + " $";
        String[] tokens = input.split("\\s+");
        int index = 0;

        System.out.printf("\n%-30s %-30s %s%n", "Stack", "Input", "Action");
        System.out.println("-------------------------------------------------------------");

        while (true) {
            String top = stack.peek();
            String currentToken = tokens[index];

            System.out.printf("%-30s %-30s ", stack, Arrays.toString(Arrays.copyOfRange(tokens, index, tokens.length)));

            if (terminals.contains(top) || top.equals("$")) {
                if (top.equals(currentToken)) {
                    if (top.equals("$")) {
                        System.out.println("Match $ → Input accepted.");
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

                System.out.println("Apply " + top + " → " + production);
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
        removeLeftRecursion();
        computeFirst();
        computeFollow();
        buildParsingTable();

        System.out.println("Grammar after removing left recursion:");
        for (Map.Entry<String, List<String>> entry : transformedGrammar.entrySet()) {
            System.out.println(entry.getKey() + " -> " + String.join(" | ", entry.getValue()));
        }

        printSetMap("FIRST Sets", first);
        printSetMap("FOLLOW Sets", follow);
        printParsingTable();

        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter input string (tokens separated by space, e.g., id + id * id):");
        String input = sc.nextLine();

        if (parse(input)) {
            // Already printed
        } else {
            System.out.println("Input rejected.");
        }

        sc.close();
    }
}
