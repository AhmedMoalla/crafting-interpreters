package com.moalla.lox;

public record Token(TokenType type, String lexeme, Object literal, int line) {

    @Override
    public String toString() {
        String literalToString = literal != null ? "= " + literal : "";
        return type + "[" + lexeme + "] " + literalToString;
    }
}