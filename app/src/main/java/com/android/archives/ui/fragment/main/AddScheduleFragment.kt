package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.constants.ScheduleColorType
import com.android.archives.databinding.FragmentAddScheduleBinding
import com.android.archives.ui.event.ScheduleEvent
import com.android.archives.ui.viewmodel.ScheduleViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.isFieldEmptyOrNull
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddScheduleFragment : DialogFragment() {
    private var _binding: FragmentAddScheduleBinding? = null
    private val binding get() = _binding!!

    private val scheduleViewModel: ScheduleViewModel by activityViewModels()

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
    private var existingDatePicker: Fragment? = null
    private val datePickerTag = "DATE PICKER"

    private var isStartPickerShowing = false
    private var isEndPickerShowing = false

    private var startTimeHour : Int = 0
    private var startTimeMin : Int = 0
    private var endTimeHour : Int = 0
    private var endTimeMin : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAddScheduleBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tilDate = view.findViewById(R.id.date_til)
        tilStart = view.findViewById(R.id.til_start)
        tilEnd = view.findViewById(R.id.til_end)
        tilTitle = view.findViewById(R.id.add_task_layout)
        tilLocation = view.findViewById(R.id.add_task_location_layout)

        etTitle = view.findViewById(R.id.add_schedule_title)
        etLocation = view.findViewById(R.id.add_schedule_location)
        etDate = view.findViewById(R.id.add_schedule_date)
        etStart = view.findViewById(R.id.et_start)
        etEnd = view.findViewById(R.id.et_end)


        val colorRadio = view.findViewById<RadioGroup>(R.id.add_schedule_color)
        val toolBar = view.findViewById<MaterialToolbar>(R.id.add_schedule_toolbar)
        val addBtn = view.findViewById<Button>(R.id.add_schedule_btn)

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Schedule Date")
            .build()

        toolBar.setNavigationOnClickListener {
            dismiss()
        }

        binding.addScheduleTitle.addTextChangedListener {
            scheduleViewModel.onEvent(ScheduleEvent.SetTitle(it.toString()))
            tilTitle.error = null
        }

        binding.addScheduleLocation.addTextChangedListener {
            scheduleViewModel.onEvent(ScheduleEvent.SetLocation(it.toString()))
            tilLocation.error = null
        }

        etDate.addTextChangedListener {
            tilDate.error = null
        }

        etStart.addTextChangedListener {
            tilStart.error = null
        }

        etEnd.addTextChangedListener {
            tilEnd.error = null
        }

        etDate.setOnClickListener {
            showDatePicker()
        }

        etStart.setOnClickListener {
            showStartTimePicker()
        }

        etEnd.setOnClickListener {
            showEndTimePicker()
        }

        colorRadio.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.schedule_white -> {
                    scheduleViewModel.onEvent(
                        ScheduleEvent.SetColorType(ScheduleColorType.SCHEDULE_WHITE)
                    )
                }
                R.id.schedule_yellow -> {
                    scheduleViewModel.onEvent(
                        ScheduleEvent.SetColorType(ScheduleColorType.SCHEDULE_YELLOW)
                    )
                }
                R.id.schedule_orange -> {
                    scheduleViewModel.onEvent(
                        ScheduleEvent.SetColorType(ScheduleColorType.SCHEDULE_ORANGE)
                    )
                }
                R.id.schedule_red -> {
                    scheduleViewModel.onEvent(
                        ScheduleEvent.SetColorType(ScheduleColorType.SCHEDULE_RED)
                    )
                }
                R.id.schedule_purple -> {
                    scheduleViewModel.onEvent(
                        ScheduleEvent.SetColorType(ScheduleColorType.SCHEDULE_PURPLE)
                    )
                }
                R.id.schedule_blue -> {
                    scheduleViewModel.onEvent(
                        ScheduleEvent.SetColorType(ScheduleColorType.SCHEDULE_BLUE)
                    )
                }
                R.id.schedule_green -> {
                    scheduleViewModel.onEvent(
                        ScheduleEvent.SetColorType(ScheduleColorType.SCHEDULE_GREEN)
                    )
                }
            }
        }

        addBtn.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener
            if(inputsAreInvalid()) return@setOnClickListener

            scheduleViewModel.onEvent(ScheduleEvent.SaveSchedule)
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up)
    }


    private fun showDatePicker() {
        if (existingDatePicker == null) {
            datePicker.show(parentFragmentManager, datePickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            etDate.setText(DateConverter.convertMillisToDateString(selection))
            scheduleViewModel.onEvent(ScheduleEvent.SetDate(selection))
        }

        datePicker.addOnDismissListener {
            existingDatePicker = null
        }

        existingDatePicker = datePicker
    }

    private fun showStartTimePicker() {
        if(isStartPickerShowing) return

        val startTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(startTimeHour)
            .setMinute(startTimeMin)
            .setTitleText("Select Start Time")
            .build()

        startTimePicker.show(parentFragmentManager, "startTimePicker")
        isStartPickerShowing = true

        startTimePicker.addOnPositiveButtonClickListener {
            binding.etStart.setText(
                DateConverter.convertTimeMillisToTimeString(
                    startTimePicker.hour, startTimePicker.minute
                )
            )

            startTimeHour = startTimePicker.hour
            startTimeMin = startTimePicker.minute
            scheduleViewModel.onEvent(ScheduleEvent.SetTimeStartHour(startTimeHour))
            scheduleViewModel.onEvent(ScheduleEvent.SetTimeStartMin(startTimeMin))
        }

        startTimePicker.addOnDismissListener {
            isStartPickerShowing = false
        }
    }

    private fun showEndTimePicker() {
        if(isEndPickerShowing) return

        val endTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(endTimeHour)
            .setMinute(endTimeMin)
            .setTitleText("Select End Time")
            .build()

        endTimePicker.show(parentFragmentManager, "endTimePicker")
        isEndPickerShowing = true

        endTimePicker.addOnPositiveButtonClickListener {
            binding.etEnd.setText(
                DateConverter.convertTimeMillisToTimeString(
                    endTimePicker.hour, endTimePicker.minute
                )
            )

            endTimeHour = endTimePicker.hour
            endTimeMin = endTimePicker.minute

            scheduleViewModel.onEvent(ScheduleEvent.SetTimeEndHour(endTimeHour))
            scheduleViewModel.onEvent(ScheduleEvent.SetTimeEndMin(endTimeMin))
        }

        endTimePicker.addOnDismissListener {
            isEndPickerShowing = false
        }
    }


    private fun areFieldsEmpty() : Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        if(binding.addScheduleTitle.isFieldEmptyOrNull()) {
            tilTitle.error = errorMsg
            isEmpty = true
        }

        if(binding.addScheduleDate.isFieldEmptyOrNull()) {
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
            tilEnd.error = errorMsg
            isEmpty = true
        }

        return isEmpty
    }

    private fun inputsAreInvalid() : Boolean {
        var isInvalid = false

        if(startTimeHour > endTimeHour) {
            showInvalidTimeDialog()
            isInvalid = true
        } else if(startTimeHour == endTimeHour) {
            if(startTimeMin >= endTimeMin) {
                showInvalidTimeDialog()
                isInvalid = true
            }
        }

        return isInvalid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showInvalidTimeDialog() {
        MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle("Invalid Time")
            .setMessage("Start time selected must be earlier than the end time")
            .setIcon(R.drawable.ic_delete_history_24px)
            .setNegativeButton("OK") { _, _ -> }
            .show()
    }
}