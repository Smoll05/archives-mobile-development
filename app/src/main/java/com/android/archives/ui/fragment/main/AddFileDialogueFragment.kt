package com.android.archives.ui.fragment.main

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.archives.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFileDialogueFragment : BottomSheetDialogFragment() {

    interface OnFileAddedListener {
        fun onFileAdded(fileName: String, uri: Uri)
    }

    private var listener: OnFileAddedListener? = null

    private lateinit var uploadBox: FrameLayout
    private lateinit var plusIcon: ImageView
    private lateinit var uploadText: TextView
    private lateinit var titleInput: EditText
    private lateinit var removeButton: ImageButton
    private lateinit var uploadButton: Button

    private var selectedUri: Uri? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            plusIcon.visibility = View.GONE
            removeButton.visibility = View.VISIBLE

            val contentResolver = requireContext().contentResolver
            val type = contentResolver.getType(it)
            val extension = when {
                type?.contains("pdf") == true -> ".pdf"
                type?.contains("msword") == true ||
                        type?.contains("officedocument.wordprocessingml") == true -> ".docx"
                type?.contains("presentation") == true ||
                        type?.contains("powerpoint") == true -> ".pptx"
                else -> ""
            }

            val fileName = getFileNameFromUri(it)?.substringBeforeLast('.') ?: "Untitled"
            val fullName = fileName + extension
            titleInput.setText(fullName)
            uploadText.text = "File selected: $fullName"
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFileAddedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFileAddedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_file_dialogue, container, false)

        uploadBox = view.findViewById(R.id.uploadBox)
        plusIcon = view.findViewById(R.id.plusIcon)
        uploadText = view.findViewById(R.id.uploadText)
        titleInput = view.findViewById(R.id.titleInput)
        removeButton = view.findViewById(R.id.removeButton)
        uploadButton = view.findViewById(R.id.uploadButton)

        uploadBox.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        removeButton.setOnClickListener {
            selectedUri = null
            uploadText.text = "Click here to upload file"
            plusIcon.visibility = View.VISIBLE
            removeButton.visibility = View.GONE
        }

        uploadButton.setOnClickListener {
            val title = titleInput.text.toString()
            if (selectedUri != null && title.isNotBlank()) {
                listener?.onFileAdded(title, selectedUri!!)
                Toast.makeText(requireContext(), "File uploaded!", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select a file and enter a title.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }
}
