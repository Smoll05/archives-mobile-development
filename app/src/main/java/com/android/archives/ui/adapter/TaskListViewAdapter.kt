package com.android.archives.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.android.archives.R
import com.android.archives.data.model.Task

class TaskListViewAdapter(
    private val context: Context,
    private val taskList: List<Task>,
    private val onClick: (Task) -> Unit,
    private val onLongClicK: (Task) -> Unit
): BaseAdapter() {
    override fun getCount(): Int = taskList.size

    override fun getItem(position: Int): Any = taskList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.listview_task_item, parent, false)

        val taskIcon = view.findViewById<TextView>(R.id.tfIcon)
        val taskTitle = view.findViewById<TextView>(R.id.tfTitle)
        val taskDescription = view.findViewById<TextView>(R.id.tfDescription)

        val task = taskList[position]
        taskIcon.setText(task.emojiIcon)
        taskTitle.setText(task.title)
        taskDescription.setText(task.description)

        view.setOnClickListener {
            onClick(task)
        }

        view.setOnLongClickListener {
            onLongClicK(task)
            true
        }

        return view
    }
}