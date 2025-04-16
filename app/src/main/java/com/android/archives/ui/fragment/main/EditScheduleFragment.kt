package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.application.ArchivesApplication
import com.android.archives.databinding.FragmentEditScheduleBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.isFieldEmptyOrNull
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EditScheduleFragment : DialogFragment() {
    private var _binding: FragmentEditScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()
    
    // SAME SAME RANI SIYA SA ADD SCHEDULE FRAGMENT
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
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEditScheduleBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as ArchivesApplication

        tilDate = view.findViewById(R.id.date_til)
        tilStart = view.findViewById(R.id.til_start)
        tilEnd = view.findViewById(R.id.til_end)
        tilTitle = view.findViewById(R.id.edit_task_layout)
        tilLocation = view.findViewById(R.id.edit_task_location_layout)

        etTitle = view.findViewById(R.id.edit_schedule_title)
        etLocation = view.findViewById(R.id.edit_schedule_location)
        etDate = view.findViewById(R.id.edit_schedule_date)
        etStart = view.findViewById(R.id.et_start)
        etEnd = view.findViewById(R.id.et_end)
        val colorRadio = view.findViewById<RadioGroup>(R.id.edit_schedule_color)
        val toolBar = view.findViewById<MaterialToolbar>(R.id.edit_schedule_toolbar)
        val editBtn = view.findViewById<Button>(R.id.edit_schedule_btn)

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
            dismiss()
        }

        binding.editScheduleTitle.addTextChangedListener {
            tilTitle.error = null
        }

        binding.editScheduleLocation.addTextChangedListener {
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

        editBtn.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener
            Toast.makeText(context, "The Fields Are Valid", Toast.LENGTH_LONG).show()
            dismiss()
        }
    }


    private fun showDatePicker() {
        if (existingDatePicker == null) {
            datePicker.show(parentFragmentManager, datePickerTag)
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
            timePicker.show(parentFragmentManager, timePickerTag)
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

        if(binding.editScheduleTitle.isFieldEmptyOrNull()) {
            tilTitle.error = errorMsg
            isEmpty = true
        }

        if(binding.editScheduleDate.isFieldEmptyOrNull()) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}