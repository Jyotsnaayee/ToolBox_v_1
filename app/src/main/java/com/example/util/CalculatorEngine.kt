package com.example.util

import kotlin.math.*

object CalculatorEngine {
    fun evaluate(expression: String): String {
        return try {
            val result = Parser(expression).parse()
            if (result.isInfinite() || result.isNaN()) {
                "Error"
            } else {
                if (result == result.toLong().toDouble()) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }
            }
        } catch (e: Exception) {
            "Error"
        }
    }

    private class Parser(val input: String) {
        private var pos = -1
        private var ch = -1

        private fun nextChar() {
            ch = if (++pos < input.length) input[pos].toInt() else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.toInt()) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < input.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.toInt())) x += parseTerm()
                else if (eat('-'.toInt())) x -= parseTerm()
                else return x
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.toInt())) x *= parseFactor()
                else if (eat('/'.toInt())) x /= parseFactor()
                else return x
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.toInt())) return parseFactor()
            if (eat('-'.toInt())) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.toInt())) {
                x = parseExpression()
                eat(')'.toInt())
            } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) {
                while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
                x = input.substring(startPos, pos).toDouble()
            } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) {
                while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
                val func = input.substring(startPos, pos)
                x = parseFactor()
                x = when (func) {
                    "sqrt" -> sqrt(x)
                    "sin" -> sin(Math.toRadians(x))
                    "cos" -> cos(Math.toRadians(x))
                    "tan" -> tan(Math.toRadians(x))
                    "log" -> log10(x)
                    "ln" -> ln(x)
                    else -> throw RuntimeException("Unknown function: $func")
                }
            } else {
                throw RuntimeException("Unexpected: " + ch.toChar())
            }

            if (eat('^'.toInt())) x = x.pow(parseFactor())

            return x
        }
    }
}
