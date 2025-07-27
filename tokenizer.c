#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>

int keyword_count = 0, symbol_count = 0, identifier_count = 0;
int numeric_count = 0, string_count = 0, unknown_count = 0;

bool isSymbol(char c) {
    char symbols[] = "+-*/=%;:,(){}[]<>!&|^~.";
    return strchr(symbols, c) != NULL;
}

bool isKeyword(char *word) {
    char *keywords[] = {
        "auto", "break", "case", "char", "const", "continue", "default",
        "do", "double", "else", "enum", "extern", "float", "for", "goto",
        "if", "int", "long", "register", "return", "short", "signed",
        "sizeof", "static", "struct", "switch", "typedef", "union",
        "unsigned", "void", "volatile", "while", "printf", "scanf"
    };
    int num_keywords = sizeof(keywords) / sizeof(keywords[0]);
    for (int i = 0; i < num_keywords; i++) {
        if (strcmp(word, keywords[i]) == 0)
            return true;
    }
    return false;
}

bool isNumber(char *word) {
    for (int i = 0; word[i]; i++) {
        if (!isdigit(word[i]))
            return false;
    }
    return true;
}

void classifyToken(char *word) {
    if (strlen(word) == 0) return;

    if (isKeyword(word)) {
        printf("%s  <-- Keyword\n", word);
        keyword_count++;
    } else if (isNumber(word)) {
        printf("%s  <-- Numeric Literal\n", word);
        numeric_count++;
    } else if ((word[0] == '"' && word[strlen(word) - 1] == '"') ||
               (word[0] == '\'' && word[strlen(word) - 1] == '\'')) {
        printf("%s  <-- String/Char Literal\n", word);
        string_count++;
    } else if (isalpha(word[0]) || word[0] == '_') {
        printf("%s  <-- Identifier\n", word);
        identifier_count++;
    } else if (isSymbol(word[0])) {
        printf("%s  <-- Symbol\n", word);
        symbol_count++;
    } else {
        printf("%s  <-- Unknown\n", word);
        unknown_count++;
    }
}

void tokenize(char *line) {
    char *delim = " \t\r\n";
    char *word = strtok(line, delim);
    while (word != NULL) {
        int len = strlen(word);
        if ((word[0] == '"' && word[len - 1] != '"') || (word[0] == '\'' && word[len - 1] != '\'')) {
            // Handle multi-word string/char literals
            char buffer[100] = {0};
            strcat(buffer, word);
            strcat(buffer, " ");
            word = strtok(NULL, delim);
            while (word && !(word[strlen(word) - 1] == '"' || word[strlen(word) - 1] == '\'')) {
                strcat(buffer, word);
                strcat(buffer, " ");
                word = strtok(NULL, delim);
            }
            if (word) strcat(buffer, word);
            classifyToken(buffer);
        } else {
            classifyToken(word);
        }
        word = strtok(NULL, delim);
    }
}

int main() {
    FILE *fp = fopen("source.c", "r");
    if (!fp) {
        printf("Error opening file.\n");
        return 1;
    }

    char line[1000];
    bool inMultiLineComment = false;
    while (fgets(line, sizeof(line), fp)) {
        char clean[1000] = {0};
        int j = 0;
        for (int i = 0; line[i]; i++) {
            if (!inMultiLineComment && line[i] == '/' && line[i+1] == '/') break;
            if (!inMultiLineComment && line[i] == '/' && line[i+1] == '*') {
                inMultiLineComment = true;
                i++;
                continue;
            }
            if (inMultiLineComment && line[i] == '*' && line[i+1] == '/') {
                inMultiLineComment = false;
                i++;
                continue;
            }
            if (!inMultiLineComment)
                clean[j++] = line[i];
        }
        clean[j] = '\0';

        tokenize(clean);
    }

    fclose(fp);

    printf("\nSummary:\n");
    printf("Symbols: %d\n", symbol_count);
    printf("Identifiers: %d\n", identifier_count);
    printf("Keywords: %d\n", keyword_count);
    printf("Numeric Literals: %d\n", numeric_count);
    printf("String/Char Literals: %d\n", string_count);
    printf("Unknown: %d\n", unknown_count);

    return 0;
}
