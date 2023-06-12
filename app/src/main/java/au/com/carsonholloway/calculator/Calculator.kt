package au.com.carsonholloway.calculator

import kotlin.math.abs
import kotlin.math.roundToLong

class Calculator {
    private enum class Operator {
        NONE,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE;

        fun calculate(first: Double, second: Double): Double {
            return when (this) {
                ADD -> first + second
                SUBTRACT -> first - second
                MULTIPLY -> first * second
                DIVIDE -> first / second
                NONE -> throw IllegalStateException("unhandled calculate none")
            }
        }

        fun calculateConstant(constant: Double, value: Double): Double {
            return when (this) {
                ADD -> value + constant
                SUBTRACT -> value - constant
                MULTIPLY -> constant * value
                DIVIDE -> value / constant
                NONE -> throw IllegalStateException("unhandled calc constant none")
            }
        }
    }

    private enum class State {
        // INPUT_*_START states are for when an input is awaited for but something else is being
        // displayed on the screen.  Typing in a decimal place or digit will override what's on the
        // screen and move to the corresponding INPUT_* state.

        INPUT_FIRST_START,
        /**Receiving the first input for a new equation. */
        INPUT_FIRST,
        INPUT_SECOND_START,
        /**Receiving the second input for an equation. */
        INPUT_SECOND,
        INPUT_NEW_START,
        /**Receiving input immediately after an equation. */
        INPUT_NEW,
        /**Showing the output of an equation to the display. */
        SHOW_ANSWER,
    }

    private var first: Double = 0.0
    private var result: Double? = null
    private var constant: Double? = null
    private var state: State = State.INPUT_FIRST_START
    private var operator: Operator = Operator.NONE
    private var input: InputDecimal = InputDecimal()

    val display: String
        get() = when (state) {
            State.INPUT_FIRST_START,
            State.INPUT_SECOND_START -> displayDouble(first)
            State.INPUT_FIRST,
            State.INPUT_SECOND,
            State.INPUT_NEW -> input.toString()
            State.SHOW_ANSWER,
            State.INPUT_NEW_START -> displayDouble(result!!)
        }

    fun inputDigit(digit: Int) {
        if (digit < 0 || digit > 9) {
            throw IllegalArgumentException("expected a single digit input")
        }

        when (state) {
            State.INPUT_FIRST_START -> {
                input.clear()
                input.appendDigit(digit)
                state = State.INPUT_FIRST
            }
            State.INPUT_SECOND_START -> {
                input.clear()
                input.appendDigit(digit)
                state = State.INPUT_SECOND
            }
            State.INPUT_NEW_START -> {
                input.clear()
                input.appendDigit(digit)
                state = State.INPUT_NEW
            }
            State.INPUT_FIRST,
            State.INPUT_SECOND,
            State.INPUT_NEW -> input.appendDigit(digit)
            State.SHOW_ANSWER -> {
                input.clear()
                input.appendDigit(digit)
                state = State.INPUT_NEW
            }
        }

        logRegisters("input")
    }

    fun inputDecimal() {
        when (state) {
            State.INPUT_FIRST_START -> {
                input.clear()
                input.appendDecimal()
                state = State.INPUT_FIRST
            }
            State.INPUT_SECOND_START -> {
                input.clear()
                input.appendDecimal()
                state = State.INPUT_SECOND
            }
            State.INPUT_NEW_START -> {
                input.clear()
                input.appendDecimal()
                state = State.INPUT_NEW
            }
            State.INPUT_FIRST,
            State.INPUT_SECOND,
            State.INPUT_NEW -> input.appendDecimal()
            State.SHOW_ANSWER -> {
                input.clear()
                input.appendDecimal()
                state = State.INPUT_NEW_START
            }
        }

        logRegisters("input")
    }

    fun inputAdd() {
        inputOperator(Operator.ADD)

        logRegisters("add")
    }

    fun inputMultiply() {
        inputOperator(Operator.MULTIPLY)

        logRegisters("mul")
    }

    fun inputDivide() {
        inputOperator(Operator.DIVIDE)

        logRegisters("div")
    }

