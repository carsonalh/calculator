package au.com.carsonholloway.calculator

import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private val calculator = Calculator()

    private lateinit var display: TextView
    private lateinit var buttonClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        display.text = calculator.display

        buttonClear = findViewById(R.id.buttonClear)
        buttonClear.text = getString(R.string.clear_all)

        val buttonDelete = findViewById<View>(R.id.buttonDelete)
        val button0 = findViewById<View>(R.id.button0)
        val button1 = findViewById<View>(R.id.button1)
        val button2 = findViewById<View>(R.id.button2)
        val button3 = findViewById<View>(R.id.button3)
        val button4 = findViewById<View>(R.id.button4)
        val button5 = findViewById<View>(R.id.button5)
        val button6 = findViewById<View>(R.id.button6)
        val button7 = findViewById<View>(R.id.button7)
        val button8 = findViewById<View>(R.id.button8)
        val button9 = findViewById<View>(R.id.button9)
        val buttonAdd = findViewById<View>(R.id.buttonAdd)
        val buttonMultiply = findViewById<View>(R.id.buttonMultiply)
        val buttonSubtract = findViewById<View>(R.id.buttonSubtract)
        val buttonDivide = findViewById<View>(R.id.buttonDivide)
        val buttonDecimal = findViewById<View>(R.id.buttonDecimal)
        val buttonEqual = findViewById<View>(R.id.buttonEqual)
        val buttonMemoryAdd = findViewById<View>(R.id.buttonMemoryAdd)
        val buttonMemorySubtract = findViewById<View>(R.id.buttonMemorySubtract)
        val buttonMemoryRecall = findViewById<View>(R.id.buttonMemoryRecall)
        val buttonMemoryClear = findViewById<View>(R.id.buttonMemoryClear)
        val buttonNegate = findViewById<View>(R.id.buttonNegate)
        val buttonSquareRoot = findViewById<View>(R.id.buttonSquareRoot)

        buttonClear.setOnClickListener { calculator.inputClear(); onButtonClick(it) }
        buttonDelete.setOnClickListener { calculator.inputDelete(); onButtonClick(it) }
        button0.setOnClickListener { calculator.inputDigit(0); onButtonClick(it) }
        buttonDecimal.setOnClickListener { calculator.inputDecimal(); onButtonClick(it) }
        buttonEqual.setOnClickListener { calculator.inputEqual(); onButtonClick(it) }
        button1.setOnClickListener { calculator.inputDigit(1); onButtonClick(it) }
        button2.setOnClickListener { calculator.inputDigit(2); onButtonClick(it) }
        button3.setOnClickListener { calculator.inputDigit(3); onButtonClick(it) }
        button4.setOnClickListener { calculator.inputDigit(4); onButtonClick(it) }
        button5.setOnClickListener { calculator.inputDigit(5); onButtonClick(it) }
        button6.setOnClickListener { calculator.inputDigit(6); onButtonClick(it) }
        button7.setOnClickListener { calculator.inputDigit(7); onButtonClick(it) }
        button8.setOnClickListener { calculator.inputDigit(8); onButtonClick(it) }
        button9.setOnClickListener { calculator.inputDigit(9); onButtonClick(it) }
        buttonAdd.setOnClickListener { calculator.inputAdd(); onButtonClick(it) }
        buttonMultiply.setOnClickListener { calculator.inputMultiply(); onButtonClick(it) }
        buttonSubtract.setOnClickListener { calculator.inputSubtract(); onButtonClick(it) }
        buttonDivide.setOnClickListener { calculator.inputDivide(); onButtonClick(it) }
        buttonMemoryAdd.setOnClickListener { calculator.inputMemoryAdd(); onButtonClick(it) }
        buttonMemorySubtract.setOnClickListener { calculator.inputMemorySubtract(); onButtonClick(it) }
        buttonMemoryRecall.setOnClickListener { calculator.inputMemoryRecall(); onButtonClick(it) }
        buttonMemoryClear.setOnClickListener { calculator.inputMemoryClear(); onButtonClick(it) }
        buttonNegate.setOnClickListener { calculator.inputNegate(); onButtonClick(it) }
        buttonSquareRoot.setOnClickListener { calculator.inputSquareRoot(); onButtonClick(it) }
    }

    private fun onButtonClick(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
        }

        display.text = calculator.display
        buttonClear.text = when (calculator.buttonClearText) {
            Calculator.ClearText.CLEAR_ENTRY -> getString(R.string.clear_entry)
            Calculator.ClearText.CLEAR_ALL -> getString(R.string.clear_all)
        }
    }
}

