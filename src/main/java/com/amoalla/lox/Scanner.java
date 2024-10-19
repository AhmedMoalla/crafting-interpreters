package com.amoalla.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amoalla.lox.TokenType.*;
import static java.util.Map.entry;

public class Scanner {
    private final String source;
    private final ErrorReporter errorReporter;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0; // first character in the lexeme being scanned
    private int current = 0; // character currently being considered
    private int line = 1; // what source line current is on

    private static final Map<String, TokenType> keywords = Map.ofEntries(
            entry("and", AND),
            entry("class", CLASS),
            entry("else", ELSE),
            entry("false", FALSE),
            entry("for", FOR),
            entry("fun", FUN),
            entry("if", IF),
            entry("nil", NIL),
            entry("or", OR),
            entry("print", PRINT),
            entry("return", RETURN),
            entry("super", SUPER),
            entry("this", THIS),
            entry("true", TRUE),
            entry("var", VAR),
            entry("while", WHILE)
    );

    public Scanner(String source, ErrorReporter errorReporter) {
        this.source = source;
        this.errorReporter = errorReporter;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '"' -> addStringToken();
            case char _ when isAlpha(c) -> addIdentifierToken();
            case char _ when isDigit(c) -> addNumberToken();
            case '/' -> {
                if (match('/')) { // Comments
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else { // Division
                    addToken(SLASH);
                }
            }
            case ' ', '\r', '\t' -> {
                // Ignore whitespace.
            }
            case '\n' -> line++;
            default -> errorReporter.error(line, "Unexpected character '%s'.".formatted(peek()));
        }
    }

    // consumes the next character in the source file
    private char advance() {
        return source.charAt(current++);
    }

    // conditional advance(). only consume the current character if it’s what we’re looking for.
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // like advance(), but doesn’t consume the character
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // like peek(), but looks at the next character
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    // grabs the text of the current lexeme and creates a new token for it
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // overload of addToken for literal values
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void addStringToken() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            errorReporter.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void addNumberToken() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void addIdentifierToken() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}