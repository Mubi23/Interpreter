package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

fun main() {
    val source = System.`in`.bufferedReader().readText()

    val tokens = Lexer(source).tokenize()

    val statements = Parser(tokens).parse()

    val interpreter = Interpreter()
    interpreter.interpret(statements)
    interpreter.printGlobals()
}