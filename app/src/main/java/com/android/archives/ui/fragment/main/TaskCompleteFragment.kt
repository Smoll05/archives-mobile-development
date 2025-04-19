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
import com.android.archives.databinding.FragmentSettingsBinding
import com.android.archives.databinding.FragmentTaskCompleteBinding
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.SpacingDecorator
import com.android.archives.utils.collectLatestOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskCompleteFragment : Fragment() {

    private var _binding: FragmentTaskCompleteBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: TaskRecyclerAdapter
    private val taskViewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_complete, container, false)

        val taskEmptySign = binding.taskCompleteEmpty
        val rvComplete = binding.taskCompleteRecyclerView

        rvComplete.addItemDecoration(
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

        rvComplete.adapter = adapter
        rvComplete.layoutManager = LinearLayoutManager(requireContext())

        collectLatestOnViewLifecycle(taskViewModel.state) { state ->
            if (state.isLoading) {
                rvComplete.isEnabled = false
                return@collectLatestOnViewLifecycle
            } else {
                rvComplete.isEnabled = true
            }

            val todoList = state.completeTask

            adapter.differ.submitList(todoList)

            if(todoList.isEmpty()) {
                taskEmptySign.visibility = LinearLayout.VISIBLE
                rvComplete.visibility = RecyclerView.INVISIBLE
            } else {
                taskEmptySign.visibility = LinearLayout.INVISIBLE
                rvComplete.visibility = RecyclerView.VISIBLE
            }
        }

        return view
    }

}