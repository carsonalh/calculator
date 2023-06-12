package au.com.carsonholloway.calculator

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale

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
    fun `clear current entry`() {
        calculator.enterSequence("15+35628756E15=")
        assertNumericEquivalent("30", calculator.display)
    }

    @Test
    fun `clear clears constant`() {
        calculator.enterSequence("2*=====C10=")
        assertNumericEquivalent("10", calculator.display)
    }

    @Test
    fun `one plus one`() {
        calculator.enterSequence("1+1=")
        assertNumericEquivalent("2", calculator.display)
    }

    @Test
    fun `add repeatedly`() {
        calculator.enterSequence("1+1=")
        assertNumericEquivalent("2", calculator.display)

        calculator.inputEqual()
        assertNumericEquivalent("3", calculator.display)

        calculator.inputEqual()
        assertNumericEquivalent("4", calculator.display)
    }

    @Test
    fun `multiply several elements before equal`() {
        calculator.enterSequence("3*4*6=")
        assertNumericEquivalent("72", calculator.display)
    }

    @Test
    fun `multiply floats`() {
        calculator.enterSequence("2.5*2.5=")
        assertNumericEquivalent("6.25", calculator.display)
    }

    @Test
    fun `large numbers`() {
        calculator.enterSequence("101.5*101.25=")

        assertNumericEquivalent("10,276.875", calculator.display)
    }

    @Test
    fun `set the constant multiply`() {
        calculator.enterSequence("2*3=4=")

        assertNumericEquivalent("8", calculator.display)
    }

    @Test
    fun `multiple operations`() {
        calculator.enterSequence("8/2*3=")

        assertNumericEquivalent("12", calculator.display)
    }

    @Test
    fun `compute reciprocal`() {
        calculator.enterSequence("4/=")

        assertNumericEquivalent("0.25", calculator.display)
    }

    @Test
    fun `new equation does not show old values`() {
        calculator.enterSequence("1+2=5*")
        assertNumericEquivalent("5", calculator.display)
    }

    @Test
    fun `add to answer`() {
        calculator.enterSequence("6*4=+1=")
        assertNumericEquivalent("25", calculator.display)
    }

    @Test
    fun `equals initially does nothing`() {
        calculator.enterSequence("10=")
        assertNumericEquivalent("10", calculator.display)
    }

    private fun assertNumericEquivalent(expected: String, actual: String) {
        val delta = 0.001
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        assertEquals(
            numberFormat.parse(expected)!!.toDouble(),
            numberFormat.parse(actual)!!.toDouble(),
            delta
        )
    }

    private fun Calculator.enterSequence(sequence: String) {
        for (c in sequence) {
            when (c) {
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
                'E' -> this.inputClearEntry()
                else -> throw IllegalArgumentException("unexpected sequence char $c")
            }
        }
    }
}