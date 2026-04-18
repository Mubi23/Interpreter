package org.example

class Interpreter(private val globalScope: Scope = Scope(), private val functions: MutableMap<String, Statement.Function> = mutableMapOf()) {
    fun interpret(statements: List<Statement>){
        for (statement in statements){
            execute(statement, globalScope)
        }
    }

    private fun execute(statement: Statement, scope: Scope){
        when (statement) {
            is Statement.Assign -> executeAssignment(statement, scope)
            is Statement.If -> executeIf(statement, scope)
            is Statement.While -> executeWhile(statement, scope)
            is Statement.Return -> executeReturn(statement, scope)
            is Statement.Function -> executeFunction(statement, scope)
            is Statement.MultipleStatements -> executeMultiple(statement, scope)
        }
    }

    private fun evaluate(expr: Expr, scope: Scope): Any{
        when (expr) {
            is Expr.NumberLiteral -> return expr.value
            is Expr.BoolLiteral -> return expr.value
            is Expr.Variable -> return scope.get(expr.value)
            is Expr.BinaryOp -> return evaluateBinary(expr, scope)
            is Expr.Call -> return evaluateCall(expr, scope)
            else -> throw Exception("Unknown expression")
        }
    }

    private fun evaluateCall(expr: Expr.Call, scope: Scope): Any {
        val function = functions[expr.name]
            ?: throw RuntimeException("Undefined function '${expr.name}'")
        val callScope = globalScope.newChild()
        for ((param, arg) in function.params.zip(expr.arguments)) {
            callScope.define(param, evaluate(arg, scope))
        }
        try {
            for (statement in function.body) execute(statement, callScope)
            throw RuntimeException("Function '${expr.name}' has no return statement")
        } catch (r: ReturnException) {
            return r.value
        }
    }

    private fun evaluateBinary(expr: Expr.BinaryOp, scope: Scope): Any {
        val left = evaluate(expr.left, scope)
        val right = evaluate(expr.right, scope)
        when (expr.op) {
            TokenType.PLUS -> return (left as Int) + (right as Int)
            TokenType.MINUS -> return (left as Int) - (right as Int)
            TokenType.STAR  -> return (left as Int) * (right as Int)
            TokenType.SLASH -> return (left as Int) / (right as Int)
            TokenType.EQ_EQ -> return left == right
            TokenType.NE_EQ -> return left != right
            TokenType.LT    -> return (left as Int) < (right as Int)
            TokenType.GT    -> return (left as Int) > (right as Int)
            TokenType.LT_EQ -> return (left as Int) <= (right as Int)
            TokenType.GT_EQ -> return (left as Int) >= (right as Int)
            else  -> throw RuntimeException("Unknown operator ${expr.op}")
        }
    }

    private fun executeAssignment(statement: Statement.Assign, scope: Scope){
        val value = evaluate(statement.value, scope)
        try {
            scope.set(statement.name, value)
        } catch (e: RuntimeException) {
            scope.define(statement.name, value)
        }
    }
    private fun executeIf(statement: Statement.If, scope: Scope) {
        val condition = evaluate(statement.condition, scope) as Boolean
        if (condition) {
            execute(statement.thenBranch, scope)
        } else {
            statement.elseBranch?.let { execute(it, scope) }
        }
    }

    private fun executeWhile(statement: Statement.While, scope: Scope) {
        while (evaluate(statement.condition, scope) as Boolean) {
            execute(statement.whenBody, scope)
        }
    }

    private fun executeReturn(statement: Statement.Return, scope: Scope) {
        throw ReturnException(evaluate(statement.expression, scope))
    }

    private fun executeFunction(statement: Statement.Function, scope: Scope) {
        functions[statement.name] = statement
    }

    private fun executeMultiple(statement: Statement.MultipleStatements, scope: Scope) {
        for (otherStatement in statement.statements) {
            execute(otherStatement, scope)
        }
    }
    fun printGlobals() {
        for ((name, value) in globalScope.getAll()) {
            println("$name: $value")
        }
    }

    fun getGlobals(): Map<String, Any> = globalScope.getAll()
}

class ReturnException(val value: Any) : Exception()