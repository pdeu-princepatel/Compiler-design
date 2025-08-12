%{
#include <stdio.h>
FILE *yyin;
%}

%%
"<"                     { printf("OPEN_TAG\t%s\n", yytext); }
"</"                    { printf("CLOSE_TAG_START\t%s\n", yytext); }
">"                     { printf("TAG_END\t%s\n", yytext); }
"="                     { printf("EQUALS\t%s\n", yytext); }
\"[^\"]*\"              { printf("STRING\t%s\n", yytext); }
[a-zA-Z][a-zA-Z0-9_-]*  { printf("IDENTIFIER\t%s\n", yytext); }
[^<>\"= \t\n\r]+        { printf("TEXT\t%s\n", yytext); }
[ \t\n\r]+              { /* skip whitespace */ }
.                       { printf("UNKNOWN\t%s\n", yytext); }
%%

int main(int argc, char **argv) {
    if (argc > 1) {
        yyin = fopen(argv[1], "r");
        if (!yyin) {
            perror("Error opening file");
            return 1;
        }
    } else {
        yyin = stdin;
    }

    yylex();

    if (yyin != stdin) {
        fclose(yyin);
    }

    return 0;
}

int yywrap() {
    return 1;
}
