package au.com.carsonholloway.calculator

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.NumberFormat

class CalculatorTest {
    private lateinit var calculator: Calculator

    @Before
    fun before() {
        calculator = Calculator()
    }

    @Test
    fun `type a number`() {
        calculator.enterSequence("569")
        assertEquals("569", calculator.display)
    }

    @Test
    fun `type a fractional number`() {
        calculator.enterSequence("3.14")
        assertEquals("3.14", calculator.display)
    }

    @Test
    fun `delete last typed input`() {
        calculator.enterSequence("12.834DDDD")
        assertEquals("12", calculator.display)
    }

    @Test
    fun `delete end of input`() {
        calculator.enterSequence("123DDD")
        assertEquals("0", calculator.display)
    }

    @Test
    fun `delete past end`() {
        calculator.enterSequence("12DDDDDD")
        assertEquals("0", calculator.display)
    }

    @Test
    fun `clear current entry`() {
        calculator.enterSequence("15+35628756C15=")
        assertNumericEquivalent(30.0, calculator.display)
    }

    @Test
    fun `clear clears constant`() {
        calculator.enterSequence("2*=====C10=")
        assertNumericEquivalent(10.0, calculator.display)
    }

    @Test
    fun `one plus one`() {
        calculator.enterSequence("1+1=")
        assertNumericEquivalent(2.0, calculator.display)
    }

    @Test
    fun `add repeatedly`() {
        calculator.enterSequence("1+1=")
        assertNumericEquivalent(2.0, calculator.display)

        calculator.inputEqual()
        assertNumericEquivalent(3.0, calculator.display)

        calculator.inputEqual()
        assertNumericEquivalent(4.0, calculator.display)
    }

    @Test
    fun `powers of two`() {
        calculator.enterSequence("2*=")
        assertNumericEquivalent(4.0, calculator.display)

        calculator.inputEqual()
        assertNumericEquivalent(8.0, calculator.display)

        calculator.inputEqual()
        assertNumericEquivalent(16.0, calculator.display)

        calculator.inputEqual()
        assertNumericEquivalent(32.0, calculator.display)
    }

    @Test
    fun `multiply several elements before equal`() {
        calculator.enterSequence("3*4*6=")
        assertNumericEquivalent(72.0, calculator.display)
    }

    @Test
    fun `multiply floats`() {
        calculator.enterSequence("2.5*2.5=")
        assertNumericEquivalent(6.25, calculator.display)
    }

    @Test
    fun `large numbers`() {
        calculator.enterSequence("101.5*101.25=")
        assertNumericEquivalent(10_276.875, calculator.display)
    }

    @Test
    fun `set the constant multiply`() {
        calculator.enterSequence("2*3=4=")
        assertNumericEquivalent(8.0, calculator.display)
    }

    @Test
    fun `multiple operations`() {
        calculator.enterSequence("8/2*3=")
        assertNumericEquivalent(12.0, calculator.display)
    }

    @Test
    fun `compute reciprocal`() {
        calculator.enterSequence("4/=")

        assertNumericEquivalent(0.25, calculator.display)
    }

    @Test
    fun `new equation does not show old values`() {
        calculator.enterSequence("1+2=5*")
        assertNumericEquivalent(5.0, calculator.display)
    }

    @Test
    fun `add to answer`() {
        calculator.enterSequence("6*4=+1=")
        assertNumericEquivalent(25.0, calculator.display)
    }

    @Test
    fun `equals initially does nothing`() {
        calculator.enterSequence("10=")
        assertNumericEquivalent(10.0, calculator.display)
    }

    @Test
    fun `add and recall memory`() {
        calculator.enterSequence("3M+C5*MR=")
        assertNumericEquivalent(15.0, calculator.display)
    }

    @Test
    fun `clear all does not clear memory`() {
        calculator.enterSequence("3M+CC5*MR=")
        assertNumericEquivalent(15.0, calculator.display)
    }

    @Test
    fun `memory add does not clear display`() {
        calculator.enterSequence("5M+")
        assertNumericEquivalent(5.0, calculator.display)
    }

    @Test
    fun `compute polynomial with memory`() {
        // let us compute 2x^2 + 3x + 5 at x = 2.5
        calculator.enterSequence("2.5*=*2=M+2.5*3=M+5M+MR")
        assertNumericEquivalent(25.0, calculator.display)
    }

    @Test
    fun `memory freezes the display`() {
        calculator.enterSequence("8M+5M-MR")
        assertNumericEquivalent(3.0, calculator.display)
    }

