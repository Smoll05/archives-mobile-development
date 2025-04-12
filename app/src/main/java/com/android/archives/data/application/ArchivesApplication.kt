package com.android.archives.data.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.android.archives.data.model.Schedule
import com.android.archives.data.model.Task

class ArchivesApplication : Application() {
    var taskList : MutableList<Task> = mutableListOf(
        Task(1, "Mobile Development: ListView", "Implement a scrollable list using ListView in a mobile app.", "📱", false),
        Task(2, "Java OOP: Graph User Interface", "Design a graphical user interface (GUI) using Java and OOP principles.", "🖥️", false),
        Task(3, "DAA: Implement Sorting Algorithms", "Develop and analyze sorting algorithms for efficient data ordering.",  "🔢", false),
        Task(4, "IM: Learn SQL CRUD Operations", "Practice SQL queries for Creating, Reading, Updating, and Deleting data.",  "💾", false),
        Task(5, "Android Dev: Implement RecyclerView", "Create a dynamic list using RecyclerView in Android.", "📱", false),
        Task(6, "JavaFX: Build a Desktop App", "Develop a simple desktop application using JavaFX.", "🖥️", false),
        Task(7, "Data Structures: Implement a Linked List", "Write a custom linked list implementation in Java.", "🔗", false),
        Task(8, "AI: Train a Machine Learning Model", "Use Python and TensorFlow to train a basic ML model.", "🤖", false),
        Task(9, "Cybersecurity: Study OWASP Top 10", "Learn about the top web security vulnerabilities.", "🔒", false),
        Task(10, "Cloud Computing: Deploy on AWS", "Host an application on AWS using EC2 or Lambda.", "☁️", false),
        Task(11, "Kotlin: Build a Simple Android App", "Develop a basic Android application using Kotlin.", "📲", false),
        Task(12, "Git: Learn Branching & Merging", "Practice Git branching strategies for collaboration.", "🌿", false),
        Task(13, "Game Dev: Create a 2D Game", "Use libGDX to build a simple 2D game.", "🎮", false),
    )

    val scheduleList : MutableList<Schedule> = mutableListOf(

    )

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}