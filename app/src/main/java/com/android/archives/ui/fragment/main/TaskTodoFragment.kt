package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.archives.application.ArchivesApplication
import com.android.archives.data.model.Task
import com.android.archives.databinding.FragmentTaskTodoBinding
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.utils.SpacingDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskTodoFragment : Fragment() {
    private var _binding: FragmentTaskTodoBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: TaskRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskTodoBinding.inflate(inflater, container, false)
        val view = binding.root
        val app = requireActivity().application as ArchivesApplication

        val taskEmptySign = binding.taskTodoEmpty
        val rvTask = binding.taskTodoRecyclerView
        val todoList: MutableList<Task> = app.taskList.filter { !it.isComplete }.toMutableList()

        rvTask.addItemDecoration(
            SpacingDecorator(0, 0, 0, 24)
        )

        adapter = TaskRecyclerAdapter(
            todoList,
            onClick = { task ->
                Toast.makeText(requireContext(), "On click: ${task.description}", Toast.LENGTH_SHORT).show()
                TaskDetailViewFragment().show(parentFragmentManager, "FullScreenDialog")
            },

            onCheckChanged = { task, isChecked ->
                task.isComplete = isChecked

                Toast.makeText(requireContext(), "index ${todoList.indexOf(task)}", Toast.LENGTH_SHORT).show()

                val position = todoList.indexOf(task)
                if (position != -1) {
                    todoList.removeAt(position)

                    rvTask.post {
                        adapter.notifyItemRemoved(position)
                    }
                }
            }
        )

        rvTask.adapter = adapter
        rvTask.layoutManager = LinearLayoutManager(requireContext())

        if (todoList.isEmpty()) {
            taskEmptySign.visibility = View.VISIBLE
            rvTask.visibility = View.INVISIBLE
        } else {
            taskEmptySign.visibility = View.INVISIBLE
            rvTask.visibility = View.VISIBLE
        }

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
