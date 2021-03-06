package cat.atridas87.minairo;

enum TokenType {
  // Single-character tokens.
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
  COMMA, DOT, MINUS, PLUS, COLON, SEMICOLON, SLASH,
  STAR, QUESTION,

  // One or two character tokens.
  ARROW,
  BANG, BANG_EQUAL,
  EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL,
  LESS, LESS_EQUAL,

  // Literals.
  IDENTIFIER, STRING, NUMBER,

  // Keywords.
  AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
  PRINT, RETURN, SUPER, THIS, TRUE, TABLE, VAR, WHILE,

  // Basic Types
  TYPE_BOOLEAN, TYPE_NUMBER, TYPE_STRING,

  EOF
}
