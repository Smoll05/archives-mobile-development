package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.databinding.FragmentTaskTodoBinding
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.SpacingDecorator
import com.android.archives.utils.collectLatestOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskTodoFragment : Fragment() {
    private lateinit var adapter: TaskRecyclerAdapter
    private val taskViewModel: TaskViewModel by activityViewModels()

    private var _binding: FragmentTaskTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTaskTodoBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskEmptySign = view.findViewById<LinearLayout>(R.id.task_todo_empty)
        val rvTask = view.findViewById<RecyclerView>(R.id.task_todo_recycler_view)

        rvTask.addItemDecoration (
            SpacingDecorator(0, 0, 0, 24)
        )

        adapter = TaskRecyclerAdapter(
            onClick = { task ->
                taskViewModel.onEvent(TaskEvent.LoadTask(task))
                TaskDetailViewFragment().show(parentFragmentManager, "FullScreenDialog")
            },

            onCheckChanged = { task, isChecked ->
                task.isComplete = isChecked
                taskViewModel.onEvent(TaskEvent.SetTaskCompletion(task, isChecked))
            }
        )

        rvTask.adapter = adapter
        rvTask.layoutManager = LinearLayoutManager(requireContext())

        loadTask()
    }

    override fun onResume() {
        super.onResume()
        loadTask()
    }

    private fun loadTask() {
        collectLatestOnViewLifecycle(taskViewModel.state) { state ->
            if (state.isLoading) {
                binding.taskTodoRecyclerView.isEnabled = false
                return@collectLatestOnViewLifecycle
            } else {
                binding.taskTodoRecyclerView.isEnabled = true
            }

            Log.d("Task", "I am updated ${state.title}")

            val todoList = state.todoTask

            adapter.differ.submitList(todoList)

            if(todoList.isEmpty()) {
                binding.taskTodoEmpty.visibility = LinearLayout.VISIBLE
                binding.taskTodoRecyclerView.visibility = RecyclerView.INVISIBLE
            } else {
                binding.taskTodoEmpty.visibility = LinearLayout.INVISIBLE
                binding.taskTodoRecyclerView.visibility = RecyclerView.VISIBLE
            }
        }
    }
}