package com.android.archives.ui.fragment.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.Upload
import com.android.archives.databinding.FragmentFilesBinding
import com.android.archives.ui.adapter.FileListAdapter
import com.android.archives.ui.viewmodel.UserViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class FilesFragment : DialogFragment() {
    private var _binding: FragmentFilesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    private lateinit var fileAdapter: FileListAdapter
    private val files = mutableListOf<Upload>()
    private val filteredFiles = mutableListOf<Upload>()

    private lateinit var currentUser: String
    private lateinit var courseKey: String

    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }
    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFilesBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.view = view

        val courseName = requireActivity().intent.getStringExtra("courseTitle") ?: "Course"
        val coverUri = requireActivity().intent.getStringExtra("coverImageUri")
        val profileUri = requireActivity().intent.getStringExtra("profileImageUri")

        courseKey = courseName.replace(" ", "_")
        currentUser = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("currentUser", "") ?: ""

        setupHeader(courseName, coverUri, profileUri)
        setupRecyclerView()
        loadFilesFromStorage()
        filterList("") // <- This ensures the adapter updates right after loading
        setupAddButton()
        setupSearchBar()
        setupSwipeToDelete()

        binding.btnBACK.setOnClickListener {
            dismiss()
        }
    }

    private fun setupHeader(name: String, coverUri: String?, profileUri: String?) {
        binding.CourseName.text = name

//        val coverImage = view.findViewById<ImageView>(R.id.coverPhoto)
//        val profileImage = view.findViewById<ImageView>(R.id.profilePhoto)

//        coverImage.setImageURI(coverUri?.let { Uri.parse(it) })
//        if (coverUri.isNullOrEmpty()) coverImage.setImageResource(R.drawable.gray_placeholder)
//
//        profileImage.setImageURI(profileUri?.let { Uri.parse(it) })
//        if (profileUri.isNullOrEmpty()) profileImage.setImageResource(R.drawable.gray_placeholder)
    }

    private fun setupRecyclerView() {
        fileAdapter = FileListAdapter { file -> openFile(Uri.parse(file.uri)) }
        binding.fileRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.fileRecyclerView.adapter = fileAdapter
        fileAdapter.updateFiles(filteredFiles)
    }

    private fun setupAddButton() {
        binding.btnAdd.setOnClickListener {
            filePickerLauncher.launch(arrayOf("*/*"))
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val fileName = getFileName(it)
            val newFile = Upload(fileName, it.toString())
            files.add(newFile)
            saveFilesToStorage()
            filterList(binding.searchBar.text.toString())
        }
    }

    companion object {
        fun deleteFilesForCourse(context: Context, userId: String, courseTitle: String) {
            val prefs = context.getSharedPreferences("uploaded_files_$userId", Context.MODE_PRIVATE)
            val courseKey = courseTitle.replace(" ", "_")
            prefs.edit().remove(courseKey).apply()
        }
    }


    private fun getFileName(uri: Uri): String {
        requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) return cursor.getString(nameIndex)
        }
        return "Unknown File"
    }

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filterList(s.toString())
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterList(query: String) {
        filteredFiles.clear()
        val lowerQuery = query.lowercase(Locale.ROOT)
        filteredFiles.addAll(files.filter { it.name.lowercase(Locale.ROOT).contains(lowerQuery) })
        fileAdapter.updateFiles(filteredFiles)
        binding.noFilesText.visibility = if (filteredFiles.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val fileToDelete = filteredFiles[position]
                AlertDialog.Builder(context)
                    .setTitle("Delete File")
                    .setMessage("Are you sure you want to delete \"${fileToDelete.name}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        files.remove(fileToDelete)
                        saveFilesToStorage()
                        filterList(binding.searchBar.text.toString())
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        fileAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.fileRecyclerView)
    }

    private fun openFile(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, requireActivity().contentResolver.getType(uri) ?: "*/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, "Open file with"))
    }

    private fun saveFilesToStorage() {
        val sharedPrefs = requireActivity().getSharedPreferences("uploaded_files_$currentUser", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        files.forEach {
            val obj = JSONObject()
            obj.put("name", it.name)
            obj.put("uri", it.uri)
            jsonArray.put(obj)
        }
        sharedPrefs.edit().putString(courseKey, jsonArray.toString()).apply()
    }

    private fun loadFilesFromStorage() {
        val sharedPrefs = requireActivity().getSharedPreferences("uploaded_files_$currentUser", Context.MODE_PRIVATE)
        val jsonString = sharedPrefs.getString(courseKey, null) ?: return
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val name = obj.getString("name")
            val uri = obj.getString("uri")
            files.add(Upload(name, uri))
        }
        filteredFiles.addAll(files)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}