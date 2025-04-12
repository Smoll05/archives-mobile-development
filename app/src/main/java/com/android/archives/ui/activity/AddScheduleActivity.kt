package com.android.archives.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.android.archives.R
import com.android.archives.data.application.ArchivesApplication
import com.android.archives.utils.isFieldEmptyOrNull
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddScheduleActivity : AppCompatActivity() {
    private lateinit var etDate : EditText
    private lateinit var etStart : EditText
    private lateinit var etEnd : EditText
    private lateinit var etTitle : EditText
    private lateinit var etLocation : EditText


    private lateinit var tilDate : TextInputLayout
    private lateinit var tilStart : TextInputLayout
    private lateinit var tilEnd : TextInputLayout
    private lateinit var tilTitle : TextInputLayout
    private lateinit var tilLocation : TextInputLayout

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var timePicker: MaterialTimePicker
    private var existingDatePicker: Fragment? = null
    private var existingTimePicker: Fragment? = null
    private val datePickerTag = "DATE PICKER"
    private val timePickerTag = "TIME PICKER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        val app = application as ArchivesApplication

        etTitle = findViewById(R.id.add_schedule_title)
        etLocation = findViewById(R.id.add_schedule_location)
        etDate = findViewById(R.id.add_schedule_date)
        etStart = findViewById(R.id.et_start)
        etEnd = findViewById(R.id.et_end)
        val colorRadio = findViewById<RadioGroup>(R.id.add_schedule_color)
        val toolBar = findViewById<MaterialToolbar>(R.id.add_schedule_toolbar)
        val addBtn = findViewById<Button>(R.id.add_schedule_btn)

        timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Appointment time")
                .build()

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Schedule Date")
            .build()

        toolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        etTitle.addTextChangedListener {
            tilTitle.error = null
        }

        etLocation.addTextChangedListener {
            tilLocation.error = null
        }

        etDate.setOnClickListener {
            showDatePicker()
        }

        etStart.setOnClickListener {
            showTimePicker()
        }

        etEnd.setOnClickListener {
            showTimePicker()
        }

        colorRadio.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.schedule_white -> {

                }
                R.id.schedule_yellow -> {

                }
                R.id.schedule_orange -> {

                }
                R.id.schedule_red -> {

                }
                R.id.schedule_purple -> {

                }
                R.id.schedule_blue -> {

                }
                R.id.schedule_green -> {

                }
            }
        }

        addBtn.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener

            Toast.makeText(this, "The Fields Are Valid", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDatePicker() {
        if (existingDatePicker == null) {
            datePicker.show(supportFragmentManager, datePickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = convertMillisToDateString(selection)

        }

        datePicker.addOnNegativeButtonClickListener {
            // Handle negative button click (optional)
        }

        datePicker.addOnCancelListener {
            // Handle cancel event (optional)
        }

        datePicker.addOnDismissListener {
            existingDatePicker = null
        }

        existingDatePicker = datePicker
    }

    private fun showTimePicker() {
        if (existingTimePicker == null) {
            timePicker.show(supportFragmentManager, timePickerTag)
        }

        timePicker.addOnPositiveButtonClickListener {
            // call back code
        }
        timePicker.addOnNegativeButtonClickListener {
            // call back code
        }
        timePicker.addOnCancelListener {
            // call back code
        }
        timePicker.addOnDismissListener {
            existingTimePicker = null
        }

        existingTimePicker = timePicker
    }

    private fun convertMillisToDateString(millis: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    private fun areFieldsEmpty() : Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        if(etTitle.isFieldEmptyOrNull()) {
            tilTitle.error = errorMsg
            isEmpty = true
        }

        if(etLocation.isFieldEmptyOrNull()) {
            tilLocation.error = errorMsg
            isEmpty = true
        }

        if(etDate.isFieldEmptyOrNull()) {
            tilDate.error = errorMsg
            isEmpty = true
        } else {
            tilDate.error = null
        }

        if(etStart.isFieldEmptyOrNull()) {
            tilStart.error = errorMsg
            isEmpty = true
        }

        if(etEnd.isFieldEmptyOrNull()) {
            etEnd.error = errorMsg
            isEmpty = true
        }

        return isEmpty
    }
}