    @Test
    fun `all clear and clear entry are same button`() {
        calculator.enterSequence("2+3C5=")
        assertNumericEquivalent(7.0, calculator.display)
        calculator.enterSequence("*3CC1=")
        assertNumericEquivalent(1.0, calculator.display)
    }

    @Test
    fun `all clear appears on frozen input`() {
        assertEquals(Calculator.ClearText.CLEAR_ALL, calculator.buttonClearText)
        calculator.enterSequence("15")
        assertEquals(Calculator.ClearText.CLEAR_ENTRY, calculator.buttonClearText)
        calculator.enterSequence("C1+1=")
        assertEquals(Calculator.ClearText.CLEAR_ALL, calculator.buttonClearText)
    }

    @Test
    fun `negate a number`() {
        calculator.enterSequence("15N=")
        assertNumericEquivalent(-15.0, calculator.display)
    }

    @Test
    fun `negate freezes the number`() {
        calculator.enterSequence("32N3=")
        assertNumericEquivalent(3.0, calculator.display)
    }

    @Test
    fun `negate the result of an equation`() {
        calculator.enterSequence("5+7=N")
        assertNumericEquivalent(-12.0, calculator.display)
    }

    @Test
    fun `take a square root`() {
        calculator.enterSequence("4Q=")
        assertNumericEquivalent(2.0, calculator.display)
    }

    @Test
    fun `take square root of second term`() {
        calculator.enterSequence("5+4Q=")
        assertNumericEquivalent(7.0, calculator.display)
    }

    @Test
    fun `take a square root of a result`() {
        calculator.enterSequence("6+3=Q")
        assertNumericEquivalent(3.0, calculator.display)
    }

    @Test
    fun `square root freezes the number`() {
        calculator.enterSequence("2Q3+2=")
        assertNumericEquivalent(5.0, calculator.display)
    }

    @Test
    fun `double negate`() {
        calculator.enterSequence("12NN=")
        assertNumericEquivalent(12.0, calculator.display)
    }

    @Test
    fun `handles divide by zero`() {
        calculator.enterSequence("1/0=")
        // any numeric description of this is wrong
        assertEquals(null, calculator.display.toDoubleOrNull())
    }

    @Test
    fun `displays negative three-digit numbers`() {
        calculator.enterSequence("80*10N=")
        assertNumericEquivalent(-800.0, calculator.display)
    }

    @Test
    fun `allows user to set the maximum digits to input`() {
        calculator.maxAllowedDigits = 4
        calculator.enterSequence("12345")

        assertNumericEquivalent(1234.0, calculator.display)
    }

    @Test
    fun `max allowed digits does not include decimal separator`() {
        calculator.maxAllowedDigits = 6
        calculator.enterSequence("1234.56")
        assertNumericEquivalent(1234.56, calculator.display)
    }

    private fun assertNumericEquivalent(value: Double, actual: String) {
        val delta = 0.001
        val numberFormat = NumberFormat.getNumberInstance()
        assertEquals(
            value,
            numberFormat.parse(actual)!!.toDouble(),
            delta
        )
    }

    private fun Calculator.enterSequence(sequence: String) {
        var i = 0
        while (i < sequence.length) {
            when (sequence[i]) {
                '0' -> this.inputDigit(0)
                '1' -> this.inputDigit(1)
                '2' -> this.inputDigit(2)
                '3' -> this.inputDigit(3)
                '4' -> this.inputDigit(4)
                '5' -> this.inputDigit(5)
                '6' -> this.inputDigit(6)
                '7' -> this.inputDigit(7)
                '8' -> this.inputDigit(8)
                '9' -> this.inputDigit(9)
                '.' -> this.inputDecimal()
                '+' -> this.inputAdd()
                '-' -> this.inputSubtract()
                '*' -> this.inputMultiply()
                '/' -> this.inputDivide()
                '=' -> this.inputEqual()
                'D' -> this.inputDelete()
                'C' -> this.inputClear()
                'N' -> this.inputNegate()
                'Q' -> this.inputSquareRoot()
                'M' -> when (sequence[++i]) {
                    '+' -> this.inputMemoryAdd()
                    '-' -> this.inputMemorySubtract()
                    'R' -> this.inputMemoryRecall()
                    'C' -> this.inputMemoryClear()
                    else -> throw IllegalArgumentException("unexpected char '${sequence[i]}' after M")
                }
                else -> throw IllegalArgumentException("unexpected sequence char ${sequence[i]}")
            }

            i++
        }
    }
}