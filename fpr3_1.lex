%{
#include <stdio.h>
int flag;
%}

%%
[a-zA-Z0-9]+ {
    flag = 1;
    for(int i = 0, j = yyleng-1; i <= j; i++, j--) {
        if(yytext[i] != yytext[j]) {
            flag = 0;
            break;
        }
    }
    if(flag) {
        printf("\"%s\" is a palindrome\n", yytext);
    } else {
        printf("\"%s\" is not a palindrome\n", yytext);
    }
}
.|\n { /* Ignore other characters */ }
%%

int main() {
    printf("Enter a string: ");
    yylex();
    return 0;
}

int yywrap() {
    return 1;
}
//
//
//
//
//
//
//
//
//
//
//
//
/*
%{
#include <stdio.h>
int i,j,flag;
%}
%%
{
[a-zA-Z0-9]*    {
for(i=0,j=yyleng-1;i<=j;i++,j--){
if(yytext[i]==yytext[j]){
flag=1;
}
else
{
flag=0;
break;
}
}
if(flag == 1){
printf("Entered String is a Palindrome");
}
else{
printf("Entered String is not a Palindrome");
}
}
}
%%
main(){
printf("Enter a String:");
yylex();
return 0;
}
int yywrap(){
return 1;
}*/