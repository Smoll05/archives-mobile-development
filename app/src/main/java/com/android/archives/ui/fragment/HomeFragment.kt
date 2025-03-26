package com.android.archives.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.archives.R
import com.android.archives.data.model.Task
import com.android.archives.ui.adapter.TaskListViewAdapter

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val listView = view.findViewById<ListView>(R.id.task_listview)  // emoji
        val taskList = listOf(
            Task(
                "Mobile Development: ListView",
                "Implement a scrollable list using ListView in a mobile app.",
                "📱" // phone emoji
            ),
            Task("Java OOP: Graph User Interface", "Design a graphical user interface (GUI) using Java and OOP principles.", "🖥️"),
            Task("DAA: Implement Sorting Algorithms", "Develop and analyze sorting algorithms for efficient data ordering.",  "🔢"),
            Task("IM: Learn SQL CRUD Operations", "Practice SQL queries for Creating, Reading, Updating, and Deleting data.",  "💾"),
            Task("Android Dev: Implement RecyclerView", "Create a dynamic list using RecyclerView in Android.", "📱"),
            Task("JavaFX: Build a Desktop App", "Develop a simple desktop application using JavaFX.", "🖥️"),
            Task("Data Structures: Implement a Linked List", "Write a custom linked list implementation in Java.", "🔗"),
            Task("AI: Train a Machine Learning Model", "Use Python and TensorFlow to train a basic ML model.", "🤖"),
            Task("Cybersecurity: Study OWASP Top 10", "Learn about the top web security vulnerabilities.", "🔒"),
            Task("Cloud Computing: Deploy on AWS", "Host an application on AWS using EC2 or Lambda.", "☁️"),
            Task("Kotlin: Build a Simple Android App", "Develop a basic Android application using Kotlin.", "📲"),
            Task("Git: Learn Branching & Merging", "Practice Git branching strategies for collaboration.", "🌿"),
            Task("Game Dev: Create a 2D Game", "Use libGDX to build a simple 2D game.", "🎮"),
        )

        val adapter = TaskListViewAdapter(
            requireContext(), taskList,
            onClick = { task ->
                Toast.makeText(requireContext(), "On click: ${task.title}", Toast.LENGTH_LONG).show()
            },
            onLongClicK = { task ->
                Toast.makeText(requireContext(), "On long click: ${task.description}", Toast.LENGTH_LONG).show()
            }
        )

        listView.adapter = adapter

        return view
    }
}