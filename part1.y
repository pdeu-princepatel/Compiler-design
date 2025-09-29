%{
/*
 * This is the C declarations section for the parser.
 * We include standard libraries and declare the error function.
 */
#include <stdio.h>
#include <stdlib.h>

void yyerror(char *);
int yylex(void);
%}

/* Define the tokens that Yacc will use */
%token NUMBER

/* Define operator precedence and associativity */
%left '+' '-'
%left '*' '/'

%%
/*
 * These are the grammar rules.
 * The format is: non-terminal: components { C action }
 */

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