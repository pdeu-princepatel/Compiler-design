%{

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

void yyerror(char *);
int yylex(void);
%}

%token NUMBER
%left '+' '-'
%left '*' '/'
%right '^'
%%


program:    /* The program can be empty or have multiple expressions */
            | program expression { printf("= %d\n", $2); }
            ;

expression: NUMBER          { $$ = $1; }
            | expression '+' expression { $$ = $1 + $3; }
            | expression '-' expression { $$ = $1 - $3; }
            | expression '*' expression { $$ = $1 * $3; }
            | expression '/' expression {
                                          if ($3 == 0) {
                                              yyerror("Error: Division by zero");
                                          } else {
                                              $$ = $1 / $3;
                                          }
                                        }
            | expression '^' expression { $$ = (int)pow($1,$3); }
            | '(' expression ')'      { $$ = $2; }
            ;

%%

/* This is the C code section */

#include <stdio.h>

void yyerror(char *s) {
    fprintf(stderr, "%s\n", s);
}

int main(void) {
    printf("Enter an expression to calculate:\n");
    yyparse();
    return 0;
}
