package com.android.archives.ui.fragment.main

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.android.archives.R
import com.android.archives.databinding.FragmentAddFileDialogueBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFileDialogueFragment : BottomSheetDialogFragment() {

    interface OnFileAddedListener {
        fun onFileAdded(fileName: String, uri: Uri)
    }

    private var listener: OnFileAddedListener? = null

    private var _binding: FragmentAddFileDialogueBinding? = null
    private val binding get() = _binding!!

    private var selectedUri: Uri? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            binding.plusIcon.visibility = View.GONE
            binding.removeButton.visibility = View.VISIBLE

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
            binding.titleInput.setText(fullName)
            binding.uploadText.text = "File selected: $fullName"
        }
    }
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up);
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
    ): View? {
        _binding = FragmentAddFileDialogueBinding.inflate(inflater, container, false)

        // Access views through the binding object
        binding.uploadBox.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        binding.removeButton.setOnClickListener {
            selectedUri = null
            binding.uploadText.text = "Click here to upload file"
            binding.plusIcon.visibility = View.VISIBLE
            binding.removeButton.visibility = View.GONE
        }

        binding.uploadButton.setOnClickListener {
            val title = binding.titleInput.text.toString()
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

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        _binding = null
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
