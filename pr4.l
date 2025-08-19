%{
#include <stdio.h>

char l;

void E();
void E_();
void match(char t);

%}

%%
%%

void E() {
    if (l == 'i') {
        match('i');
        E_();
    }
}

void E_() {
    if (l == '+') {
        match('+');
        match('i');
        E_();
    } else {
       return;
    }
}

void match(char t) {
    if (l == t) {
        l = getchar(); 
    } else {
        printf("Error");
        return;
    }
}

int main() {
    l = getchar();
    E();
    if (l == '$') {
        printf("Parsing Successful\n");
    }
    return 0;
}

int yywrap() {
    return 1;
}
