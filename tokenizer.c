#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

FILE *ptr;

const char *keywords[] = {"int", "float", "bool", "void", "char", "printf", "scanf", "NULL", "return", "if", "else", "while", "do", "for"};
const char symbols[] = {'~', '!', '(', ')', '*', '&', '{', '}', '\\', '?', ',', '.', '/', '+', '-', '_', '^', ';', ':', '|', '%', '$', '#', '@', '`', '=', '[', ']', '<', '>'};
int kcount = 0, icount = 0, scount = 0, ncount = 0, strcount = 0, unknown = 0;

// Check if token is a keyword
int isKeyword(const char *val) {
    for (int i = 0; i < sizeof(keywords)/sizeof(keywords[0]); i++) {
        if (strcmp(val, keywords[i]) == 0) {
            printf("%s  <-- Keyword\n", val);
            kcount++;
            return 1;
        }
    }
    return 0;
}

// Check if token is a symbol
int isSymbol(char ch) {
    for (int i = 0; i < sizeof(symbols); i++) {
        if (ch == symbols[i]) {
            printf("%c  <-- Symbol\n", ch);
            scount++;
            return 1;
        }
    }
    return 0;
}

// Check if token is numeric literal
int isNumeric(const char *val) {
    int hasDecimal = 0;
    int i = 0;
    if (val[0] == '-') i++; // handle negative
    for (; val[i]; i++) {
        if (val[i] == '.') {
            if (hasDecimal) return 0;
            hasDecimal = 1;
        } else if (!isdigit(val[i])) return 0;
    }
    if (i > 0) {
        printf("%s  <-- Numeric Literal\n", val);
        ncount++;
        return 1;
    }
    return 0;
}

// Check if token is string or char literal
int isStringLiteral(const char *val) {
    if ((val[0] == '"' && val[strlen(val) - 1] == '"') ||
        (val[0] == '\'' && val[strlen(val) - 1] == '\'')) {
        printf("%s  <-- String/Char Literal\n", val);
        strcount++;
        return 1;
    }
    return 0;
}

// Default to identifier
void processToken(const char *val) {
    if (isKeyword(val)) return;
    if (isNumeric(val)) return;
    if (isStringLiteral(val)) return;

    // Not keyword, numeric or string
    // Valid identifier: starts with alpha or _, followed by alnum or _
    if (isalpha(val[0]) || val[0] == '_') {
        int valid = 1;
        for (int i = 1; val[i]; i++) {
            if (!isalnum(val[i]) && val[i] != '_') {
                valid = 0;
                break;
            }
        }
        if (valid) {
            printf("%s  <-- Identifier\n", val);
            icount++;
            return;
        }
    }

    printf("%s  <-- Unknown\n", val);
    unknown++;
}

int main() {
    ptr = fopen("source.c", "r");
    if (ptr == NULL) {
        printf("File not found.\n");
        return 1;
    }

    char ch;
    char token[100];
    int index = 0;
    int inString = 0;
    char quoteChar = '\0';

    while ((ch = fgetc(ptr)) != EOF) {
        if (inString) {
            token[index++] = ch;
            if (ch == quoteChar) {
                token[index] = '\0';
                processToken(token);
                index = 0;
                inString = 0;
            }
            continue;
        }

        if (ch == '"' || ch == '\'') {
            if (index > 0) {
                token[index] = '\0';
                processToken(token);
                index = 0;
            }
            inString = 1;
            quoteChar = ch;
            token[index++] = ch;
            continue;
        }

        if (isspace(ch)) {
            if (index > 0) {
                token[index] = '\0';
                processToken(token);
                index = 0;
            }
        } else if (isSymbol(ch)) {
            if (index > 0) {
                token[index] = '\0';
                processToken(token);
                index = 0;
            }
            // already printed symbol
        } else {
            token[index++] = ch;
        }
    }

    if (index > 0) {
        token[index] = '\0';
        processToken(token);
    }

    printf("\nSummary:\n");
    printf("Symbols: %d\n", scount);
    printf("Identifiers: %d\n", icount);
    printf("Keywords: %d\n", kcount);
    printf("Numeric Literals: %d\n", ncount);
    printf("String/Char Literals: %d\n", strcount);
    printf("Unknown: %d\n", unknown);

    fclose(ptr);
    return 0;
}
