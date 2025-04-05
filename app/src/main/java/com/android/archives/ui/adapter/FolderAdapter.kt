package com.android.archives.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.ui.Class.FolderItem
class FolderAdapter(
    private var folderList: List<FolderItem>,
    private val onItemClick: (FolderItem) -> Unit,
    private val onItemLongClick: (FolderItem) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>(), Filterable {

    private var filteredList: List<FolderItem> = folderList

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderIcon: ImageView = itemView.findViewById(R.id.folderIcon)
        val folderName: TextView = itemView.findViewById(R.id.folderName)

        fun bind(item: FolderItem) {
            folderIcon.setImageResource(item.iconRes)
            folderName.text = item.name
            itemView.setOnClickListener { onItemClick(item) }
            itemView.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
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
