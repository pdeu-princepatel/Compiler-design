%{
#include "pyhtml.tab.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
%}

%%
[ \t\r]+                 ;           /* ignore spaces */
\n                       { yylineno++; }

"<main>"                 return MAIN_OPEN;
"</main>"                return MAIN_CLOSE;

"<set"                   return SET_OPEN;
"<print"                 return PRINT_OPEN;
"<if"                    return IF_OPEN;
"</if>"                  return IF_CLOSE;
"<while"                 return WHILE_OPEN;
"</while>"               return WHILE_CLOSE;

"<func"                  return FUNC_OPEN;
"</func>"                return FUNC_CLOSE;
"<call"                  return CALL_OPEN;

"/>"                     return TAG_END;

"="                      return EQ;
"("                      return '(';
")"                      return ')';
">"                      return '>';
"<"                      return '<';
"=="                     return EQEQ;
"!="                     return NEQ;
">="                     return GE;
"<="                     return LE;
"+"                      return '+';
"-"                      return '-';
"*"                      return '*';
"/"                      return '/';
","                      return ',';

\"([^\"\\]|\\.)*\"       { yylval.str = strdup(yytext); return STRING; }
[0-9]+(\.[0-9]+)?        { yylval.dval = atof(yytext); return NUMBER; }
[a-zA-Z_][a-zA-Z0-9_]*   { yylval.str = strdup(yytext); return NAME; }

.                        { printf("Unknown token: %s\n", yytext); }
%%

int yywrap(void){return 1;}
