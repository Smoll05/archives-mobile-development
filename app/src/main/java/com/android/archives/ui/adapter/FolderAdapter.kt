package com.android.archives.ui.adapter

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
import com.android.archives.data.model.FolderItem

class FolderAdapter(
    private val onItemClick: (FolderItem) -> Unit,
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>(), Filterable {

    private var fullList: List<FolderItem> = listOf()

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderIcon: ImageView = itemView.findViewById(R.id.folderIcon)
        private val folderName: TextView = itemView.findViewById(R.id.folderName)

        fun bind(item: FolderItem) {
            folderIcon.setImageResource(item.iconRes)
            folderName.text = item.name
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<FolderItem>() {
        override fun areItemsTheSame(oldItem: FolderItem, newItem: FolderItem): Boolean {
            return oldItem.folderId == newItem.folderId
        }

        override fun areContentsTheSame(oldItem: FolderItem, newItem: FolderItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
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
                        it.name.contains(charSearch, ignoreCase = true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val filtered = results?.values as? List<FolderItem> ?: emptyList()
                differ.submitList(filtered)
            }
        }
    }

    fun submitList(newList: List<FolderItem>) {
        fullList = newList
        differ.submitList(newList)
    }

    fun clearFilter() {
        differ.submitList(fullList)
    }

}
