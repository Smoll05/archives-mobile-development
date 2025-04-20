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
import com.android.archives.databinding.FragmentTaskCompleteBinding
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.utils.SpacingDecorator
import com.android.archives.utils.collectLatestOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskCompleteFragment : Fragment() {
    private lateinit var adapter: TaskRecyclerAdapter
    private val taskViewModel: TaskViewModel by activityViewModels()

    private var _binding: FragmentTaskCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTaskCompleteBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskEmptySign = view.findViewById<LinearLayout>(R.id.task_complete_empty)
        val rvComplete = view.findViewById<RecyclerView>(R.id.task_complete_recycler_view)

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
                taskViewModel.onEvent(TaskEvent.SetTaskCompletion(task, isChecked))
            }
        )

        rvComplete.adapter = adapter
        rvComplete.layoutManager = LinearLayoutManager(requireContext())

        loadTask()
    }

    override fun onResume() {
        super.onResume()
        loadTask()
    }

    private fun loadTask() {
        collectLatestOnViewLifecycle(taskViewModel.state) { state ->
            if (state.isLoading) {
                binding.taskCompleteRecyclerView.isEnabled = false
                return@collectLatestOnViewLifecycle
            } else {
                binding.taskCompleteRecyclerView.isEnabled = true
            }

            Log.d("Task", "I am updated ${state.title}")

            val completeList = state.completeTask

            adapter.differ.submitList(completeList)

            if(completeList.isEmpty()) {
                binding.taskCompleteEmpty.visibility = LinearLayout.VISIBLE
                binding.taskCompleteRecyclerView.visibility = RecyclerView.INVISIBLE
            } else {
                binding.taskCompleteEmpty.visibility = LinearLayout.INVISIBLE
                binding.taskCompleteRecyclerView.visibility = RecyclerView.VISIBLE
            }
        }
    }

}