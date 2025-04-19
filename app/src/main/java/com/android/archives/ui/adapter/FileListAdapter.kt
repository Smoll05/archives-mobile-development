package com.android.archives.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.Upload
import com.android.archives.databinding.ListviewFileItemBinding

class FileListAdapter(
    private val onFileClick: (Upload) -> Unit
) : RecyclerView.Adapter<FileListAdapter.FileViewHolder>() {

    private val files = mutableListOf<Upload>()

    fun updateFiles(newFiles: List<Upload>) {
        files.clear()
        files.addAll(newFiles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ListviewFileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(file)
        holder.itemView.setOnClickListener { onFileClick(file) }
    }

    override fun getItemCount(): Int = files.size

    inner class FileViewHolder(private val binding: ListviewFileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: Upload) {
            binding.fileName.text = file.name
            val extension = file.name.substringAfterLast('.', "").lowercase()
            binding.fileIcon.setImageResource(getIconForFileType(extension))
        }

        private fun getIconForFileType(extension: String): Int {
            return when (extension) {
                "pdf" -> R.drawable.ic_pdf
                "doc", "docx" -> R.drawable.ic_word
                "ppt", "pptx" -> R.drawable.ic_ppt
                "xls", "xlsx" -> R.drawable.ic_excel
                "jpg", "jpeg", "png" -> R.drawable.ic_image
                "mp4", "avi", "mkv" -> R.drawable.ic_video
                else -> R.drawable.ic_file
            }
        }
    }
}
