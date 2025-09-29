%{
#include <stdio.h>
#include <stdlib.h>

void yyerror(char* s);
int yylex(void);
%}
%token ID
%left '+'
%left '*'
%right '^'
%start E
%%
E: E '+' T
 | T;

T:T '*' F
 | F;

F:P '^' F
 | P;

P: '('E')'
 | ID;

%%

/* C Functions */
void yyerror(char *s) {
    fprintf(stderr, "Error: %s\n", s);
}

int main(void) {
    printf("Enter an expression using 'id', '+', '*', '^', '(', ')'.\n");
    yyparse();
    return 0;
}