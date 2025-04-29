package com.android.archives.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.data.model.Task
import com.android.archives.databinding.RecyclerviewTaskItemBinding

class TaskRecyclerAdapter(
    private val onClick: (Task) -> Unit,
    private val onCheckChanged: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: RecyclerviewTaskItemBinding) : RecyclerView.ViewHolder(binding.root)

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
        val binding = RecyclerviewTaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = differ.currentList[position]

        holder.binding.taskItemIcon.text = task.emojiIcon
        holder.binding.taskItemTitle.text = task.title
        holder.binding.taskItemDesc.text = task.description
        holder.binding.taskItemCb.isChecked = task.isComplete

        holder.binding.taskItemCb.setOnCheckedChangeListener { _, isChecked ->
            onCheckChanged(task, isChecked)
        }

        // Double-tap prevention
        var lastClickTime = 0L
        val clickInterval = 3000L // 1 second

        holder.binding.root.setOnClickListener {
            val now = android.os.SystemClock.elapsedRealtime()
            if (now - lastClickTime < clickInterval) return@setOnClickListener
            lastClickTime = now
            onClick(task)
        }
    }

}
