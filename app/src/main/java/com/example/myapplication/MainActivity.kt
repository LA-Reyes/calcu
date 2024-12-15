package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var inputText: TextView
    private var currentInput: String = ""
    private var isResultDisplayed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        inputText = findViewById(R.id.textInput)


        val numberButtons = listOf(
            findViewById<Button>(R.id.button0),
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6),
            findViewById<Button>(R.id.button7),
            findViewById<Button>(R.id.button8),
            findViewById<Button>(R.id.button9)
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (isResultDisplayed) {
                    currentInput = ""
                    isResultDisplayed = false
                }
                appendToInput(index.toString())
            }
        }


        findViewById<Button>(R.id.plus).setOnClickListener { appendOperator("+") }
        findViewById<Button>(R.id.minus).setOnClickListener { appendOperator("-") }
        findViewById<Button>(R.id.multiply).setOnClickListener { appendOperator("*") }
        findViewById<Button>(R.id.divide).setOnClickListener { appendOperator("/") }


        findViewById<Button>(R.id.buttonClear).setOnClickListener {
            currentInput = ""
            isResultDisplayed = false
            updateInputDisplay()
        }


        findViewById<Button>(R.id.buttonEquals).setOnClickListener {
            calculateResult()
        }
    }

    private fun appendToInput(value: String) {
        currentInput += value
        updateInputDisplay()
    }

    private fun appendOperator(operator: String) {
        if (isResultDisplayed) {
            isResultDisplayed = false
        }
        currentInput += operator
        updateInputDisplay()
    }

    private fun updateInputDisplay() {
        inputText.text = currentInput
    }

    private fun calculateResult() {
        try {
            val result = eval(currentInput)
            currentInput = if (result % 1 == 0.0) {
                result.toInt().toString()
            } else {
                result.toString()
            }
            isResultDisplayed = true
            updateInputDisplay()
        } catch (e: Exception) {
            currentInput = "Error"
            isResultDisplayed = true
            updateInputDisplay()
        }
    }

    private fun eval(expression: String): Double {

        return object : Any() {
            private var pos = -1
            private var ch = 0

            private fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            private fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            private fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            private fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            private fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                when {
                    eat('('.code) -> {
                        x = parseExpression()
                        eat(')'.code)
                    }
                    ch in '0'.code..'9'.code || ch == '.'.code -> {
                        while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                        x = expression.substring(startPos, pos).toDouble()
                    }
                    else -> throw RuntimeException("Unexpected: " + ch.toChar())
                }

                return x
            }
        }.parse()
    }
}
