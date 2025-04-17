package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.archives.application.ArchivesApplication
import com.android.archives.data.model.Task
import com.android.archives.databinding.FragmentEditTaskBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.getContent
import com.android.archives.utils.isFieldEmptyOrNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : DialogFragment() {
    private var _binding: FragmentEditTaskBinding? = null
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
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as ArchivesApplication

        binding.apply {
            errorEmojiBtn.visibility = View.INVISIBLE

            editTaskDialogToolbar.setNavigationOnClickListener {
                dismiss()
            }

            editTaskEmojiButton.setOnClickListener {
                val emojiPickerDialogFragment = EmojiPickerDialogueFragment()
                emojiPickerDialogFragment.show(parentFragmentManager, "emoji_picker_dialog")
            }

            editTaskButton.setOnClickListener {
                if (areFieldsEmpty()) return@setOnClickListener
                Toast.makeText(context, "Fields Input Correct", Toast.LENGTH_SHORT).show()

                app.taskList.add(
                    Task(
                        99,
                        editTaskTitle.getContent(),
                        editTaskDesc.getContent(),
                        editTaskEmojiButton.getContent(),
                        false
                    )
                )

                findNavController().popBackStack()
            }

            editTaskTitle.addTextChangedListener {
                editTaskLayout.error = null
            }

            editTaskDesc.addTextChangedListener {
                editTaskDescLayout.error = null
            }
        }
    }

    private fun areFieldsEmpty(): Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        binding.apply {
            if (editTaskTitle.isFieldEmptyOrNull()) {
                editTaskLayout.error = errorMsg
                isEmpty = true
            }

            if (editTaskDesc.isFieldEmptyOrNull()) {
                editTaskDescLayout.error = errorMsg
                isEmpty = true
            }

            if (editTaskEmojiButton.text.isNullOrEmpty()) {
                errorEmojiBtn.visibility = View.VISIBLE
                isEmpty = true
            }
        }

        return isEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
