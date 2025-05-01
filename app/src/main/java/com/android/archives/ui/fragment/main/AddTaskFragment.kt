package com.android.archives.ui.fragment.main

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
import com.android.archives.databinding.FragmentAddTaskBinding
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.isFieldEmptyOrNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskFragment : DialogFragment() {
    private var check = false
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by activityViewModels()

    private var emojiPickerShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAddTaskBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use View Binding to initialize views
        val btnAdd = binding.addTaskButton
        val etTaskTitle = binding.addTaskTitle
        val etTaskDescription = binding.addTaskDesc
        val btnEmoji = binding.addTaskEmojiButton
        val tilTitle = binding.addTaskLayout
        val tilDesc = binding.addTaskDescLayout
        val errorEmoji = binding.errorEmojiBtn
        val toolBar = binding.addTaskDialogToolbar

        // Initialize views
        errorEmoji.visibility = TextView.INVISIBLE

        toolBar.setNavigationOnClickListener {
            dismiss()
        }

        btnEmoji.setOnClickListener {
            if(emojiPickerShown) return@setOnClickListener

            emojiPickerShown = true
            EmojiPickerDialogueFragment().show(parentFragmentManager, "emoji_picker_dialog")
        }

        parentFragmentManager.setFragmentResultListener(
            EmojiPickerDialogueFragment.EMOJI_PICKER_RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedEmoji = bundle.getString(EmojiPickerDialogueFragment.EMOJI_PICKED_BUNDLE_KEY)
            selectedEmoji?.let { emoji ->
                btnEmoji.text = emoji
                btnEmoji.textSize = 40f
                errorEmoji.visibility = TextView.INVISIBLE
                taskViewModel.onEvent(TaskEvent.SetEmoji(emoji))
            }

            emojiPickerShown = false
        }


        btnAdd.setOnClickListener {
            if (check) return@setOnClickListener  // Prevent multiple clicks
            if (areFieldsEmpty()) return@setOnClickListener

            check = true // Set the flag so the button can't be clicked again
            btnAdd.isEnabled = false // Also disable the button

            Toast.makeText(context, "Task Successfully Added", Toast.LENGTH_SHORT).show()
            taskViewModel.onEvent(TaskEvent.SaveTask)

            dismiss() // Dismiss dialog
        }

        // Add text listeners
        etTaskTitle.addTextChangedListener {
            taskViewModel.onEvent(TaskEvent.SetTitle(it.toString()))
            tilTitle.error = null
        }

        etTaskDescription.addTextChangedListener {
            taskViewModel.onEvent(TaskEvent.SetDescription(it.toString()))
            tilDesc.error = null
        }
    }

    private fun areFieldsEmpty(): Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        // Check fields for emptiness using View Binding
        if (binding.addTaskTitle.isFieldEmptyOrNull()) {
            binding.addTaskLayout.error = errorMsg
            isEmpty = true
        }

        if (binding.addTaskDesc.isFieldEmptyOrNull()) {
            binding.addTaskDescLayout.error = errorMsg
            isEmpty = true
        }

        if (binding.addTaskEmojiButton.text.isNullOrEmpty()) {
            binding.errorEmojiBtn.visibility = TextView.VISIBLE
            isEmpty = true
        }

        return isEmpty
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "AddTaskFragment"

        fun showIfNotOpen(fragmentManager: androidx.fragment.app.FragmentManager) {
            val existing = fragmentManager.findFragmentByTag(TAG)
            if (existing == null) {
                AddTaskFragment().show(fragmentManager, TAG)
            }
        }
    }

}