    fun inputSubtract() {
        inputOperator(Operator.SUBTRACT)

        logRegisters("sub")
    }

    fun inputEqual() {
        result = when (state) {
            State.INPUT_FIRST_START,
            State.INPUT_FIRST -> {
                when (operator) {
                    Operator.NONE -> input.toDouble()
                    else -> throw IllegalStateException("operator set on input first")
                }
            }
            State.INPUT_SECOND_START -> {
                constant = if (operator == Operator.MULTIPLY) first else input.toDouble()

                when (operator) {
                    Operator.NONE -> throw IllegalStateException()
                    Operator.DIVIDE -> operator.calculate(1.0, input.toDouble())
                    else -> operator.calculate(first, input.toDouble())
                }
            }
            State.INPUT_SECOND -> {
                constant = if (operator == Operator.MULTIPLY) first else input.toDouble()

                when (operator) {
                    Operator.NONE -> throw IllegalStateException()
                    else -> operator.calculate(first, input.toDouble())
                }
            }
            State.INPUT_NEW -> {
                if (constant != null) {
                    operator.calculateConstant(constant!!, input.toDouble())
                } else {
                    throw IllegalStateException("unhandled constant is null")
                }
            }
            State.INPUT_NEW_START,
            State.SHOW_ANSWER -> {
                if (constant != null) {
                    operator.calculateConstant(constant!!, result!!)
                } else {
                    result
                }
            }
        }

        input.clear()
        state = State.SHOW_ANSWER

        logRegisters("eq")
    }

    private fun inputOperator(op: Operator) {
        if (op == Operator.NONE) {
            throw IllegalArgumentException()
        }

        when (state) {
            State.INPUT_NEW_START,
            State.INPUT_NEW,
            State.INPUT_FIRST_START,
            State.INPUT_FIRST -> {
                first = input.toDouble()
                state = State.INPUT_SECOND_START
            }
            State.INPUT_SECOND_START -> {}
            State.INPUT_SECOND -> {
                first = operator.calculate(first, input.toDouble())
                result = first
                state = State.INPUT_SECOND_START
            }
            State.SHOW_ANSWER -> {
                first = result!!
                state = State.INPUT_SECOND_START
            }
        }

        operator = op
    }

    private fun displayDouble(d: Double): String {
        val epsilon = 0.000000005

        val concise = if (abs(d.roundToLong() - d) < epsilon) {
            d.roundToLong().toString().truncateDecimal()
        } else {
            String.format("%.8f", d).truncateTrailingZeros().truncateDecimal()
        }

        return concise.withCommas()
    }

    private fun logRegisters(doing: String = "unspec") {
        println("$doing: state = $state; first = $first; result = $result; constant = $constant; op = $operator; input = $input")
    }
}

private fun String.truncateDecimal(): String =
    if (this.lastOrNull() == '.') this.substring(0, this.length - 1) else this

private fun String.truncateTrailingZeros(): String {
    var end = this.length
    for (i in this.indices.reversed()) {
        if (this[i] == '0') {
            end = i
        } else {
            break
        }
    }

    return this.substring(0, end)
}


private fun String.withCommas(): String {
    val numCharsBeforeDecimal = if (this.indexOf('.') < 0) {
        this.length
    } else {
        this.indexOf('.')
    }

    val builder = StringBuilder()

    for (i in 0 until numCharsBeforeDecimal) {
        if (i != 0 && i % 3 == numCharsBeforeDecimal % 3) {
            builder.append(',')
        }

        builder.append(this[i])
    }

    builder.append(this.substring(numCharsBeforeDecimal))

    return builder.toString()
}

/**
 * A decimal value designed to be written to by the user directly with a number sequence
 */
private class InputDecimal() {
    private var string: String = "0"

    fun appendDigit(d: Int) {
        if (string == "0") {
            string = "" + ('0' + d)
        } else {
            string += ('0' + d)
        }
    }

    fun appendDecimal() {
        if (!string.contains('.')) {
            string += '.'
        }
    }

    fun toDouble(): Double = string.toDouble()

    fun clear() {
        string = "0"
    }

    override fun toString(): String = string.withCommas()
}
