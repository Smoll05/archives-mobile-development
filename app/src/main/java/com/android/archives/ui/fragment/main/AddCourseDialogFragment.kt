package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.archives.R
import com.android.archives.data.model.FolderItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCourseDialogFragment(
    private val onFolderAdded: (FolderItem) -> Unit
) : BottomSheetDialogFragment() {

    private var selectedColorRes: Int = R.drawable.ic_folder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_course_dialog, container, false)

        val courseNameInput = view.findViewById<EditText>(R.id.etCourseName)
        val btnAdd = view.findViewById<Button>(R.id.btnAddCourse)
        val btnCancel = view.findViewById<Button>(R.id.btnCancelCourse)

        val colorOption1 = view.findViewById<ImageView>(R.id.colorOption1)
        val colorOption2 = view.findViewById<ImageView>(R.id.colorOption2)
        val colorOption3 = view.findViewById<ImageView>(R.id.colorOption3)

        colorOption1.setOnClickListener {
            selectedColorRes = R.drawable.ic_folder_red
            highlightSelected(colorOption1, listOf(colorOption2, colorOption3))
        }

        colorOption2.setOnClickListener {
            selectedColorRes = R.drawable.ic_folder_blue
            highlightSelected(colorOption2, listOf(colorOption1, colorOption3))
        }

        colorOption3.setOnClickListener {
            selectedColorRes = R.drawable.ic_folder_green
            highlightSelected(colorOption3, listOf(colorOption1, colorOption2))
        }

        btnAdd.setOnClickListener {
            val courseName = courseNameInput.text.toString().trim()
            if (courseName.isNotEmpty()) {
                val folder = FolderItem(
                    title = courseName,
                    name = courseName,
//                    iconRes = selectedColorRes
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

        return view
    }

    private fun highlightSelected(selected: ImageView, others: List<ImageView>) {
        selected.setBackgroundResource(R.drawable.bg_selected_border)
        others.forEach { it.setBackgroundResource(0) }
    }
}
