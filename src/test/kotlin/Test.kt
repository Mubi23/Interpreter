package org.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class InterpreterTest {

    private fun run(source: String): Map<String, Any> {
        val tokens = Lexer(source).tokenize()
        val statements = Parser(tokens).parse()
        val interpreter = Interpreter()
        interpreter.interpret(statements)
        return interpreter.getGlobals()
    }

    @Test
    fun `basic arithmetic`() {
        val result = run("x = 2\ny = (x + 2) * 2")
        assertEquals(2, result["x"])
        assertEquals(8, result["y"])
    }

    @Test
    fun `if else`() {
        val result = run("x = 20\nif x > 10 then y = 100 else y = 0")
        assertEquals(20, result["x"])
        assertEquals(100, result["y"])
    }

    @Test
    fun `while loop`() {
        val result = run("x = 0\ny = 0\nwhile x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1")
        assertEquals(3, result["x"])
        assertEquals(11, result["y"])
    }

    @Test
    fun `function call`() {
        val result = run("fun add(a, b) { return a + b }\nfour = add(2, 2)")
        assertEquals(4, result["four"])
    }

    @Test
    fun `recursive factorial`() {
        val result = run("fun fact_rec(n) { if n <= 0 then return 1 else return n*fact_rec(n-1) }\na = fact_rec(5)")
        assertEquals(120, result["a"])
    }

    @Test
    fun `iterative factorial`() {
        val result = run("fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }\nb = fact_iter(5)")
        assertEquals(120, result["b"])
    }

    @Test
    fun `double test`() {
        val result = run("x = 2.5\ny = 3\n y = x + y")
        assertEquals(2.5, result["x"])
        assertEquals(5.5, result["y"])
    }
}