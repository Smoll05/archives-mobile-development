package com.android.archives.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.Upload

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_file_item, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(file)
        holder.itemView.setOnClickListener { onFileClick(file) }
    }

    override fun getItemCount(): Int = files.size

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileIcon: ImageView = itemView.findViewById(R.id.fileIcon)
        private val fileName: TextView = itemView.findViewById(R.id.fileName)

        fun bind(file: Upload) {
            fileName.text = file.name
            val extension = file.name.substringAfterLast('.', "").lowercase()
            fileIcon.setImageResource(getIconForFileType(extension))
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
