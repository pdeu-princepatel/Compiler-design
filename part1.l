%{

#include "y.tab.h"
#include <stdlib.h>
void yyerror(char *);
%}

%%
[0-9]+      {
                yylval = atoi(yytext); 
                return NUMBER;         
            }

[ \t]       { /* ignore whitespace */ }

\n          { return 0; } 

.           { return yytext[0]; } 

%%

int yywrap(void) {
    return 1;
}
