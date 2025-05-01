package com.android.archives.ui.fragment.main

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.data.model.Task
import com.android.archives.databinding.FragmentEditTaskBinding
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.isFieldEmptyOrNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : DialogFragment() {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by activityViewModels()
    lateinit var task : Task

    private var emojiPickerShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)

        arguments?.let { arg ->
            task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arg.getParcelable("task", Task::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                arg.getParcelable("task")!!
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
    ) = FragmentEditTaskBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTask()

        binding.editTaskTitle.setText(task.title)
        binding.editTaskDesc.setText(task.description)
        binding.editTaskEmojiButton.text = task.emojiIcon
        binding.editTaskEmojiButton.textSize = 40f

        binding.errorEmojiTv.visibility = TextView.INVISIBLE

        binding.editTaskDialogToolbar.setNavigationOnClickListener {
            dismiss()
        }

        binding.editTaskEmojiButton.setOnClickListener {
            if(emojiPickerShown) return@setOnClickListener

            emojiPickerShown = true
            val emojiPickerDialogFragment = EmojiPickerDialogueFragment()
            emojiPickerDialogFragment.show(parentFragmentManager, "emoji_picker_dialog")
        }

        parentFragmentManager.setFragmentResultListener(
            EmojiPickerDialogueFragment.EMOJI_PICKER_RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedEmoji = bundle.getString(EmojiPickerDialogueFragment.EMOJI_PICKED_BUNDLE_KEY)
            selectedEmoji?.let { emoji ->
                binding.editTaskEmojiButton.text = emoji
                binding.errorEmojiTv.visibility = TextView.INVISIBLE
                taskViewModel.onEvent(TaskEvent.SetEmoji(emoji))
            }
            emojiPickerShown = false
        }

        binding.editTaskTitle.addTextChangedListener {
            binding.editTaskLayout.error = null
            taskViewModel.onEvent(TaskEvent.SetTitle(it.toString()))
        }

        binding.editTaskDesc.addTextChangedListener {
            binding.editTaskDescLayout.error = null
            taskViewModel.onEvent(TaskEvent.SetDescription(it.toString()))
        }

        binding.editTaskButton.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener

            taskViewModel.onEvent(TaskEvent.EditTask(task))

            Toast.makeText(context, "Task Edited", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun areFieldsEmpty() : Boolean {
        var isEmpty = false

        val errorMsg = "This Field Is Required"

        if(binding.editTaskTitle.isFieldEmptyOrNull()) {
            binding.editTaskLayout.error = errorMsg
            isEmpty = true
        }

        if(binding.editTaskDesc.isFieldEmptyOrNull()) {
            binding.editTaskDescLayout.error = errorMsg
            isEmpty = true
        }

        if(binding.editTaskEmojiButton.text.isNullOrEmpty()) {
            binding.errorEmojiTv.visibility = TextView.VISIBLE
            isEmpty = true
        }

        return isEmpty
    }

    private fun loadTask() {
        taskViewModel.onEvent(TaskEvent.SetTitle(task.title))
        taskViewModel.onEvent(TaskEvent.SetDescription(task.description))
        taskViewModel.onEvent(TaskEvent.SetEmoji(task.emojiIcon))
        taskViewModel.onEvent(TaskEvent.SetCompletion(task.isComplete))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}