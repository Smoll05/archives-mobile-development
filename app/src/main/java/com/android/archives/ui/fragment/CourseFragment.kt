package com.android.archives.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.android.archives.R
import com.android.archives.ui.adapter.FileListViewAdapter

class CourseFragment : Fragment() {
    private lateinit var fileAdapter: ArrayAdapter<String>
    private lateinit var fileList: ListView
    private lateinit var searchBar: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnDelete: Button

    private val files = mutableListOf(
        "document.pdf", "report.pdf",
        "notes.txt", "todo.txt",
        "presentation.ppt", "slides.ppt",
        "summary.pdf", "draft.txt",
        "meeting_notes.ppt", "project_plan.docx",
        "budget.xlsx", "invoice.pdf",
        "resume.docx", "cover_letter.docx",
        "schedule.xlsx", "proposal.pdf",
        "design_mockup.png", "wireframe.sketch",
        "app_logo.svg", "requirements.docx",
        "log.txt", "backup.zip",
        "database.sql", "script.py",
        "config.json", "data.csv",
        "presentation_final.ppt", "assignment.docx",
        "references.pdf", "bookmarks.html",
        "manual.pdf", "policy.docx"
    )

    private val filteredFiles = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course, container, false)

        fileList = view.findViewById(R.id.fileList)
        searchBar = view.findViewById(R.id.searchBar)
        btnAdd = view.findViewById(R.id.btnAdd)
        btnDelete = view.findViewById(R.id.btnDelete)

        filteredFiles.addAll(files)

        fileAdapter = FileListViewAdapter(requireContext(), filteredFiles)
        fileList.adapter = fileAdapter

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
        })

        btnAdd.setOnClickListener {
            showAddFileDialog()
        }

        btnDelete.setOnClickListener {
            showDeleteSelection()
        }
        fileList.setOnItemClickListener{ _, _, position, _ ->
            val selectedFile = filteredFiles[position];
            Toast.makeText(requireContext(), "File Selected", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun showAddFileDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New File")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val input = EditText(requireContext())
        input.hint = "Enter file name"
        layout.addView(input)

        val fileTypes = arrayOf(".txt", ".docx", ".pdf", ".mp4", ".mp3")
        val fileTypeSpinner = Spinner(requireContext())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fileTypes)
        fileTypeSpinner.adapter = adapter
        layout.addView(fileTypeSpinner)
        builder.setView(layout)

        builder.setPositiveButton("Add") { _, _ ->
            val fileName = input.text.toString().trim()
            val fileType = fileTypeSpinner.selectedItem.toString()
            if (fileName.isNotEmpty()) {
                files.add("$fileName$fileType")
                filterList(searchBar.text.toString())
            } else {
                Toast.makeText(requireContext(), "File name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showDeleteSelection() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select a file to delete")
        builder.setSingleChoiceItems(files.toTypedArray(), -1) { dialog, which ->
            files.removeAt(which)
            filterList(searchBar.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun filterList(query: String) {
        filteredFiles.clear()
        if (query.isEmpty()) {
            filteredFiles.addAll(files)
        } else {
            filteredFiles.addAll(files.filter { it.contains(query, ignoreCase = true) })
        }
        fileAdapter.notifyDataSetChanged()
    }
}