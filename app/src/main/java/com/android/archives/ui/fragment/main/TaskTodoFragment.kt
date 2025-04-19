package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.databinding.FragmentTaskDetailViewBinding
import com.android.archives.databinding.FragmentTaskTodoBinding
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.SpacingDecorator
import com.android.archives.utils.collectLatestOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskTodoFragment : Fragment() {
    private var _binding: FragmentTaskTodoBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: TaskRecyclerAdapter
    private val taskViewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskTodoBinding.inflate(inflater, container, false)
        val view = binding.root

        val taskEmptySign = binding.taskTodoEmpty
        val rvTask = binding.taskTodoRecyclerView

        rvTask.addItemDecoration(
            SpacingDecorator(0, 0, 0, 24)
        )

        adapter = TaskRecyclerAdapter(
            onClick = { task ->
                taskViewModel.onEvent(TaskEvent.LoadTask(task))
                TaskDetailViewFragment().show(parentFragmentManager, "FullScreenDialog")
            },
            onCheckChanged = { task, isChecked ->
                task.isComplete = isChecked
                taskViewModel.onEvent(TaskEvent.SetCompletion(task, isChecked))
            }
        )

        rvTask.adapter = adapter
        rvTask.layoutManager = LinearLayoutManager(requireContext())

        collectLatestOnViewLifecycle(taskViewModel.state) { state ->
            if (state.isLoading) {
                rvTask.isEnabled = false
                return@collectLatestOnViewLifecycle
            } else {
                rvTask.isEnabled = true
            }

            val todoList = state.todoTask
            adapter.differ.submitList(todoList)

            if (todoList.isEmpty()) {
                taskEmptySign.visibility = View.VISIBLE
                rvTask.visibility = View.INVISIBLE
            } else {
                taskEmptySign.visibility = View.INVISIBLE
                rvTask.visibility = View.VISIBLE
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
