import java.util.*;

public class LL1 {

    public static String removeLeftRecursion(String grammar) {
        StringBuilder g1 = new StringBuilder();

        String[] parts = grammar.split("->");
        String nonTerminal = parts[0].trim();
        String[] productions = parts[1].split("\\|");

        List<String> alpha = new ArrayList<>();
        List<String> beta = new ArrayList<>();

        for (String prod : productions) {
            prod = prod.trim();
            if (prod.startsWith(nonTerminal)) {
                alpha.add(prod.substring(nonTerminal.length()));
            } else {
                beta.add(prod);
            }
        }

        if (alpha.isEmpty()) {
            System.out.println("No left recursion in the grammar.");
            g1.append(grammar);
        } else {
            String newNonTerminal = nonTerminal + "'";
            g1.append(nonTerminal + "->");
            for (int i = 0; i < beta.size(); i++) {
                g1.append(beta.get(i) + newNonTerminal);
                if (i != beta.size() - 1)
                    g1.append("|");
            }
            g1.append("\n");

            g1.append(newNonTerminal + " -> ");
            for (int i = 0; i < alpha.size(); i++) {
                g1.append(alpha.get(i) + newNonTerminal);
                if (i != alpha.size() - 1)
                    g1.append("|");
            }
            g1.append("| epsilon");

        }
        return g1.toString();
    }

    public static void removeLeftFactoring(String grammar) {
        StringBuilder g2 = new StringBuilder();
        String[] parts = grammar.split("->");
        String nonTerminal = parts[0].trim();
        String[] productions = parts[1].split("\\|");
    }

    public static void main(String[] args) {
        // Scanner scanner = new Scanner(System.in);
        // System.out.println("Enter Grammar (e.g., E -> E+E | E*E | id): ");
        // String grammar = scanner.nextLine();
        String grammar = "E -> E+E | E*E | id";
        grammar = removeLeftRecursion(grammar);
        System.err.println(grammar);
        // grammar = removeLeftFactoring(grammar);
    }
}
