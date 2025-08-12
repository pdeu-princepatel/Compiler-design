%{
#include <stdio.h>
#include <stdlib.h>

FILE *commentfile;
FILE *outputfile;

int yywrap() {
    return 1;
}
%}

%%
"//".*                          { fprintf(commentfile, "%s\n", yytext); }
"/*"([^*]|\*+[^*/])*\*+"/"     { fprintf(commentfile, "%s\n", yytext); }

[^/\n]+                        { fputs(yytext, outputfile); }
"/"                            { fputc(yytext[0], outputfile); }
\n                             { fputc('\n', outputfile); }
.                              { fputc(yytext[0], outputfile); }
%%

int main(int argc, char **argv) {
    commentfile = fopen("comments.txt", "w");
    if (!commentfile) {
        perror("Could not open comments.txt");
        exit(1);
    }

    outputfile = fopen("input.c", "w");
    if (!outputfile) {
        perror("Could not open input.c");
        fclose(commentfile);
        exit(1);
    }

    yylex();

    fclose(commentfile);
    fclose(outputfile);

    return 0;
}
