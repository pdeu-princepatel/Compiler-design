import java.util.*;

public class LL1 {
    static Map<String, List<String>> grammar = new HashMap<>();
    static Map<String, Set<String>> first = new HashMap<>();
    static Map<String, Set<String>> follow = new HashMap<>();
    static Map<String, Map<String, String>> parseTable = new HashMap<>();
    static String startSymbol = "E";
    static Set<String> terminals = new HashSet<>(Arrays.asList("id", "+", "*", "$", "ε"));
    static Set<String> nonTerminals = new HashSet<>();

    public static void main(String[] args) {
        String grammarStr = "E -> E+E | E*E | id";
        System.out.println("Given grammar:");
        System.out.println(grammarStr);

        grammarStr = removeLeftRecursion(grammarStr);
        System.out.println("\nAfter removal of Left Recursion:");
        System.out.println(grammarStr);

        parseGrammarString(grammarStr);
        
        // This must be called AFTER parsing the grammar string
        initializeNonTerminalsAndTerminals();
        initializeFirstFollow();
        
        computeFirst();
        computeFollow();
        buildParseTable();

        System.out.println("\nFIRST sets:");
        for (String nt : nonTerminals) {
            System.out.println("FIRST(" + nt + ") = " + first.get(nt));
        }

        System.out.println("\nFOLLOW sets:");
        for (String nt : nonTerminals) {
            System.out.println("FOLLOW(" + nt + ") = " + follow.get(nt));
        }
        
        System.out.println("\nParsing Table:");
        List<String> sortedTerminals = new ArrayList<>(terminals);
        sortedTerminals.sort(null);
        List<String> sortedNonTerminals = new ArrayList<>(nonTerminals);
        sortedNonTerminals.sort(null);

        System.out.printf("%-10s", "");
        for (String t : sortedTerminals) {
            if (!t.equals("ε")) {
                System.out.printf("%-10s", t);
            }
        }
        System.out.println();
        
        for (String nt : sortedNonTerminals) {
            System.out.printf("%-10s", nt);
            for (String t : sortedTerminals) {
                if (!t.equals("ε")) {
                    String rule = parseTable.get(nt).get(t);
                    if (rule != null) {
                        System.out.printf("%-10s", nt + "->" + rule);
                    } else {
                        System.out.printf("%-10s", "");
                    }
                }
            }
            System.out.println();
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter input string (tokens separated by space, end with $):");
        String input = sc.nextLine().trim();
        String[] tokens = input.split("\\s+");
        sc.close();

        System.out.println("\nParsing steps:");
        parseInput(tokens);
    }

    static void initializeNonTerminalsAndTerminals() {
        nonTerminals.clear();
        for(String nt : grammar.keySet()) {
            nonTerminals.add(nt);
        }
        
        // Add all terminals from productions
        for (List<String> prods : grammar.values()) {
            for (String prod : prods) {
                String[] symbols = prod.split(" ");
                for (String sym : symbols) {
                    if (!grammar.containsKey(sym) && !sym.equals("ε") && !sym.isEmpty()) {
                        terminals.add(sym);
                    }
                }
            }
        }
    }

    static void parseGrammarString(String grammarStr) {
        grammar.clear();
        String[] lines = grammarStr.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("->");
            String lhs = parts[0].trim();
            String[] rhsProds = parts[1].split("\\|");
            List<String> prods = new ArrayList<>();
            for (String p : rhsProds) {
                prods.add(p.trim());
            }
            grammar.put(lhs, prods);
        }
    }

    static String removeLeftRecursion(String grammar) {
        StringBuilder g1 = new StringBuilder();
        String[] parts = grammar.split("->");
        String nonTerminal = parts[0].trim();
        String[] productions = parts[1].split("\\|");

        List<String> alpha = new ArrayList<>();
        List<String> beta = new ArrayList<>();

        for (String prod : productions) {
            prod = prod.trim();
            if (prod.startsWith(nonTerminal)) {
                alpha.add(prod.substring(nonTerminal.length()).trim());
            } else {
                beta.add(prod);
            }
        }

        if (alpha.isEmpty()) {
            g1.append(grammar);
        } else {
            String newNonTerminal = nonTerminal + "'";
            g1.append(nonTerminal).append(" -> ");
            for (int i = 0; i < beta.size(); i++) {
                g1.append(beta.get(i)).append(" ").append(newNonTerminal);
                if (i != beta.size() - 1) g1.append(" | ");
            }
            g1.append("\n");

            g1.append(newNonTerminal).append(" -> ");
            for (int i = 0; i < alpha.size(); i++) {
                g1.append(alpha.get(i)).append(" ").append(newNonTerminal);
                if (i != alpha.size() - 1) g1.append(" | ");
            }
            g1.append(" | ε");
        }
        return g1.toString();
    }

