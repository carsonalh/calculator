package au.com.carsonholloway.calculator

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

class Calculator {

    enum class ClearText {
        CLEAR_ENTRY,
        CLEAR_ALL,
    }

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

    var maxAllowedDigits: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("max allowed digits must be >= 0")
            }

            field = value
        }

    private var first: Double = 0.0
    private var second: Double = 0.0
    private var constant: Double? = null
    private var state: State = State.FIRST_FROZEN
    private var operator: Operator = Operator.NONE
    private var input: InputDecimal = InputDecimal()
    private var memory: Double? = null

    val buttonClearText: ClearText
        get() = when(state) {
            State.FIRST_FROZEN, State.SECOND_FROZEN -> ClearText.CLEAR_ALL
            State.FIRST_EDIT, State.SECOND_EDIT -> ClearText.CLEAR_ENTRY
        }

    val display: String
        get() = when (state) {
            State.FIRST_FROZEN -> displayDouble(first)
            State.SECOND_FROZEN -> displayDouble(second)

            State.FIRST_EDIT,
            State.SECOND_EDIT -> input.toString()
        }

    fun inputDigit(digit: Int) {
        if (digit < 0 || digit > 9) {
            throw IllegalArgumentException("expected a single digit input")
        }

        if (maxAllowedDigits > 0) {
            val rawInput = input.rawString()

            val countedLength = rawInput.length - if (rawInput.contains('.')) 1 else 0

            if (countedLength > maxAllowedDigits) {
                throw IllegalStateException()
            } else if (countedLength == maxAllowedDigits) {
                return
            } // otherwise, we can continue just fine
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
        when (state) {
            State.FIRST_FROZEN, State.SECOND_FROZEN -> {
                // clear all
                input.clear()
                constant = null
                first = 0.0
                second = 0.0
                operator = Operator.NONE
                state = State.FIRST_FROZEN
            }
            State.FIRST_EDIT -> {
                // clear entry
                input.clear()
                state = State.FIRST_FROZEN
            }
            State.SECOND_EDIT -> {
                // clear entry
                input.clear()
                state = State.SECOND_FROZEN
            }
        }

        logRegisters("clear")
    }

    fun inputNegate() {
        when (state) {
            State.FIRST_EDIT -> {
                first = -input.toDouble()
                state = State.FIRST_FROZEN
            }
            State.SECOND_EDIT -> {
                second = -input.toDouble()
                state = State.SECOND_FROZEN
            }
            State.FIRST_FROZEN -> {
                first = -first
            }
            State.SECOND_FROZEN -> {
                second = -second
            }
        }
    }

    fun inputSquareRoot() {
        when (state) {
            State.FIRST_EDIT -> {
                first = sqrt(input.toDouble())
                state = State.FIRST_FROZEN
            }
            State.SECOND_EDIT -> {
                second = sqrt(input.toDouble())
                state = State.SECOND_FROZEN
            }
            State.FIRST_FROZEN -> {
                first = sqrt(first)
            }
            State.SECOND_FROZEN -> {
                second = sqrt(second)
            }
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
                constant = when (operator) {
                    Operator.NONE -> throw IllegalStateException("operator must be set on second")
                    Operator.MULTIPLY -> first
                    Operator.ADD, Operator.SUBTRACT, Operator.DIVIDE -> second
                }

                when (operator) {
                    Operator.NONE -> throw IllegalStateException("operator must be set on second")
                    Operator.DIVIDE -> operator.calculate(1.0, first)
                    else -> operator.calculate(first, second)
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

    fun inputMemoryAdd() {
        memory = (memory ?: 0.0) + when (state) {
            State.FIRST_FROZEN -> first
            State.SECOND_FROZEN -> second
            State.FIRST_EDIT -> {
                state = State.FIRST_FROZEN
                first = input.toDouble()
                first
            }
            State.SECOND_EDIT -> {
                state = State.SECOND_FROZEN
                second = input.toDouble()
                second
            }
        }

        logRegisters("m+")
    }

    fun inputMemorySubtract() {
        memory = (memory ?: 0.0) - when (state) {
            State.FIRST_FROZEN -> first
            State.SECOND_FROZEN -> second
            State.FIRST_EDIT -> {
                state = State.FIRST_FROZEN
                first = input.toDouble()
                first
            }
            State.SECOND_EDIT -> {
                state = State.SECOND_FROZEN
                second = input.toDouble()
                second
            }
        }

        logRegisters("m-")
    }

    fun inputMemoryRecall() {
        if (memory != null) {
            when (state) {
                State.FIRST_FROZEN -> first = memory!!
                State.SECOND_FROZEN -> second = memory!!
                State.FIRST_EDIT -> {
                    first = memory!!
                    state = State.FIRST_FROZEN
                }
                State.SECOND_EDIT -> {
                    second = memory!!
                    state = State.SECOND_FROZEN
                }
            }
        }

        // if memory is null, MR is a no-op

        logRegisters("mr")
    }

    fun inputMemoryClear() {
        memory = null

        logRegisters("mc")
    }

    private fun inputOperator(op: Operator) {
        if (op == Operator.NONE) {
            throw IllegalArgumentException()
        }

        when (state) {
            State.FIRST_FROZEN -> {
                second = first
                state = State.SECOND_FROZEN
            }
            State.FIRST_EDIT -> {
                first = input.toDouble()
                second = first
                state = State.SECOND_FROZEN
            }
            State.SECOND_FROZEN -> {
                // change of mind from one operator to the next; no-op
            }
            State.SECOND_EDIT -> {
                first = operator.calculate(first, input.toDouble())
                second = first
                state = State.SECOND_FROZEN
            }
        }

        operator = op
    }

    private fun displayDouble(d: Double): String {
        return if (d == 0.0 || 1e-4 <= abs(d) && abs(d) < 1e12) {
            DecimalFormat("#,###.#####").format(d)
        } else if (d.isNaN()) {
            "NaN" // TODO internationalise
        } else {
            DecimalFormat("0.#####E0").format(d).lowercase()
        }
    }

    private fun logRegisters(doing: String = "unspec") {
        println("$doing: state = $state; first = $first; second = $second; memory = $memory; constant = $constant; op = $operator; input = $input")
    }

}

/**
 * A decimal value designed to be written to by the user directly with a number sequence
 */
private class InputDecimal() {
    private var integerPart: String = "0"
    private var decimalSet: Boolean = false
    private var fractionalPart: String = ""

    fun appendDigit(d: Int) {
        if (d < 0 || d > 9) {
            throw IllegalArgumentException("input must be one digit")
        }

        if (decimalSet) {
            fractionalPart += ('0' + d)
        } else if (integerPart == "0") {
            integerPart = "" + ('0' + d)
        } else {
            integerPart += ('0' + d)
        }
    }

    fun appendDecimal() {
        decimalSet = true
    }

    fun deleteLast() {
        if (fractionalPart.isNotEmpty()) {
            fractionalPart = fractionalPart.substring(0 until fractionalPart.length - 1)
        } else if (decimalSet) {
            decimalSet = false
        } else if (integerPart.length >= 2) {
            integerPart = integerPart.substring(0 until integerPart.length - 1)
        } else if (integerPart.length == 1) {
            integerPart = "0"
        } else {
            throw IllegalStateException()
        }
    }

    fun toDouble(): Double {
        return NumberFormat
            .getInstance(Locale.US)
            .parse("$integerPart.$fractionalPart")!!
            .toDouble()
    }

    fun clear() {
        integerPart = "0"
        decimalSet = false
        fractionalPart = ""
    }

    override fun toString(): String {
        val decimalFormat = DecimalFormat.getInstance() as DecimalFormat
        val separator = decimalFormat.decimalFormatSymbols.decimalSeparator

        decimalFormat.isParseBigDecimal = true
        val integerBigDecimal = decimalFormat.parse(integerPart) as BigDecimal

        val builder = StringBuilder()

        builder.append(DecimalFormat("#,###").format(integerBigDecimal))

        if (decimalSet) {
            builder.append(separator)
            builder.append(fractionalPart)
        } else if (fractionalPart != "") {
            throw IllegalStateException()
        }

        return builder.toString()
    }

    fun rawString(): String {
        val builder = StringBuilder()

        builder.append(integerPart)

        if (decimalSet) {
            builder.append('.')
            builder.append(fractionalPart)
        }

        return builder.toString()
    }
}
