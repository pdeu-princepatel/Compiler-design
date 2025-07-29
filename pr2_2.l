%{
#include <stdio.h>
#include <string.h>

int keyword = 0;
int identifier = 0;
int symbol = 0;
int Numbers=0;

// Helper: List of keywords
char *keywords[] = {"int", "float", "return", "if", "else", "while", "for", "char", "double", "void"};
int is_keyword(char *word) {
    for (int i = 0; i < sizeof(keywords)/sizeof(keywords[0]); i++) {
        if (strcmp(word, keywords[i]) == 0)
            return 1;
    }
    return 0;
}
%}

%%

[a-zA-Z_][a-zA-Z0-9_]*   {
                            if (is_keyword(yytext))
                                keyword++;
                            else
                                identifier++;
                        }
[0-9]+          { Numbers++;  }
[{}();=+\-*/<>]           { symbol++; }

[ \t\n]+                 ; // Ignore whitespace

.                        ; // Ignore any other characters

%%

int yywrap() {
    return 1;
}

int main() {
    yylex();
    printf("Keywords: %d\n", keyword);
    printf("Identifiers: %d\n", identifier);
    printf("Symbols: %d\n", symbol);
    printf("Numbers: %d\n",Numbers);
    return 0;
}
