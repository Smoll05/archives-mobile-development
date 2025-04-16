package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.archives.R
import com.android.archives.application.ArchivesApplication
import com.android.archives.data.model.Task
import com.android.archives.databinding.FragmentEditTaskBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.getContent
import com.android.archives.utils.isFieldEmptyOrNull
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskFragment : DialogFragment() {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    lateinit var etTaskTitle : EditText
    lateinit var etTaskDescription : EditText
    lateinit var btnEmoji : Button
    lateinit var tilTitle : TextInputLayout
    lateinit var tilDesc : TextInputLayout
    lateinit var errorEmoji : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
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


        val btnAdd = view.findViewById<Button>(R.id.add_task_button)
        etTaskTitle = view.findViewById(R.id.add_task_title)
        etTaskDescription = view.findViewById(R.id.add_task_desc)
        btnEmoji = view.findViewById(R.id.add_task_emoji_button)

        tilTitle = view.findViewById(R.id.add_task_layout)
        tilDesc = view.findViewById(R.id.add_task_desc_layout)

        errorEmoji = view.findViewById(R.id.error_emoji_btn)
        errorEmoji.visibility = TextView.INVISIBLE

        val app = requireActivity().application as ArchivesApplication

        val toolBar = view.findViewById<MaterialToolbar>(R.id.add_task_dialog_toolbar)

        toolBar.setNavigationOnClickListener {
            dismiss()
        }

        btnEmoji.setOnClickListener {
            val emojiPickerDialogFragment = EmojiPickerDialogueFragment()
            emojiPickerDialogFragment.show(parentFragmentManager, "emoji_picker_dialog")
        }

        btnAdd.setOnClickListener {
            if(areFieldsEmpty()) return@setOnClickListener
            Toast.makeText(context, "Fields Input Correct", Toast.LENGTH_SHORT).show()

            app.taskList.add(
                Task(
                    99,
                    etTaskTitle.getContent(),
                    etTaskDescription.getContent(),
                    btnEmoji.getContent(),
                    false
                )
            )

            findNavController().popBackStack()
        }

        etTaskTitle.addTextChangedListener {
            tilTitle.error = null
        }

        etTaskDescription.addTextChangedListener {
            tilDesc.error = null
        }
    }

    private fun areFieldsEmpty() : Boolean {
        var isEmpty = false

        val errorMsg = "This Field Is Required"

        if(etTaskTitle.isFieldEmptyOrNull()) {
            tilTitle.error = errorMsg
            isEmpty = true
        }

        if(etTaskDescription.isFieldEmptyOrNull()) {
            tilDesc.error = errorMsg
            isEmpty = true
        }

        if(btnEmoji.text.isNullOrEmpty()) {
            errorEmoji.visibility = TextView.VISIBLE
            isEmpty = true
        }

        return isEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}