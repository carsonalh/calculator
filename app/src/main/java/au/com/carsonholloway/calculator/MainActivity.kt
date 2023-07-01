package au.com.carsonholloway.calculator

import android.os.Build
import android.os.Bundle
import android.text.TextPaint
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    private val maxInputLength = 18
    private val calculator = Calculator()

    private var displayInitialTextSizePx: Float = 0f
    private lateinit var display: TextView
    private lateinit var buttonClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calculator.maxAllowedDigits = maxInputLength

        display = findViewById(R.id.display)
        display.text = calculator.display

        buttonClear = findViewById(R.id.buttonClear)
        buttonClear.text = getString(R.string.clear_all)

        val buttonDelete = findViewById<ImageButton>(R.id.buttonDelete)
        val button0 = findViewById<Button>(R.id.button0)
        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        val button5 = findViewById<Button>(R.id.button5)
        val button6 = findViewById<Button>(R.id.button6)
        val button7 = findViewById<Button>(R.id.button7)
        val button8 = findViewById<Button>(R.id.button8)
        val button9 = findViewById<Button>(R.id.button9)
        val buttonAdd = findViewById<ImageButton>(R.id.buttonAdd)
        val buttonMultiply = findViewById<ImageButton>(R.id.buttonMultiply)
        val buttonSubtract = findViewById<ImageButton>(R.id.buttonSubtract)
        val buttonDivide = findViewById<ImageButton>(R.id.buttonDivide)
        val buttonDecimal = findViewById<Button>(R.id.buttonDecimal)
        val buttonEqual = findViewById<ImageButton>(R.id.buttonEqual)
        val buttonMemoryAdd = findViewById<Button>(R.id.buttonMemoryAdd)
        val buttonMemorySubtract = findViewById<Button>(R.id.buttonMemorySubtract)
        val buttonMemoryRecall = findViewById<Button>(R.id.buttonMemoryRecall)
        val buttonMemoryClear = findViewById<Button>(R.id.buttonMemoryClear)
        val buttonNegate = findViewById<ImageButton>(R.id.buttonNegate)
        val buttonSquareRoot = findViewById<ImageButton>(R.id.buttonSquareRoot)

        val decimalSeparator = (DecimalFormat.getInstance() as DecimalFormat).decimalFormatSymbols.decimalSeparator
        buttonDecimal.text = decimalSeparator.toString()

        displayInitialTextSizePx = display.textSize

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

        updateDisplay()
        buttonClear.text = when (calculator.buttonClearText) {
            Calculator.ClearText.CLEAR_ENTRY -> getString(R.string.clear_entry)
            Calculator.ClearText.CLEAR_ALL -> getString(R.string.clear_all)
        }
    }

    private fun updateDisplay() {
        // works under the assumption that increased sizes of the display's font do not decrease a
        // rendered character's width

        val paint = TextPaint(display.paint)

        paint.textSize = displayInitialTextSizePx

        val availableWidth = display.width - display.paddingLeft - display.paddingRight

        if (paint.measureText(calculator.display) <= availableWidth) {
            display.setTextSize(TypedValue.COMPLEX_UNIT_PX, displayInitialTextSizePx)
            display.text = calculator.display
            return
        }

        val downsizeIncrementPx = 1.0f

        // let us downsize the text until it fits

        while (paint.measureText(calculator.display) > availableWidth) {
            paint.textSize -= downsizeIncrementPx
        }

        display.setTextSize(TypedValue.COMPLEX_UNIT_PX, paint.textSize)
        display.text = calculator.display
    }
}

