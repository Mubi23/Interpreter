package org.example

enum class TokenType{
    NUMBER, IDENT, TRUE, FALSE, PLUS, MINUS, STAR, SLASH, EQ, EQ_EQ, NE_EQ, LT, GT, GT_EQ, LT_EQ,
    LPAREN, RPAREN, LBRACE, RBRACE, COMMA, IF, THEN, ELSE, WHILE, DO, FUN, RETURN, EOF
}

data class Token(val type: TokenType, val value: String, val line: Int = 0)