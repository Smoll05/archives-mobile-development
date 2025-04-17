package com.android.archives.ui.fragment.main

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.archives.data.model.Task
import com.android.archives.databinding.FragmentEditTaskBinding
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.android.archives.utils.isFieldEmptyOrNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : DialogFragment() {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by activityViewModels()
    lateinit var task : Task

//    lateinit var etTaskTitle : EditText
//    lateinit var etTaskDescription : EditText
//    lateinit var btnEmoji : Button
//    lateinit var tilTitle : TextInputLayout
//    lateinit var tilDesc : TextInputLayout
//    lateinit var errorEmoji : TextView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)

        arguments?.let { arg ->
            arg.getParcelable("task", Task::class.java)?.let { argTask ->
                task = argTask
            }
        }
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

//        val btnAdd = view.findViewById<Button>(R.id.add_task_button)
//        etTaskTitle = view.findViewById(R.id.add_task_title)
//        etTaskDescription = view.findViewById(R.id.add_task_desc)
//        btnEmoji = view.findViewById(R.id.add_task_emoji_button)
//
//        tilTitle = view.findViewById(R.id.add_task_layout)
//        tilDesc = view.findViewById(R.id.add_task_desc_layout)
//
//        errorEmoji = view.findViewById(R.id.error_emoji_btn)
//        errorEmoji.visibility = TextView.INVISIBLE

        binding.editTaskTitle.setText(task.title)
        binding.editTaskDesc.setText(task.description)
        binding.editTaskEmojiButton.text = task.emojiIcon
        binding.editTaskEmojiButton.textSize = 40f

        binding.errorEmojiTv.visibility = TextView.INVISIBLE

        binding.editTaskDialogToolbar.setNavigationOnClickListener {
            dismiss()
        }

        binding.editTaskEmojiButton.setOnClickListener {
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
            Toast.makeText(context, "Task Edited", Toast.LENGTH_SHORT).show()

            taskViewModel.onEvent(TaskEvent.EditTask(task))

            collectLatestOnViewLifecycle(taskViewModel.state) { state ->
                if(state.isLoading) {
                    return@collectLatestOnViewLifecycle
                } else {
                    dismiss()
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}