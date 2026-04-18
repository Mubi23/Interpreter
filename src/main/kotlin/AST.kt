package org.example

sealed class Expr {
    data class NumberLiteral(val value: Int) : Expr()
    data class BoolLiteral(val value: Boolean) : Expr()
    data class Variable(val value: String) : Expr()
    data class BinaryOp(val left: Expr, val op: TokenType, val right: Expr) : Expr()
    data class Call(val name: String, val arguments: List<Expr>) : Expr()
}

sealed class Statement {
    data class Assign(val name: String, val value: Expr) : Statement()
    data class If(val condition: Expr, val thenBranch: Statement, val elseBranch: Statement?) : Statement()
    data class While(val condition: Expr, val whenBody: Statement) : Statement()
    data class Return(val expression: Expr) : Statement()
    data class Function(val name: String, val params: List<String>, val body: List<Statement>) : Statement()
    data class MultipleStatements(val statements: List<Statement>) : Statement()
}