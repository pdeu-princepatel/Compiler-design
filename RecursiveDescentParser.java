import java.io.*;

public class RecursiveDescentParser {
    static char l;

    public static void main(String[] args) throws IOException {
        System.out.println("Enter expression ending with $ (e.g., i+i*i$):");

        l = (char) System.in.read();
        E();

        if (l == '$') {
            System.out.println("Parsing Successful");
        } else {
            System.out.println("Parsing Failed");
        }
    }

    static void E() throws IOException {
        T();
        E_();
    }

    static void E_() throws IOException {
        if (l == '+') {
            match('+');
            T();
            E_();
        } else if (l == '-') {
            match('-');
            T();
            E_();
        }
    }

    static void T() throws IOException {
        F();
        T_();
    }

    static void T_() throws IOException {
        if (l == '*') {
            match('*');
            F();
            T_();
        } else if (l == '/') {
            match('/');
            F();
            T_();
        }
    }

    static void F() throws IOException {
        if (l == '(') {
            match('(');
            E();
            match(')');
        } else if (l == 'i') {
            match('i');
        } else {
            error("Expected identifier or '('");
        }
    }

    static void match(char t) throws IOException {
        if (l == t) {
            System.out.println("Matched: '" + t + "'");
            l = (char) System.in.read();
            // skip newline or whitespace
            while (l == '\n' || l == '\r' || l == ' ') {
                l = (char) System.in.read();
            }
        } else {
            error("Expected '" + t + "' but found '" + l + "'");
        }
    }

    static void error(String msg) {
        System.out.println("Error: " + msg);
        System.exit(1);
    }
}
