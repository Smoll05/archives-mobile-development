package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

    private var _binding: FragmentAddCourseDialogBinding? = null
    private val binding get() = _binding!!

    private var selectedColorRes: Int = R.drawable.ic_folder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the binding
        _binding = FragmentAddCourseDialogBinding.inflate(inflater, container, false)

        val colorOptions = listOf(
            binding.colorOption1,
            binding.colorOption2,
            binding.colorOption3,
            binding.colorOption4,
            binding.colorOption5,
            binding.colorOption6
        )
        val colorDrawables = listOf(
            R.drawable.ic_folder_yellow,
            R.drawable.ic_folder_orange,
            R.drawable.ic_folder_red,
            R.drawable.ic_folder_violet,
            R.drawable.ic_folder_blue,
            R.drawable.ic_folder_green
        )

        // Set up the color option click listeners
        colorOptions.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                selectedColorRes = colorDrawables[index]
                highlightSelected(imageView, colorOptions)
            }
        }

        // Add course button click listener
        binding.btnAddCourse.setOnClickListener {
            val courseName = binding.etCourseName.text.toString().trim()
            if (courseName.isNotEmpty()) {
                val folder = FolderItem(
                    name = courseName,
                    iconRes = selectedColorRes
                )
                onFolderAdded(folder)
                dismiss()
            } else {
                Toast.makeText(context, "Please enter a course name", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel button click listener
        binding.btnCancelCourse.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.dialog_animation_enter_up)
    }


    private fun highlightSelected(selected: ImageView, allOptions: List<ImageView>) {
        allOptions.forEach { imageView ->
            imageView.setBackgroundResource(
                if (imageView == selected) R.drawable.bg_selected_border else 0
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
