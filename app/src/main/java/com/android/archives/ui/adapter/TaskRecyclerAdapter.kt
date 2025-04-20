package com.android.archives.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.Task

class TaskRecyclerAdapter (
    private val onClick : (Task) -> Unit,
    private val onCheckChanged: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: TextView = view.findViewById(R.id.task_item_icon)
        val title: TextView = view.findViewById(R.id.task_item_title)
        val desc: TextView = view.findViewById(R.id.task_item_desc)
        val checkbox: CheckBox = view.findViewById(R.id.task_item_cb)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = differ.currentList[position]

        holder.icon.text = task.emojiIcon
        holder.title.text = task.title
        holder.desc.text = task.description
        holder.checkbox.isChecked = task.isComplete

        holder.checkbox.setOnCheckedChangeListener {_, isChecked ->
            onCheckChanged(task, isChecked)
        }

        holder.itemView.setOnClickListener {
            onClick(task)
        }
    }
}