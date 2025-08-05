%{
#include <stdio.h>
#include <string.h>
int vcount = 0;
char output[1000] = "";  // Buffer for reversed output
%}

%%

[aeiouAEIOU]    { vcount++; }

[a-zA-Z0-9]+    {
    /* Count vowels in this word */
    for(int i = 0; i < yyleng; i++) {
        if(strchr("aeiouAEIOU", yytext[i])) {
            vcount++;
        }
    }
    
    /* Build reversed output */
    strcat(output, "\"");
    for(int j = yyleng - 1; j >= 0; j--) {
        char c[2] = {yytext[j], '\0'};
        strcat(output, c);
    }
    strcat(output, "\" ");
}

.|\n        { /* Ignore other characters */ }

%%

int main() {
    printf("Enter string to reverse: ");
    yylex();
    printf("Reversed string: %s\n", output);
    printf("Total vowels: %d\n", vcount);
    return 0;
}

int yywrap() {
    return 1;
}