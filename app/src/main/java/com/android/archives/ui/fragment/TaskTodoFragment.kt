package com.android.archives.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.application.ArchivesApplication
import com.android.archives.data.model.Task
import com.android.archives.ui.activity.TaskDetailViewActivity
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.utils.SpacingDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskTodoFragment : Fragment() {
    lateinit var adapter: TaskRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_todo, container, false)
        val app = requireActivity().application as ArchivesApplication

        val taskEmptySign = view.findViewById<LinearLayout>(R.id.task_todo_empty)
        val todoList : MutableList<Task> = app.taskList.filter {!it.isComplete} .toMutableList()
        val rvTask = view.findViewById<RecyclerView>(R.id.task_todo_recycler_view)

        rvTask.addItemDecoration(
            SpacingDecorator(0, 0, 0, 24)
        )

        adapter = TaskRecyclerAdapter(
            todoList,
            onClick = { task ->
                Toast.makeText(requireContext(), "On click: ${task.description}", Toast.LENGTH_SHORT).show()

                startActivity(
                    Intent(requireContext(), TaskDetailViewActivity::class.java).apply {
                        putExtra("task_id", task.taskId)
                    }
                )
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

        if(todoList.isEmpty()) {
            taskEmptySign.visibility = LinearLayout.VISIBLE
            rvTask.visibility = RecyclerView.INVISIBLE
        } else {
            taskEmptySign.visibility = LinearLayout.INVISIBLE
            rvTask.visibility = RecyclerView.VISIBLE
        }


        return view
    }
}