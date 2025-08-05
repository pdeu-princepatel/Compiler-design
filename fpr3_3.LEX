%{
#include <stdio.h>
#include <math.h>

int is_prime(int n) {
    if (n <= 1) return 0;
    if (n <= 3) return 1;
    if (n % 2 == 0 || n % 3 == 0) return 0;
    
    int max_divisor = sqrt(n);
    for (int i = 5; i <= max_divisor; i += 6) {
        if (n % i == 0 || n % (i + 2) == 0) {
            return 0;
        }
    }
    return 1;
}
%}

%%
[0-9]+  {
    int num = atoi(yytext);
    if (is_prime(num)) {
        printf("\"%s\" is a prime number\n", yytext);
    } else {
        printf("\"%s\" is not a prime number\n", yytext);
    }
}
.|\n    { /* Ignore other characters */ }
%%

int main() {
    printf("Enter number to check for primality: ");
    yylex();
    return 0;
}

int yywrap() {
    return 1;
}