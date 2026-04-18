package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

fun main() {
    val source = "x = 0\ny = 0\nwhile x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1"
    println("Source: $source")

    val tokens = Lexer(source).tokenize()
    println("Tokens: $tokens")

    val statements = Parser(tokens).parse()
    println("Statements: $statements")

    val interpreter = Interpreter()
    interpreter.interpret(statements)
    interpreter.printGlobals()
}