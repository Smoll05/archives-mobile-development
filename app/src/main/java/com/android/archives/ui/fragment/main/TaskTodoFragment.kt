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
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.SpacingDecorator
import com.android.archives.utils.collectLatestOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskTodoFragment : Fragment() {
    lateinit var adapter: TaskRecyclerAdapter
    private val taskViewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_todo, container, false)

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

//            if (state.isLoading) {
//                progressBar.visibility = View.VISIBLE
//                rvTask.visibility = View.INVISIBLE
//                taskEmptySign.visibility = View.INVISIBLE
//                return@collectLatestOnViewLifecycle
//            } else {
//                progressBar.visibility = View.GONE
//            }

            val todoList = state.todoTask

            adapter.differ.submitList(todoList)

            if(todoList.isEmpty()) {
                taskEmptySign.visibility = LinearLayout.VISIBLE
                rvTask.visibility = RecyclerView.INVISIBLE
            } else {
                taskEmptySign.visibility = LinearLayout.INVISIBLE
                rvTask.visibility = RecyclerView.VISIBLE
            }
        }

        return view
    }
}