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

        binding.taskTodoRecyclerView.addItemDecoration (
            SpacingDecorator(0, 0, 0, 24)
        )

        adapter = TaskRecyclerAdapter(
            onClick = { task ->
                taskViewModel.onEvent(TaskEvent.LoadTask(task))
                TaskDetailViewFragment().show(parentFragmentManager, "TaskDetailDialog")
            },

            onCheckChanged = { task, isChecked ->
                task.isComplete = isChecked
                taskViewModel.onEvent(TaskEvent.SetTaskCompletion(task, isChecked))
            }
        )

        loadTask()

        binding.taskTodoRecyclerView.adapter = adapter
        binding.taskTodoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadTask() {
        collectLatestOnViewLifecycle(taskViewModel.state) { state ->
            val todoList = state.todoTask
            Log.d("TaskAdd", "todolist state $todoList")
            Log.d("TaskAdd", "task add before ${adapter.differ.currentList}")
            adapter.differ.submitList(todoList)
            Log.d("TaskAdd", "task add after ${adapter.differ.currentList}")

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