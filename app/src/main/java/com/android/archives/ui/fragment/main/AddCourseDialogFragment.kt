package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.archives.R
import com.android.archives.data.model.FolderItem
import com.android.archives.databinding.FragmentAddCourseDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCourseDialogFragment(
    private val onFolderAdded: (FolderItem) -> Unit
) : BottomSheetDialogFragment() {

    private var selectedColorRes: Int = R.drawable.ic_folder
    private var _binding: FragmentAddCourseDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCourseDialogBinding.inflate(inflater, container, false)

        val courseNameInput = binding.etCourseName
        val btnAdd = binding.btnAddCourse
        val btnCancel = binding.btnCancelCourse

        val colorOptions = listOf(
            binding.colorOption1, binding.colorOption2, binding.colorOption3,
            binding.colorOption4, binding.colorOption5, binding.colorOption6
        )
        val colorDrawables = listOf(
            R.drawable.ic_folder_yellow,
            R.drawable.ic_folder_orange,
            R.drawable.ic_folder_red,
            R.drawable.ic_folder_violet,
            R.drawable.ic_folder_blue,
            R.drawable.ic_folder_green
        )

        colorOptions.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                selectedColorRes = colorDrawables[index]
                highlightSelected(imageView, colorOptions)
            }
        }

        btnAdd.setOnClickListener {
            val courseName = courseNameInput.text.toString().trim()
            if (courseName.isNotEmpty()) {
                val folder = FolderItem(
                    title = courseName,
                    name = courseName,
                    iconRes = selectedColorRes
                )
                onFolderAdded(folder)
                dismiss()
            } else {
                Toast.makeText(context, "Please enter a course name", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up);
    }

    private fun highlightSelected(selected: View, allOptions: List<View>) {
        allOptions.forEach { view ->
            view.setBackgroundResource(
                if (view == selected) R.drawable.bg_selected_border else 0
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
