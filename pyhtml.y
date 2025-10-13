%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void yyerror(const char *s);
int yylex(void);
extern int yylineno;

// Simple symbol table for variables
#define MAX_VARS 100
char* var_names[MAX_VARS];
double var_values[MAX_VARS];
int var_count = 0;

// Function to find or create variable
int find_var(char* name) {
    for(int i = 0; i < var_count; i++) {
        if(strcmp(var_names[i], name) == 0) {
            return i;
        }
    }
    // Create new variable if not found
    if(var_count < MAX_VARS) {
        var_names[var_count] = strdup(name);
        var_values[var_count] = 0.0;
        return var_count++;
    }
    return -1; // Error: too many variables
}

double get_var(char* name) {
    int idx = find_var(name);
    return (idx >= 0) ? var_values[idx] : 0.0;
}

void set_var(char* name, double value) {
    int idx = find_var(name);
    if(idx >= 0) {
        var_values[idx] = value;
    }
}
%}

%union {
    double dval;
    char* str;
}

/* ---------- Tokens ---------- */
%token <dval> NUMBER
%token <str> NAME STRING
%token MAIN_OPEN MAIN_CLOSE
%token SET_OPEN PRINT_OPEN
%token IF_OPEN IF_CLOSE
%token WHILE_OPEN WHILE_CLOSE
%token FUNC_OPEN FUNC_CLOSE
%token CALL_OPEN TAG_END
%token EQ EQEQ NEQ GE LE

/* Operator precedence - lowest to highest */
%right EQ
%left EQEQ NEQ
%left GE LE '>' '<'
%left '+' '-'
%left '*' '/'
%right UMINUS
%left '(' ')'

%type <dval> expr

%%

program:
      MAIN_OPEN stmts MAIN_CLOSE
            { printf("Program execution complete.\n"); }
    ;

stmts:
      /* empty */
    | stmts stmt
    ;

stmt:
      set_stmt
    | print_stmt
    | if_stmt
    | while_stmt
    | func_stmt
    | call_stmt
    ;

set_stmt:
      SET_OPEN NAME EQ expr TAG_END
            { 
                printf("Setting variable %s = %.2f\n", $2, $4); 
                set_var($2, $4);
                free($2); 
            }
    ;

print_stmt:
      PRINT_OPEN STRING TAG_END
            { 
                // Remove quotes from string
                char* str = $2;
                int len = strlen(str);
                if(len >= 2 && str[0] == '"' && str[len-1] == '"') {
                    str[len-1] = '\0';
                    printf("Output: %s\n", str+1);
                } else {
                    printf("Output: %s\n", str);
                }
                free($2);
            }
    | PRINT_OPEN expr TAG_END
            { printf("Output: %.2f\n", $2); }
    ;

if_stmt:
      IF_OPEN '(' expr ')' '>' stmts IF_CLOSE
            { 
                if($3) {
                    printf("If condition was true\n");
                } else {
                    printf("If condition was false\n");
                }
            }
    ;

while_stmt:
      WHILE_OPEN '(' expr ')' '>' stmts WHILE_CLOSE
            { printf("While loop executed\n"); }
    ;

func_stmt:
      FUNC_OPEN NAME '(' param_list_opt ')' '>' stmts FUNC_CLOSE
            { printf("Function defined: %s\n", $2); free($2); }
    ;

call_stmt:
      CALL_OPEN NAME '(' arg_list_opt ')' TAG_END
            { printf("Function call: %s\n", $2); free($2); }
    ;

param_list_opt:
      /* empty */
    | param_list
    ;

param_list:
      NAME          { free($1); }
    | param_list ',' NAME { free($3); }
    ;

arg_list_opt:
      /* empty */
    | arg_list
    ;

arg_list:
      expr
    | arg_list ',' expr
    ;

expr:
      NUMBER                { $$ = $1; }
    | NAME                  { $$ = get_var($1); free($1); }
    | expr '+' expr         { $$ = $1 + $3; }
    | expr '-' expr         { $$ = $1 - $3; }
    | expr '*' expr         { $$ = $1 * $3; }
    | expr '/' expr         { $$ = ($3 != 0) ? $1 / $3 : 0; }
    | '-' expr %prec UMINUS { $$ = -$2; }
    | '(' expr ')'          { $$ = $2; }
    | expr EQEQ expr        { $$ = ($1 == $3) ? 1 : 0; }
    | expr NEQ expr         { $$ = ($1 != $3) ? 1 : 0; }
    | expr '<' expr         { $$ = ($1 < $3) ? 1 : 0; }
    | expr '>' expr         { $$ = ($1 > $3) ? 1 : 0; }
    | expr LE expr          { $$ = ($1 <= $3) ? 1 : 0; }
    | expr GE expr          { $$ = ($1 >= $3) ? 1 : 0; }
    ;

%%

void yyerror(const char *s){
    fprintf(stderr,"Error: %s at line %d\n", s, yylineno);
}

int main(void){
    printf("Parsing PyHTML script...\n");
    yyparse();
    printf("Parsing finished.\n");
    return 0;
}
