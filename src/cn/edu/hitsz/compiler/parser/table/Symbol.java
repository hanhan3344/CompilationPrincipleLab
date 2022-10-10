package cn.edu.hitsz.compiler.parser.table;

import cn.edu.hitsz.compiler.lexer.Token;

public class Symbol{
    Token token;
    NonTerminal nonTerminal;

    public Symbol(Token token, NonTerminal nonTerminal){
        this.token = token;
        this.nonTerminal = nonTerminal;
    }

    public Symbol(Token token){
        new Symbol(token, null);
    }

    public Symbol(NonTerminal nonTerminal){
        new Symbol(null, nonTerminal);
    }

    public NonTerminal getNonterminal(){
        return nonTerminal;
    }

    public Token getToken(){
        return token;
    }

    public Boolean isToken(){
        return this.token != null;
    }

    public Boolean isNonterminal(){
        return this.nonTerminal != null;
    }
}

