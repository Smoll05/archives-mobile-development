package com.android.archives.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.Task
import com.android.archives.ui.adapter.TaskRecyclerAdapter
import com.android.archives.utils.SpacingDecorator

class TaskTodoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_todo, container, false)


        val rvTask = view.findViewById<RecyclerView>(R.id.task_todo_recycler_view)
        rvTask.addItemDecoration(
            SpacingDecorator(0, 0, 0, 24)
        )

        val taskList = listOf(
            Task("Mobile Development: ListView", "Implement a scrollable list using ListView in a mobile app.", "📱", false),
            Task("Java OOP: Graph User Interface", "Design a graphical user interface (GUI) using Java and OOP principles.", "🖥️", false),
            Task("DAA: Implement Sorting Algorithms", "Develop and analyze sorting algorithms for efficient data ordering.",  "🔢", false),
            Task("IM: Learn SQL CRUD Operations", "Practice SQL queries for Creating, Reading, Updating, and Deleting data.",  "💾", false),
            Task("Android Dev: Implement RecyclerView", "Create a dynamic list using RecyclerView in Android.", "📱", false),
            Task("JavaFX: Build a Desktop App", "Develop a simple desktop application using JavaFX.", "🖥️", false),
            Task("Data Structures: Implement a Linked List", "Write a custom linked list implementation in Java.", "🔗", false),
            Task("AI: Train a Machine Learning Model", "Use Python and TensorFlow to train a basic ML model.", "🤖", false),
            Task("Cybersecurity: Study OWASP Top 10", "Learn about the top web security vulnerabilities.", "🔒", false),
            Task("Cloud Computing: Deploy on AWS", "Host an application on AWS using EC2 or Lambda.", "☁️", false),
            Task("Kotlin: Build a Simple Android App", "Develop a basic Android application using Kotlin.", "📲", false),
            Task("Git: Learn Branching & Merging", "Practice Git branching strategies for collaboration.", "🌿", false),
            Task("Game Dev: Create a 2D Game", "Use libGDX to build a simple 2D game.", "🎮", false),
        )

        val adapter = TaskRecyclerAdapter(
            taskList,
            onClick = { task ->
                Toast.makeText(requireContext(), "On click click: ${task.description}", Toast.LENGTH_SHORT).show()
            }
        )

        rvTask.adapter = adapter
        rvTask.layoutManager = LinearLayoutManager(requireContext())


        return view
    }
}