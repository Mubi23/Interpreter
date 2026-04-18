package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

fun main() {
    val source = "fun fact_rec(n) { if n <= 0 then return 1 else return n*fact_rec(n-1) }\na = fact_rec(5)"
    println("Source: $source")

    val tokens = Lexer(source).tokenize()
    println("Tokens: $tokens")

    val statements = Parser(tokens).parse()
    println("Statements: $statements")

    val interpreter = Interpreter()
    interpreter.interpret(statements)
    interpreter.printGlobals()
}