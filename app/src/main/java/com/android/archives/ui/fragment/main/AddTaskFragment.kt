package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.archives.application.ArchivesApplication
import com.android.archives.data.model.Task
import com.android.archives.databinding.FragmentAddTaskBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.getContent
import com.android.archives.utils.isFieldEmptyOrNull
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskFragment : DialogFragment() {
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAddTaskBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as ArchivesApplication

        binding.addTaskDialogToolbar.setNavigationOnClickListener {
            dismiss()
        }

        binding.addTaskEmojiButton.setOnClickListener {
            EmojiPickerDialogueFragment().show(parentFragmentManager, "emoji_picker_dialog")
        }

        parentFragmentManager.setFragmentResultListener(
            EmojiPickerDialogueFragment.EMOJI_PICKER_RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedEmoji = bundle.getString(EmojiPickerDialogueFragment.EMOJI_PICKED_BUNDLE_KEY)
            selectedEmoji?.let { emoji ->
                binding.addTaskEmojiButton.text = emoji
                binding.addTaskEmojiButton.textSize = 40f
                binding.errorEmojiBtn.visibility = View.INVISIBLE
            }
        }

        binding.addTaskButton.setOnClickListener {
            if (areFieldsEmpty()) return@setOnClickListener
            Toast.makeText(context, "Fields Input Correct", Toast.LENGTH_SHORT).show()

            app.taskList.add(
                Task(
                    99,
                    binding.addTaskTitle.getContent(),
                    binding.addTaskDesc.getContent(),
                    binding.addTaskEmojiButton.getContent(),
                    false
                )
            )

            dismiss()
        }

        binding.addTaskTitle.addTextChangedListener {
            binding.addTaskLayout.error = null
        }

        binding.addTaskDesc.addTextChangedListener {
            binding.addTaskDescLayout.error = null
        }

        binding.errorEmojiBtn.visibility = View.INVISIBLE
    }

    private fun areFieldsEmpty(): Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        if (binding.addTaskTitle.isFieldEmptyOrNull()) {
            binding.addTaskLayout.error = errorMsg
            isEmpty = true
        }

        if (binding.addTaskDesc.isFieldEmptyOrNull()) {
            binding.addTaskDescLayout.error = errorMsg
            isEmpty = true
        }

        if (binding.addTaskEmojiButton.text.isNullOrEmpty()) {
            binding.errorEmojiBtn.visibility = View.VISIBLE
            isEmpty = true
        }

        return isEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
