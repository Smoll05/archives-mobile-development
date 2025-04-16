package com.android.archives.ui.fragment.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.android.archives.R
import com.android.archives.data.model.FolderItem
import com.android.archives.databinding.FragmentMainCourseBinding
import com.android.archives.ui.adapter.FolderAdapter
import com.android.archives.ui.fragment.AddCourseDialogFragment
import com.android.archives.ui.fragment.main.FilesFragment.Companion.deleteFilesForCourse
import com.android.archives.ui.viewmodel.MainCourseViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class MainCourseFragment : Fragment() {

    private var _binding: FragmentMainCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FolderAdapter
    private lateinit var currentUser: String

    private val viewModel: MainCourseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentUser = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("currentUser", "") ?: ""

        if (viewModel.getFolders().isEmpty()) {
            loadFoldersFromStorage()
        }

        setupRecyclerView()
        setupSearchView()
        setupAddButtons()
        toggleEmptyState(viewModel.getFolders())
    }
    private fun setupRecyclerView() {
        adapter = FolderAdapter(
            folderList = viewModel.getFolders().toMutableList(),
            onItemClick = { folderItem ->
//                val intent = Intent(requireContext(), FilesActivity::class.java).apply {
//                    putExtra("courseTitle", folderItem.name)
//                    putExtra("coverImageUri", folderItem.coverImageUri)
//                    putExtra("profileImageUri", folderItem.profileImageUri)
//                }
//                startActivity(intent)
                FilesFragment().show(parentFragmentManager, "FullScreenDialog")
            },
            onItemLongClick = { folderItem ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Folder")
                    .setMessage("Are you sure you want to delete the folder \"${folderItem.name}\" and all files inside it?")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteFolderAndFiles(folderItem)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun deleteFolderAndFiles(folderItem: FolderItem) {
        val updatedList = viewModel.getFolders().toMutableList().apply {
            removeAll { it.name == folderItem.name }
        }
        viewModel.setFolders(updatedList)
        adapter.updateList(updatedList)
        saveFoldersToStorage()

        // Also delete all files associated with this folder
        deleteFilesForCourse(requireContext(), currentUser, folderItem.name)

        toggleEmptyState(updatedList)
    }


    private fun deleteFilesForFolder(courseTitle: String) {
        val prefsName = "files_${currentUser}_$courseTitle"
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()

        // Optional: Actually remove the prefs file by writing nothing
        requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            .edit()
            .remove("file_list") // Assuming your file data is stored under this key
            .apply()
    }



    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        try {
            val searchEditText = binding.searchView.findViewById<EditText>(
                androidx.appcompat.R.id.search_src_text
            )
            searchEditText?.apply {
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                setHintTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupAddButtons() {
        val showAddDialog = {
            val dialog = AddCourseDialogFragment { newFolder ->
                // Check for duplicate folder names (case-insensitive)
                val folderExists = viewModel.getFolders().any {
                    it.name.equals(newFolder.name, ignoreCase = true)
                }

                if (folderExists) {
                    // Show simple alert dialog to notify user
                    AlertDialog.Builder(requireContext())
                        .setTitle("Duplicate Folder")
                        .setMessage("A folder with the name \"${newFolder.name}\" already exists. Please choose a different name.")
                        .setPositiveButton("OK", null)
                        .show()
                } else {
                    viewModel.addFolder(newFolder)
                    adapter.updateList(viewModel.getFolders())
                    saveFoldersToStorage()
                    toggleEmptyState(viewModel.getFolders())
                }
            }
            dialog.show(parentFragmentManager, "AddCourseDialog")
        }

        binding.addButton.setOnClickListener { showAddDialog() }
        binding.centerAddButton.setOnClickListener { showAddDialog() }
    }


    private fun toggleEmptyState(list: List<FolderItem>) {
        val isEmpty = list.isEmpty()
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.addButton.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun saveFoldersToStorage() {
        val prefs = requireContext().getSharedPreferences("course_folders_$currentUser", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val jsonArray = JSONArray()
        viewModel.getFolders().forEach { folder ->
            val obj = JSONObject().apply {
                put("title", folder.title)
                put("name", folder.name)
                put("iconRes", folder.iconRes)
                put("coverImageUri", folder.coverImageUri)
                put("profileImageUri", folder.profileImageUri)
            }
            jsonArray.put(obj)
        }
        editor.putString("folders", jsonArray.toString()).apply()
    }

    private fun loadFoldersFromStorage() {
        val prefs = requireContext().getSharedPreferences("course_folders_$currentUser", Context.MODE_PRIVATE)
        val folderJson = prefs.getString("folders", null) ?: return
        val jsonArray = JSONArray(folderJson)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val title = obj.optString("title", "")
            val name = obj.optString("name", title)
            val iconRes = obj.optInt("iconRes", R.drawable.ic_folder)
            val coverImageUri = obj.optString("coverImageUri", null)
            val profileImageUri = obj.optString("profileImageUri", null)
            viewModel.addFolder(FolderItem(title, name, iconRes, coverImageUri, profileImageUri))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
