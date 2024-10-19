package com.moalla.lox;

public class ErrorReporter {
    public boolean hadError = false;

    public void error(int line, String message) {
        report(line, "", message);
    }

    private void report(int line, String where,
                        String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
