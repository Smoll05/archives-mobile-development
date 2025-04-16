package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.databinding.FragmentTaskDetailViewBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailViewFragment : DialogFragment() {
    private var _binding: FragmentTaskDetailViewBinding? = null
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
    ) = FragmentTaskDetailViewBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val app = application as ArchivesApplication

//        intent?.let { it ->
//            it.getIntExtra("task_id", 0).let {id ->
//                task = app.taskList.find { it.taskId.toInt() == id }!!
//            }
//        }

        val toolBar = view.findViewById<MaterialToolbar>(R.id.task_detail_toolbar)
        val tvTitle = view.findViewById<TextView>(R.id.task_detail_title)
        val tvDescription = view.findViewById<TextView>(R.id.task_detail_description)

        val btnEdit = view.findViewById<Button>(R.id.task_detail_edit)
        val btnDelete = view.findViewById<Button>(R.id.task_detail_delete)
        val tvMark = view.findViewById<TextView>(R.id.task_detail_mark)

        toolBar.setNavigationOnClickListener {
            dismiss()
        }

//        tvTitle.text = task.title
//        tvDescription.text = task.title

        btnEdit.setOnClickListener {
            Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
            EditTaskFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Delete") { _, _ ->
                }
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }
        }

        val spannableString = SpannableString("Mark As Complete")
        tvMark.text = spannableString

        tvMark.setOnClickListener {
            val spans = spannableString.getSpans(0, spannableString.length, StrikethroughSpan::class.java)

            if (spans.isEmpty()) {
                spannableString.setSpan(StrikethroughSpan(), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                spannableString.removeSpan(spans[0])
            }

            tvMark.text = spannableString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}