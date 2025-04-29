package com.android.archives.ui.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.File

class FileRecyclerAdapter(
    private val onFileClick: (File) -> Unit
) : RecyclerView.Adapter<FileRecyclerAdapter.FileViewHolder>(), Filterable{

    private var fullList: List<File> = listOf()

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileIcon: ImageView = itemView.findViewById(R.id.fileIcon)
        private val fileName: TextView = itemView.findViewById(R.id.fileName)

        private var lastClickTime: Long = 0
        private val clickInterval: Long = 3000 // 1 second

        fun bind(file: File) {
            fileName.text = file.fileName
            fileIcon.setImageResource(getIconForFileType(file.fileType))

            itemView.setOnClickListener {
                val now = SystemClock.elapsedRealtime()
                if (now - lastClickTime < clickInterval) return@setOnClickListener

                lastClickTime = now
                onFileClick(file)
            }
        }

        private fun getIconForFileType(extension: String): Int {
            return when (extension.lowercase()) {
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


    private val differCallback = object : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.fileId == newItem.fileId
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_file_item, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = differ.currentList[position]
        holder.bind(file)
        holder.itemView.setOnClickListener { onFileClick(file) }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                val filtered = if (charSearch.isEmpty()) {
                    fullList
                } else {
                    fullList.filter {
                        it.fileName.contains(charSearch, ignoreCase = true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val filtered = results?.values as? List<File> ?: emptyList()
                differ.submitList(filtered)
            }
        }
    }

    fun submitList(newList: List<File>) {
        fullList = newList
        differ.submitList(newList)
    }

    fun clearFilter() {
        differ.submitList(fullList)
    }

    val currentList: List<File>
        get() = differ.currentList

}