package com.android.archives.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.Task

class TaskRecyclerAdapter (
    private var taskList : List<Task>,
    private val onClick : (Task) -> Unit,
) : RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon = view.findViewById<TextView>(R.id.task_item_icon)
        val title = view.findViewById<TextView>(R.id.task_item_title)
        val desc = view.findViewById<TextView>(R.id.task_item_desc)
        val checkbox = view.findViewById<CheckBox>(R.id.task_item_cb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.icon.text = task.emojiIcon
        holder.title.text = task.title
        holder.desc.text = task.description
        holder.checkbox.isChecked = task.isComplete

        holder.itemView.setOnClickListener {
            onClick(task)
        }
    }
}