    static void computeFirst() {
        boolean changed;
        do {
            changed = false;
            for (String lhs : grammar.keySet()) {
                for (String rhs : grammar.get(lhs)) {
                    String[] symbols = rhs.split(" ");
                    int before = first.get(lhs).size();
                    
                    boolean allNullable = true;
                    for (String sym : symbols) {
                        if (sym.isEmpty()) continue;
                        Set<String> firstSet = getFirst(sym);
                        first.get(lhs).addAll(firstSet);
                        
                        if (!firstSet.contains("ε")) {
                            allNullable = false;
                            break;
                        }
                    }
                    if (allNullable && !rhs.equals("ε")) {
                        first.get(lhs).add("ε");
                    }
                    if (rhs.equals("ε")) {
                         first.get(lhs).add("ε");
                    }
                    if (first.get(lhs).size() > before) changed = true;
                }
            }
        } while (changed);
    }

    static void computeFollow() {
        follow.get(startSymbol).add("$");

        boolean changed;
        do {
            changed = false;
            for (String lhs : grammar.keySet()) {
                for (String rhs : grammar.get(lhs)) {
                    String[] symbols = rhs.split(" ");
                    for (int i = 0; i < symbols.length; i++) {
                        String B = symbols[i];
                        if (nonTerminals.contains(B)) {
                            Set<String> followB = follow.get(B);
                            int before = followB.size();

                            boolean allNullable = true;
                            for (int j = i + 1; j < symbols.length; j++) {
                                String beta = symbols[j];
                                if (beta.isEmpty()) continue;
                                Set<String> firstBeta = getFirst(beta);
                                followB.addAll(firstBeta);
                                followB.remove("ε");
                                if (!firstBeta.contains("ε")) {
                                    allNullable = false;
                                    break;
                                }
                            }

                            if (i == symbols.length - 1 || allNullable) {
                                followB.addAll(follow.get(lhs));
                            }

                            if (followB.size() > before) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    static void buildParseTable() {
    for (String nonTerm : nonTerminals) {
        parseTable.put(nonTerm, new HashMap<>());
    }
    
    for (String nonTerm : grammar.keySet()) {
        for (String production : grammar.get(nonTerm)) {
            Set<String> firstSetOfProd = getFirstOfProduction(production);

            for (String terminal : firstSetOfProd) {
                if (!terminal.equals("ε")) {
                    parseTable.get(nonTerm).put(terminal, production);
                }
            }

            if (firstSetOfProd.contains("ε")) {
                for (String followSym : follow.get(nonTerm)) {
                    parseTable.get(nonTerm).put(followSym, production);
                }
            }
        }
    }
}

static Set<String> getFirstOfProduction(String production) {
    Set<String> result = new HashSet<>();
    String[] symbols = production.split(" ");
    
    boolean allNullable = true;
    for (String sym : symbols) {
        if (sym.isEmpty()) continue;
        Set<String> firstSet = first.get(sym);
        if (firstSet == null) {
            // This case should be handled by proper initialization, but
            // this check adds robustness.
            continue;
        }
        result.addAll(firstSet);
        result.remove("ε");
        if (!firstSet.contains("ε")) {
            allNullable = false;
            break;
        }
    }

    if (allNullable) {
        result.add("ε");
    }
    
    if (production.equals("ε")) {
        result.add("ε");
    }
    
    return result;
}

    static void parseInput(String[] tokens) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

        int i = 0;
        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = i < tokens.length ? tokens[i] : "$";
            if (currentToken.isEmpty()) { // Handle empty space from split
                i++;
                continue;
            }

            System.out.printf("\nStack: %-30s Input: %-20s ", stack.toString(), String.join(" ", Arrays.copyOfRange(tokens, i, tokens.length)));

            if (isTerminal(top)) {
                if (top.equals(currentToken)) {
                    System.out.println("Action: match " + currentToken);
                    stack.pop();
                    i++;
                } else {
                    System.out.println("Error: Terminal mismatch. Expected " + top + ", got " + currentToken);
                    return;
                }
            } else { // top is a non-terminal
                String rule = parseTable.get(top).get(currentToken);
                if (rule == null) {
                    System.out.println("Error: No rule for M[" + top + ", " + currentToken + "]");
                    return;
                }

                System.out.println("Action: " + top + " -> " + rule);
                stack.pop();
                if (!rule.equals("ε")) {
                    String[] rhsSymbols = rule.split(" ");
                    for (int j = rhsSymbols.length - 1; j >= 0; j--) {
                        if (!rhsSymbols[j].isEmpty()) {
                            stack.push(rhsSymbols[j]);
                        }
                    }
                }
            }
        }

        if (i == tokens.length) {
            System.out.println("\nInput accepted!");
        } else {
            System.out.println("\nError: Input not fully consumed.");
        }
    }

    static boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }
    
    static Set<String> getFirst(String symbol) {
        Set<String> result = new HashSet<>();
        if (isTerminal(symbol)) {
            result.add(symbol);
        } else {
            result.addAll(first.get(symbol));
        }
        return result;
    }
    
    static void initializeFirstFollow() {
        first.clear();
        follow.clear();

        for (String t : terminals) {
            first.put(t, new HashSet<>(Collections.singleton(t)));
        }

        for (String nt : nonTerminals) {
            first.putIfAbsent(nt, new HashSet<>());
            follow.putIfAbsent(nt, new HashSet<>());
        }
    }

}