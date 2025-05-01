package com.android.archives.ui.fragment.main

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.constants.ScheduleColorType
import com.android.archives.data.model.Schedule
import com.android.archives.databinding.FragmentEditScheduleBinding
import com.android.archives.ui.event.ScheduleEvent
import com.android.archives.ui.viewmodel.ScheduleViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.android.archives.utils.isFieldEmptyOrNull
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditScheduleFragment @Inject constructor(

) : DialogFragment() {
    private var _binding: FragmentEditScheduleBinding? = null
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

    private lateinit var schedule: Schedule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)

        arguments?.let { arg ->
            schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arg.getParcelable("schedule", Schedule::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                arg.getParcelable("schedule")!!
            }
        }
    }
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up)
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

        loadTask()

        tilDate = binding.dateTil
        tilStart = binding.tilStart
        tilEnd = binding.tilEnd
        tilTitle = binding.editTaskLayout
        tilLocation = binding.editTaskLocationLayout

        etTitle = binding.editScheduleTitle
        etLocation = binding.editScheduleLocation
        etDate = binding.editScheduleDate
        etStart = binding.etStart
        etEnd = binding.etEnd

        val colorRadio = binding.editScheduleColor
        val toolBar = binding.editScheduleToolbar
        val editBtn = binding.editScheduleBtn

        binding.editScheduleTitle.setText(schedule.title)
        binding.editScheduleLocation.setText(schedule.location)

        binding.editScheduleDate.setText(
            DateConverter.convertMillisToDateString(schedule.date)
        )
        binding.etStart.setText(
            DateConverter.convertTimeMillisToTimeString(
                schedule.startTimeHour,
                schedule.startTimeMin
            )
        )
        binding.etEnd.setText(
            DateConverter.convertTimeMillisToTimeString(
                schedule.endTimeHour,
                schedule.endTimeMin
            )
        )


        setCheckedRadio()

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Schedule Date")
            .build()

        toolBar.setNavigationOnClickListener {
            dismiss()
        }

        binding.editScheduleTitle.addTextChangedListener {
            scheduleViewModel.onEvent(ScheduleEvent.SetTitle(it.toString()))
            tilTitle.error = null
        }

        binding.editScheduleLocation.addTextChangedListener {
            scheduleViewModel.onEvent(ScheduleEvent.SetLocation(it.toString()))
            tilLocation.error = null
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

        binding.editScheduleToolbar.setOnMenuItemClickListener { menu ->
            when(menu.itemId) {
                R.id.delete_entity -> {
                    showDeleteScheduleDialog { shouldDelete ->
                        if(shouldDelete) {
                            scheduleViewModel.onEvent(ScheduleEvent.DeleteSchedule(schedule))
                            dismiss()
                        }
                    }
                    true
                }
                else -> false
            }
        }

        editBtn.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener
            if(inputsAreInvalid()) return@setOnClickListener

            scheduleViewModel.onEvent(ScheduleEvent.EditSchedule(schedule))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showDatePicker() {
        if (existingDatePicker == null) {
            datePicker.show(parentFragmentManager, datePickerTag)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            binding.editScheduleDate.setText(DateConverter.convertMillisToDateString(selection))
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

    private fun setCheckedRadio() {
        when(schedule.colorType) {
            ScheduleColorType.SCHEDULE_WHITE -> {
                binding.editScheduleColor.check(R.id.schedule_white)
            }
            ScheduleColorType.SCHEDULE_YELLOW -> {
                binding.editScheduleColor.check(R.id.schedule_yellow)
            }
            ScheduleColorType.SCHEDULE_ORANGE -> {
                binding.editScheduleColor.check(R.id.schedule_orange)
            }
            ScheduleColorType.SCHEDULE_RED -> {
                binding.editScheduleColor.check(R.id.schedule_red)
            }
            ScheduleColorType.SCHEDULE_PURPLE -> {
                binding.editScheduleColor.check(R.id.schedule_purple)
            }
            ScheduleColorType.SCHEDULE_BLUE -> {
                binding.editScheduleColor.check(R.id.schedule_blue)
            }
            ScheduleColorType.SCHEDULE_GREEN -> {
                binding.editScheduleColor.check(R.id.schedule_green)
            }
        }
    }

    private fun loadTask() {
        scheduleViewModel.onEvent(ScheduleEvent.SetTitle(schedule.title))
        scheduleViewModel.onEvent(ScheduleEvent.SetLocation(schedule.location ?: ""))
        scheduleViewModel.onEvent(ScheduleEvent.SetDate(schedule.date))
        scheduleViewModel.onEvent(ScheduleEvent.SetColorType(schedule.colorType))
        scheduleViewModel.onEvent(ScheduleEvent.SetTimeStartHour(schedule.startTimeHour))
        scheduleViewModel.onEvent(ScheduleEvent.SetTimeStartMin(schedule.startTimeMin))
        scheduleViewModel.onEvent(ScheduleEvent.SetTimeEndHour(schedule.endTimeHour))
        scheduleViewModel.onEvent(ScheduleEvent.SetTimeEndMin(schedule.endTimeMin))

        collectLatestOnViewLifecycle(scheduleViewModel.state) { state->
            startTimeHour = state.startHour
            startTimeMin = state.startMin
            endTimeHour = state.endHour
            endTimeMin = state.endMin
        }
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

    private fun showDeleteScheduleDialog(onDecision: (Boolean) -> Unit) {
        val tintedIcon = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_delete_history_24px
        )?.apply {
            setTint(ContextCompat.getColor(requireContext(), R.color.error))
        }

        MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle("Delete Schedule")
            .setMessage("Are you sure you want to delete this schedule?")
            .setIcon(tintedIcon)
            .setNeutralButton("Cancel") { _, _ ->
                onDecision(false)
            }
            .setNegativeButton("Delete") { _, _ ->
                onDecision(true)
            }
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(context, R.color.error)
                )
            }
    }

}