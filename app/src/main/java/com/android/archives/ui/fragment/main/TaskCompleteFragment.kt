package com.android.archives.ui.fragment.main

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
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.utils.SpacingDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskCompleteFragment : Fragment() {
    lateinit var adapter: TaskRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_complete, container, false)

        val app = requireActivity().application as ArchivesApplication
        val taskEmptySign = view.findViewById<LinearLayout>(R.id.task_complete_empty)
        val completeList : MutableList<Task> = app.taskList.filter {it.isComplete} .toMutableList()
        val rvComplete = view.findViewById<RecyclerView>(R.id.task_complete_recycler_view)

        rvComplete.addItemDecoration(
            SpacingDecorator(0, 0, 0, 24)
        )

        adapter = TaskRecyclerAdapter(
            completeList,
            onClick = { task ->
                Toast.makeText(requireContext(), "On click click: ${task.description}", Toast.LENGTH_SHORT).show()
            },

            onCheckChanged = { task, isChecked ->
                task.isComplete = isChecked

                Toast.makeText(requireContext(), "index ${completeList.indexOf(task)}", Toast.LENGTH_SHORT).show()

                val position = completeList.indexOf(task)
                if (position != -1) {
                    completeList.removeAt(position)

                    rvComplete.post {
                        adapter.notifyItemRemoved(position)
                    }
                }
            }
        )

        rvComplete.adapter = adapter
        rvComplete.layoutManager = LinearLayoutManager(requireContext())

        if(completeList.isEmpty()) {
            taskEmptySign.visibility = LinearLayout.VISIBLE
            rvComplete.visibility = RecyclerView.INVISIBLE
        } else {
            taskEmptySign.visibility = LinearLayout.INVISIBLE
            rvComplete.visibility = RecyclerView.VISIBLE
        }

        return view
    }
}