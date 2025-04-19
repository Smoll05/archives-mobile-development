package com.android.archives.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.data.model.FolderItem
import com.android.archives.databinding.ItemFolderBinding

class FolderAdapter(
    private var folderList: List<FolderItem>,
    private val onItemClick: (FolderItem) -> Unit,
    private val onItemLongClick: (FolderItem) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>(), Filterable {

    private var filteredList: List<FolderItem> = folderList

    inner class FolderViewHolder(val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FolderItem) {
            binding.folderIcon.setImageResource(item.iconRes)
            binding.folderName.text = item.name
            binding.root.setOnClickListener { onItemClick(item) }
            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredList = if (charSearch.isEmpty()) {
                    folderList
                } else {
                    folderList.filter {
                        it.name.contains(charSearch, ignoreCase = true)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<FolderItem>
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newList: List<FolderItem>) {
        folderList = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    val currentList: List<FolderItem>
        get() = filteredList
}
