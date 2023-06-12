package au.com.carsonholloway.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private val calculator = Calculator()
    private lateinit var display: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculator)

        display = findViewById(R.id.display)
        display.text = calculator.display

        val buttonClear = findViewById<Button>(R.id.button_c)
        val buttonClearEntry = findViewById<Button>(R.id.button_clear_entry)
        val buttonDelete = findViewById<Button>(R.id.button_delete)
        val button0 = findViewById<Button>(R.id.button_0)
        val button1 = findViewById<Button>(R.id.button_1)
        val button2 = findViewById<Button>(R.id.button_2)
        val button3 = findViewById<Button>(R.id.button_3)
        val button4 = findViewById<Button>(R.id.button_4)
        val button5 = findViewById<Button>(R.id.button_5)
        val button6 = findViewById<Button>(R.id.button_6)
        val button7 = findViewById<Button>(R.id.button_7)
        val button8 = findViewById<Button>(R.id.button_8)
        val button9 = findViewById<Button>(R.id.button_9)
        val buttonAdd = findViewById<Button>(R.id.button_add)
        val buttonMultiply = findViewById<Button>(R.id.button_multiply)
        val buttonSubtract = findViewById<Button>(R.id.button_subtract)
        val buttonDivide = findViewById<Button>(R.id.button_divide)
        val buttonDecimal = findViewById<Button>(R.id.button_decimal)
        val buttonEqual = findViewById<Button>(R.id.button_equal)

        buttonClear.setOnClickListener { calculator.inputClear(); updateDisplay() }
        buttonClearEntry.setOnClickListener { calculator.inputClearEntry(); updateDisplay() }
        buttonDelete.setOnClickListener { calculator.inputDelete(); updateDisplay() }
        button0.setOnClickListener { calculator.inputDigit(0); updateDisplay() }
        buttonDecimal.setOnClickListener { calculator.inputDecimal(); updateDisplay() }
        buttonEqual.setOnClickListener { calculator.inputEqual(); updateDisplay() }
        button1.setOnClickListener { calculator.inputDigit(1); updateDisplay() }
        button2.setOnClickListener { calculator.inputDigit(2); updateDisplay() }
        button3.setOnClickListener { calculator.inputDigit(3); updateDisplay() }
        button4.setOnClickListener { calculator.inputDigit(4); updateDisplay() }
        button5.setOnClickListener { calculator.inputDigit(5); updateDisplay() }
        button6.setOnClickListener { calculator.inputDigit(6); updateDisplay() }
        button7.setOnClickListener { calculator.inputDigit(7); updateDisplay() }
        button8.setOnClickListener { calculator.inputDigit(8); updateDisplay() }
        button9.setOnClickListener { calculator.inputDigit(9); updateDisplay() }
        buttonAdd.setOnClickListener { calculator.inputAdd(); updateDisplay() }
        buttonMultiply.setOnClickListener { calculator.inputMultiply(); updateDisplay() }
        buttonSubtract.setOnClickListener { calculator.inputSubtract(); updateDisplay() }
        buttonDivide.setOnClickListener { calculator.inputDivide(); updateDisplay() }
    }

    private fun updateDisplay() {
        display.text = calculator.display
    }
}

