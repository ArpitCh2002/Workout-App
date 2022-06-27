package com.example.workoutapp

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.workoutapp.databinding.ActivityBmiBinding
import com.example.workoutapp.databinding.DialogCustomBackConfirmationBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Matcher
import java.util.regex.Pattern


class ActivityBMI : AppCompatActivity() {

    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }

    private var binding: ActivityBmiBinding? = null
    private var currentVisibleView: String = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calculate BMI"

        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            }
            else {
                makeVisibleUSUnitsView()
            }
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }

        binding?.etMetricUnitWeight?.filters = (arrayOf<InputFilter>(DecimalDigitsInputFilter(3, 2)))
        binding?.etUSMetricUnitWeight?.filters = (arrayOf<InputFilter>(DecimalDigitsInputFilter(3, 2)))

    }

    private fun calculateUnits() {
        if (currentVisibleView == METRIC_UNITS_VIEW) {
            if (validateMetricUnits()) {
                val heightValue : Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100

                val weightValue : Float = binding?.etMetricUnitWeight?.text.toString().toFloat()

                val bmi = weightValue / (heightValue*heightValue)
                displayBMIResult(bmi)
            }
            else {
                Toast.makeText(this, "Please enter Valid values", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            if (validateUSUnits()) {
                val usUnitHeightValueFeet : String = binding?.etUSMetricUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch : String = binding?.etUSMetricUnitHeightInch?.text.toString()
                val usUnitWeightValue : Float = binding?.etUSMetricUnitWeight?.text.toString().toFloat()

                val heightValue = usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12

                val bmi = 703 * (usUnitWeightValue / (heightValue*heightValue))

                displayBMIResult(bmi)
            }
            else {
                Toast.makeText(this, "Please Enter Valid Values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW

        binding?.tilMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE
        binding?.tilUSMetricUnitWeight?.visibility = View.GONE
        binding?.tilMetricUSUnitHeightFeet?.visibility = View.GONE
        binding?.tilMetricUSUnitHeightInch?.visibility = View.GONE

        binding?.etMetricUnitHeight?.text!!.clear()
        binding?.etMetricUnitWeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun makeVisibleUSUnitsView() {
        currentVisibleView = US_UNITS_VIEW

        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE
        binding?.tilMetricUnitWeight?.visibility = View.INVISIBLE
        binding?.tilUSMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUSUnitHeightFeet?.visibility = View.VISIBLE
        binding?.tilMetricUSUnitHeightInch?.visibility = View.VISIBLE

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun validateMetricUnits(): Boolean {
        var isValid = true

        if (binding?.etMetricUnitWeight?.text.toString().isEmpty()) {
            isValid = false
        }
        else if (binding?.etMetricUnitHeight?.text.toString().isEmpty()) {
            isValid = false
        }

        return isValid
    }

    private fun validateUSUnits(): Boolean {
        var isValid = true

        when {
            binding?.etUSMetricUnitWeight?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.etUSMetricUnitHeightFeet?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.etUSMetricUnitHeightInch?.text.toString().isEmpty() -> {
                isValid = true
            }
        }

        return isValid
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)

        val dialogBinding = DialogCustomBackConfirmationBmiBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener {
            this@ActivityBMI.finish()
            customDialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }
    
    private fun displayBMIResult(bmi: Float) {
        val bmiLabel : String
        val bmiDescription : String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorSevUnWeight
                )
            )
        }
        else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorUnderWeight
                )
            )
        }
        else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorYellow
                )
            )
        }
        else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorAccent
                )
            )
        }
        else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(bmi, 30f) <= 0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout regularly!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.obeseMod
                )
            )
        }
        else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout regularly!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.obeseMod
                )
            )
        }
        else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.obeseSev
                )
            )
        }
        else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
            binding?.tvBMIValue?.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.colorRed
                )
            )
        }

        binding?.llDisplayBMIResult?.visibility = View.VISIBLE

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription
    }

    class DecimalDigitsInputFilter(digitsBeforeDecimal: Int, digitsAfterDecimal: Int) : InputFilter {

        var mPattern: Pattern = Pattern.compile("[0-9]{0,$digitsBeforeDecimal}+((\\.[0-9]{0,$digitsAfterDecimal})?)||(\\.)?")

        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val matcher: Matcher = mPattern.matcher(
                dest?.subSequence(0, dstart).toString() + source?.subSequence(
                    start,
                    end
                ).toString() + dest?.subSequence(dend, dest.length).toString()
            )
            if (!matcher.matches())
                return ""
            else
                return null
        }
    }
}
