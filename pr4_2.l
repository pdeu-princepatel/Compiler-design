%{
#include <stdio.h>

char l;
void E();
void T();
void E_();
void T_();
void F();
void match(char t);

%}
%%
%%
void E(){
    T();
    E_();
}
void E_(){
    if(l=="+"){
        match("+");
        T();
        E_();
    }
    else if(l=="-"){
        match("-");
        T();
        E_();
    }
    else
        return;
}

void T(){
    F();
    T_();
}
    
void T_(){
     if(l=="*"){
        match("*");
        F();
        T_();
    }
    else if(l=="/"){
        match("/");
        F();
        T_();
    }
    else
        return;
}

void F(){
    if(l=="("){
        match("(");
        E();
        match(")");
    }
    else if(l=="i"){
        match("i");
    
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
int main(){
    l=getchar();
    E();
    if(l=="$"){
        printf("Parsing Successful");
    }
    return 0;
}
int yywrap(){
    return 1;
}