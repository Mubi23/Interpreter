package org.example

import kotlin.collections.mutableListOf

class Parser(private val tokens: List<Token>) {
    private var pos: Int = 0
    fun safePeek(): Token {
        return if (pos < tokens.size) tokens[pos] else Token(TokenType.EOF,"",0)
    }
    fun match(vararg types: TokenType): Boolean{
        for (type in types){
            if (tokens[pos].type == type){
                pos++
                return true
            }
        }
        return false
    }

    fun expect(type: TokenType): Boolean{
        if (tokens[pos].type == type){
            pos++
            return true
        }else throw Exception("Parse error")
    }

    fun parse(): List<Statement> {
        val statements = mutableListOf<Statement>()
        while (tokens[pos].type != TokenType.EOF){
            statements.add(parseStatements())
        }
        return statements
    }

    fun parseStatements(multiple: Boolean = true): Statement{
        var statement: Statement
        when (safePeek().type){
            TokenType.FUN -> statement = parseFunDef()
            TokenType.WHILE -> statement = parseWhile()
            TokenType.IF -> statement = parseIf()
            TokenType.RETURN -> statement = parseReturn()
            TokenType.IDENT -> statement = parseAssign()
            else -> throw Exception("Not allowed expression at position $pos")
        }
        if (multiple && safePeek().type == TokenType.COMMA){
            val multipleStatements = mutableListOf<Statement>()
            multipleStatements.add(statement)
            while(match(TokenType.COMMA)) multipleStatements.add(parseStatements())
            return Statement.MultipleStatements(multipleStatements)
        }else return statement
    }

    fun parseFunDef(): Statement{
        expect(TokenType.FUN)
        expect(TokenType.IDENT)
        val name = previous().value
        val args = mutableListOf<String>()
        expect(TokenType.LPAREN)
        if (safePeek().type != TokenType.RPAREN){
            expect(TokenType.IDENT)
            args.add(previous().value)
            while (match(TokenType.COMMA)) {
                expect(TokenType.IDENT)
                args.add(previous().value)
            }
        }
        expect(TokenType.RPAREN)
        expect(TokenType.LBRACE)
        val listStatements = mutableListOf<Statement>()
        while(safePeek().type != TokenType.RBRACE){
            listStatements.add(parseStatements())
        }
        expect(TokenType.RBRACE)
        return Statement.Function(name, args, listStatements)
    }

    fun parseWhile(): Statement{
        expect(TokenType.WHILE)
        val condition = parseExpr()
        expect(TokenType.DO)
        val statement = parseStatements()
        return Statement.While(condition, statement)
    }
    fun parseIf(): Statement{
        expect(TokenType.IF)
        val condition = parseExpr()
        expect(TokenType.THEN)
        val thenBody = parseStatements(false)
        if (safePeek().type == TokenType.ELSE){
            expect(TokenType.ELSE)
            val elseBody = parseStatements(false)
            return Statement.If(condition, thenBody, elseBody)
        }
        else return Statement.If(condition, thenBody, null)
    }
    fun parseReturn(): Statement{
        expect(TokenType.RETURN)
        val value = parseExpr()
        return Statement.Return(value)
    }
    fun parseAssign(): Statement{
        val identifier = tokens[pos++]
        expect(TokenType.EQ)
        val value = parseExpr()
        return Statement.Assign(identifier.value,value)
    }

    fun parseExpr(): Expr {
        return parseComparison()
    }
    fun parseComparison(): Expr {
        var left = parseAddSub()
        while (match(TokenType.EQ_EQ, TokenType.NE_EQ, TokenType.LT, TokenType.GT, TokenType.LT_EQ, TokenType.GT_EQ)) {
            val op = previous().type
            val right = parseAddSub()
            left = Expr.BinaryOp(left, op, right)
        }
        return left
    }
    fun parseAddSub(): Expr {
        var left = parseMulDiv()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val op = previous().type
            val right = parseMulDiv()
            left = Expr.BinaryOp(left, op, right)
        }
        return left
    }
    fun parseMulDiv(): Expr {
        var left = parsePrimary()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val op = previous().type
            val right = parsePrimary()
            left = Expr.BinaryOp(left, op, right)
        }
        return left
    }

    fun parsePrimary(): Expr {
        when {
            match(TokenType.NUMBER) -> return Expr.NumberLiteral(previous().value.toInt())
            match(TokenType.TRUE)   -> return Expr.BoolLiteral(true)
            match(TokenType.FALSE)  -> return Expr.BoolLiteral(false)
            match(TokenType.IDENT)  -> {
                val name = previous().value
                return if (safePeek().type == TokenType.LPAREN) parseCall(name)
                else Expr.Variable(name)
            }
            match(TokenType.LPAREN) -> {
                val expr = parseExpr()
                expect(TokenType.RPAREN)
                return expr
            }
            else -> throw Exception("Unexpected token ${tokens[pos]} at pos $pos")
        }
    }
    fun parseCall(name: String): Expr {
        expect(TokenType.LPAREN)
        val variables = mutableListOf<Expr>()
        if (safePeek().type != TokenType.RPAREN) {
            variables.add(parseExpr())
            while (match(TokenType.COMMA)) variables.add(parseExpr())
        }
        expect(TokenType.RPAREN)
        return Expr.Call(name,variables)
    }
    fun previous(): Token{
        if (pos > 0) return tokens[pos - 1] else throw Exception("Negative position")
    }
}