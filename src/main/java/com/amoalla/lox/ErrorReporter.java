package com.amoalla.lox;

public class ErrorReporter {
    public boolean hadError = false;

    public void error(int line, String message) {
        report(line, "", message);
    }

    public void error(Token token, String message) {
        if (token.type() == TokenType.EOF) {
            report(token.line(), " at end", message);
        } else {
            report(token.line(), " at '" + token.lexeme() + "'", message);
        }
    }

    private void report(int line, String where,
                        String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
