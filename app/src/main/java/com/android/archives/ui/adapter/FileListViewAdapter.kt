package com.android.archives.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.archives.R

class FileListViewAdapter(context: Context, private val fileList: MutableList<String>) :
    ArrayAdapter<String>(context, R.layout.listview_file_item, fileList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.listview_file_item, parent, false)

        val fileName = view.findViewById<TextView>(R.id.fileName)
        val container = view.findViewById<LinearLayout>(R.id.itemContainer)

        fileName.text = fileList[position]
        container.background = ContextCompat.getDrawable(context, R.drawable.shape_text_border)

        return view
    }
}