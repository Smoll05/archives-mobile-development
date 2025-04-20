package com.android.archives.ui.fragment.main

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.data.model.Task
import com.android.archives.databinding.FragmentTaskDetailViewBinding
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailViewFragment : DialogFragment() {
    private var _binding: FragmentTaskDetailViewBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var task: Task
    private val spannableString = SpannableString("Mark As Complete")

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTask()
        val toolBar = binding.taskDetailToolbar
        val tvTitle = binding.taskDetailTitle
        val tvDescription = binding.taskDetailDescription

        val btnEdit = binding.taskDetailEdit
        val btnDelete = binding.taskDetailDelete

        binding.taskDetailMark.text = spannableString

        toolBar.setNavigationOnClickListener {
            dismiss()
        }

        btnEdit.setOnClickListener {
            val editTaskFragment = EditTaskFragment()

            editTaskFragment.arguments = Bundle().apply {
                putParcelable("task", task)
            }

            editTaskFragment.show(parentFragmentManager, "FullScreenDialog")
        }

        btnDelete.setOnClickListener {
            val tintedIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_contract_delete_24px
            )?.apply {
                setTint(ContextCompat.getColor(requireContext(), R.color.error)) // Use your desired color
            }

            MaterialAlertDialogBuilder(
                requireContext(),
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setIcon(tintedIcon)
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Delete") { _, _ ->
                    taskViewModel.onEvent(TaskEvent.DeleteTask(task))
                    dismiss()
                }
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }
        }

        binding.taskDetailMark.setOnClickListener {
            if (task.isComplete) {
                taskViewModel.onEvent(TaskEvent.SetCompletion(task, false))
                unMarkTaskComplete()
            } else {
                taskViewModel.onEvent(TaskEvent.SetCompletion(task, true))
                markTaskComplete()
            }

            binding.taskDetailMark.text = spannableString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun markTaskComplete() {
        spannableString.setSpan(StrikethroughSpan(), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.taskDetailMark.text = spannableString
    }

    private fun unMarkTaskComplete() {
        val spans = spannableString.getSpans(0, spannableString.length, StrikethroughSpan::class.java)

        if(spans.isNotEmpty()) {
            spannableString.removeSpan(spans[0])
            binding.taskDetailMark.text = spannableString
        }
    }

    private fun loadTask() {
        collectLatestOnViewLifecycle(taskViewModel.state) { state ->
            state.currentTask?.let { task ->
                this.task = task

                val titleText = "${task.emojiIcon} ${task.title}"
                binding.taskDetailTitle.text = titleText
                binding.taskDetailDescription.text = task.description

                if(task.isComplete) {
                    markTaskComplete()
                } else {
                    unMarkTaskComplete()
                }
            }
        }
    }
}