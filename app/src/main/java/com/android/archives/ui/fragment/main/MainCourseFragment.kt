package com.android.archives.ui.fragment.main

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.android.archives.databinding.FragmentMainCourseBinding
import com.android.archives.ui.adapter.FolderAdapter
import com.android.archives.ui.event.FileEvent
import com.android.archives.ui.event.FolderEvent
import com.android.archives.ui.viewmodel.FileViewModel
import com.android.archives.ui.viewmodel.FolderViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainCourseFragment : Fragment() {

    private var _binding: FragmentMainCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FolderAdapter
    private lateinit var currentUser: String

    private var lastClickTime: Long = 0 // ✅ Time tracker for last click
    private val clickInterval: Long = 1000 // ✅ Interval to prevent double-tap (1 second)

    private val folderViewModel: FolderViewModel by activityViewModels()
    private val fileViewModel: FileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCourseBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentUser = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("currentUser", "") ?: ""

        setupSearchView()
        setupAddButtons()
    }

    private fun setupRecyclerView() {
        adapter = FolderAdapter { folderItem ->
            fileViewModel.onEvent(FileEvent.SetFolderId(folderItem.folderId))

            val filesFragment = FilesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("folder", folderItem)
                }
            }

            filesFragment.show(parentFragmentManager, "FullScreenDialog")
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        collectLatestOnViewLifecycle(folderViewModel.state) { state ->
            val folderList = state.folderList
            adapter.submitList(folderList)

            val isEmpty = folderList.isEmpty()
            binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.addButton.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    adapter.clearFilter()
                } else {
                    adapter.filter.filter(newText)
                }
                return true
            }
        })

        try {
            val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
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
                folderViewModel.onEvent(FolderEvent.SaveFolder(newFolder))
            }
            dialog.show(parentFragmentManager, "AddCourseDialog")
        }

        binding.addButton.setOnClickListener {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime < clickInterval) return@setOnClickListener // Prevent double-click

            lastClickTime = now // Update last click time
            showAddDialog()
        }

        binding.centerAddButton.setOnClickListener {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime < clickInterval) return@setOnClickListener // Prevent double-click

            lastClickTime = now // Update last click time
            showAddDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        lastClickTime = 0 // Reset click prevention flag when returning to the fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
