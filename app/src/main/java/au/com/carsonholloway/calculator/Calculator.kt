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
        /**About to edit the first input of the equation; displaying the value of "first." */
        FIRST_FROZEN,
        /**Editing the first input of the equation. */
        FIRST_EDIT,
        /**About to edit the second input of the equation; displaying the value of "first." */
        SECOND_FROZEN,
        /**Editing the second input of the equation. */
        SECOND_EDIT,
    }

    private var first: Double = 0.0
    private var constant: Double? = null
    private var state: State = State.FIRST_FROZEN
    private var operator: Operator = Operator.NONE
    private var input: InputDecimal = InputDecimal()

    val display: String
        get() = when (state) {
            State.FIRST_FROZEN,
            State.SECOND_FROZEN -> displayDouble(first)
            State.FIRST_EDIT,
            State.SECOND_EDIT -> input.toString()
        }

    fun inputDigit(digit: Int) {
        if (digit < 0 || digit > 9) {
            throw IllegalArgumentException("expected a single digit input")
        }

        when (state) {
            State.FIRST_FROZEN -> {
                input.clear()
                input.appendDigit(digit)
                state = State.FIRST_EDIT
            }
            State.SECOND_FROZEN -> {
                input.clear()
                input.appendDigit(digit)
                state = State.SECOND_EDIT
            }
            State.FIRST_EDIT,
            State.SECOND_EDIT -> input.appendDigit(digit)
        }

        logRegisters("input")
    }

    fun inputDecimal() {
        when (state) {
            State.FIRST_FROZEN -> {
                input.clear()
                input.appendDecimal()
                state = State.FIRST_EDIT
            }
            State.SECOND_FROZEN -> {
                input.clear()
                input.appendDecimal()
                state = State.SECOND_EDIT
            }
            State.FIRST_EDIT,
            State.SECOND_EDIT -> input.appendDecimal()
        }

        logRegisters("input")
    }

    fun inputClear() {
        input.clear()
        constant = null
        first = 0.0
        operator = Operator.NONE
        state = State.FIRST_FROZEN
    }

    fun inputClearEntry() {
        input.clear()

        state = when (state) {
            State.FIRST_EDIT, State.FIRST_FROZEN -> State.FIRST_EDIT
            State.SECOND_EDIT, State.SECOND_FROZEN -> State.SECOND_EDIT
        }
    }

    fun inputDelete() {
        input.deleteLast()
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
        first = when (state) {
            State.SECOND_FROZEN -> {
                constant = first

                when (operator) {
                    Operator.NONE -> throw IllegalStateException("operator must be set on second")
                    Operator.DIVIDE -> operator.calculate(1.0, first)
                    else -> operator.calculate(first, first)
                }
            }
            State.SECOND_EDIT -> {
                constant = if (operator == Operator.MULTIPLY) first else input.toDouble()

                when (operator) {
                    Operator.NONE -> throw IllegalStateException("operator must be set on second")
                    else -> operator.calculate(first, input.toDouble())
                }
            }
            State.FIRST_FROZEN -> {
                if (operator == Operator.NONE) {
                    first
                } else if (constant != null) {
                    operator.calculateConstant(constant!!, first)
                } else {
                    throw IllegalStateException("cannot have operator and no constant")
                }
            }
            State.FIRST_EDIT -> {
                if (operator == Operator.NONE) {
                    input.toDouble()
                } else if (constant != null) {
                    operator.calculateConstant(constant!!, input.toDouble())
                } else {
                    throw IllegalStateException("cannot have operator and no constant")
                }
            }
        }

        input.clear()
        state = State.FIRST_FROZEN

        logRegisters("eq")
    }

    private fun inputOperator(op: Operator) {
        if (op == Operator.NONE) {
            throw IllegalArgumentException()
        }

        when (state) {
            State.FIRST_FROZEN -> {
                state = State.SECOND_FROZEN
            }
            State.FIRST_EDIT -> {
                first = input.toDouble()
                state = State.SECOND_FROZEN
            }
            State.SECOND_FROZEN -> {
                // change of mind from one operator to the next; no-op
            }
            State.SECOND_EDIT -> {
                first = operator.calculate(first, input.toDouble())
                state = State.SECOND_FROZEN
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
        println("$doing: state = $state; first = $first; constant = $constant; op = $operator; input = $input")
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

    fun deleteLast() {
        if (string.isEmpty()) {
            throw IllegalStateException()
        }

        string = if (string.length - 1 == 0) "0" else string.substring(0, string.length - 1)
    }

    fun toDouble(): Double = string.toDouble()

    fun clear() {
        string = "0"
    }

    override fun toString(): String = string.withCommas()
}