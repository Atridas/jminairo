package cat.atridas87.minairo;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import cat.atridas87.minairo.generated.*;

class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // program -> ( declaration )* EOF
    List<Stmt> parse() {
        try {
            List<Stmt> statements = new ArrayList<>();
            while (!isAtEnd()) {
                statements.add(declaration());
            }

            return statements;
        } catch (ParseError error) {
            return null;
        }
    }

    // statement -> "var" varDeclaration | statement
    private Stmt declaration() {
        try {
            if (match(TokenType.VAR))
                return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    // statement -> "print" printStatement | "{" block | expressionStatement
    private Stmt statement() {
        if (match(TokenType.PRINT))
            return printStatement();
        else if(match(TokenType.LEFT_BRACE))
            return new Stmt.Block(block());
        else
            return expressionStatement();
    }

    // block -> ( declaration )* "}"
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
    
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
          statements.add(declaration());
        }
    
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
      }

    // printStatement -> exprssion ";"
    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    // varDeclaration -> TokenType.IDENTIFIER "=" exprssion ";"
    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    // expressionStatement -> exprssion ";"
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    // expression -> assignment
    private Expr expression() {
        while (match(TokenType.QUESTION, TokenType.COLON, TokenType.BANG_EQUAL,
                TokenType.EQUAL_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL,
                TokenType.SLASH, TokenType.STAR)) {
            error(previous(), "Expect expression.");
        }
        return assignment();
    }

    // assignment -> IDENTIFIER ( "=" assignment ) | ternary
    private Expr assignment() {
        Expr expr = ternary();

        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    // ternary -> equality ( "?" expression ":" expression )?
    private Expr ternary() {
        Expr expr = equality(); // this goes all the way down to "logical or"

        if (match(TokenType.QUESTION)) {
            Expr pass = expression();
            consume(TokenType.COLON, "Expect ':' after expression.");
            Expr fail = expression(); // this goes to assignment expression

            expr = new Expr.Ternary(expr, pass, fail);
        }

        return expr;
    }

    // equality -> comparison ( "?" expression ":" expression )?
    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // comparison -> term ( ( ">" | "<=" | ">" | ">=" ) term )*
    private Expr comparison() {
        Expr expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // term -> factor ( ( "-" | "+" ) factor )*
    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // factor -> unary ( ( "/" | "*" ) unary )*
    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // unary -> ( "!" | "-" ) unary | primary
    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        } else if (match(TokenType.PLUS)) {
            error(previous(), "Unary '+' expressions are not supported.");
        }

        return primary();
    }

    // primary -> NUMBER | STRING | "true" | "false" | "nil" | IDENTIFIER | "("
    // expression ")"
    private Expr primary() {
        if (match(TokenType.FALSE))
            return new Expr.Literal(false);
        if (match(TokenType.TRUE))
            return new Expr.Literal(true);
        if (match(TokenType.NIL))
            return new Expr.Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    // ------------------------------------------------------------------

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Minairo.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON)
                return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
                default:
                    break;
            }

            advance();
        }
    }
}
