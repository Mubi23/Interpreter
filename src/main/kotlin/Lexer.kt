package org.example

class Lexer(private val source: String) {
    private var pos: Int = 0
    private var line: Int = 1
    private val tokens = mutableListOf<Token>()
    companion object {
        private val keywords = mapOf(
            "if" to TokenType.IF, "then" to TokenType.THEN, "else" to TokenType.ELSE,
            "while" to TokenType.WHILE, "do" to TokenType.DO,
            "fun" to TokenType.FUN, "return" to TokenType.RETURN,
            "true" to TokenType.TRUE, "false" to TokenType.FALSE
        )
    }
    fun tokenize(): List<Token> {
        while (pos < source.length) {
            val ch: Char = source[pos++];
            when (ch) {
                ' ', '\t', '\r' -> {}
                '\n' -> line++
                '+' -> tokens.add(Token(TokenType.PLUS, "+", line))
                '-' -> tokens.add(Token(TokenType.MINUS, "-", line))
                '*' -> tokens.add(Token(TokenType.STAR, "*", line))
                '/' -> tokens.add(Token(TokenType.SLASH, "/", line))
                '(' -> tokens.add((Token(TokenType.LPAREN, "(", line)))
                ')' -> tokens.add((Token(TokenType.RPAREN, ")", line)))
                '{' -> tokens.add((Token(TokenType.LBRACE, "{", line)))
                '}' -> tokens.add((Token(TokenType.RBRACE, "}", line)))
                ',' -> tokens.add(Token(TokenType.COMMA, ",", line))
                '=' -> {
                    if (safePeek() == '=') {
                        tokens.add((Token(TokenType.EQ_EQ, "==", line)))
                        pos++
                    }  else tokens.add((Token(TokenType.EQ, "=", line)))
                }
                '>' -> {
                    if (safePeek() == '=') {
                        tokens.add((Token(TokenType.GT_EQ, ">=", line)))
                        pos++
                    } else tokens.add((Token(TokenType.GT, ">", line)))
                }
                '<' -> {
                    if (safePeek() == '=') {
                        tokens.add((Token(TokenType.LT_EQ, "<=", line)))
                        pos++
                    } else tokens.add((Token(TokenType.LT, "<", line)))
                }
                '!' -> {
                    if (safePeek() == '=') {
                        tokens.add((Token(TokenType.NE_EQ, "!=", line)))
                        pos++
                    } else throw Exception("Not allowed expression.")
                }
                else -> {
                    when{
                        ch.isDigit() -> {
                            val actualPos = pos-1;
                            while (safePeek().isDigit()) pos++
                            tokens.add(Token(TokenType.NUMBER, source.substring(actualPos,pos), line));
                        }
                        ch.isLetter() -> {
                            val actualPos = pos-1
                            while (safePeek().isLetterOrDigit() || safePeek() == '_') pos++
                            val lexema = source.substring(actualPos, pos)
                            val type = Lexer.keywords[lexema] ?: TokenType.IDENT
                            tokens.add(Token(type, lexema, line))
                        }
                        else -> throw Exception("Invalid character.")
                    }
                }
            }

        }
        tokens.add(Token(TokenType.EOF, "", line))
        return tokens.toList();
    }
    fun safePeek(): Char{
        return if (pos < source.length) source[pos] else '\u0000';
    }

}