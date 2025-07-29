%{
#include <stdio.h>
int tokencount = 0;
%}

%%
[a-zA-Z_][a-zA-Z0-9_]*    { tokencount++; }     // Identifiers / words
[0-9]+                    { tokencount++; }     // Numbers
"=="|"!="|"<="|">="       { tokencount++; }     // Double-character operators
[-+*/%=<>{}()[\];:,!&|^~] { tokencount++; }     // Single-character symbols
[ \t\n]+                  { /* Ignore whitespace */ }
.                         { /* Ignore unrecognized chars */ }
%%

int yywrap() {
    return 1;
}

int main() {
    yylex();
    printf("Total tokens: %d\n", tokencount);
    return 0;
}
