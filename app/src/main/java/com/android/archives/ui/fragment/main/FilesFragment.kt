package com.android.archives.ui.fragment.main

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.archives.R
import com.android.archives.data.model.File
import com.android.archives.data.model.FolderItem
import com.android.archives.databinding.FragmentFilesBinding
import com.android.archives.ui.adapter.FileRecyclerAdapter
import com.android.archives.ui.event.FileEvent
import com.android.archives.ui.event.FolderEvent
import com.android.archives.ui.viewmodel.FileViewModel
import com.android.archives.ui.viewmodel.FolderViewModel
import com.android.archives.utils.FileHelper
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class FilesFragment : DialogFragment() {
    private var _binding: FragmentFilesBinding? = null
    private val binding get() = _binding!!

    private val folderViewModel : FolderViewModel by activityViewModels()
    private val fileViewModel: FileViewModel by activityViewModels()

    private lateinit var fileAdapter: FileRecyclerAdapter

    private lateinit var folder: FolderItem


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)

        arguments?.let { arg ->
            arg.getParcelable("folder", FolderItem::class.java)?.let { argFolder ->
                folder = argFolder
            }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up)
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

        setupHeader(folder.name)
        setupSearchView()
        setupRecyclerView()
        setupAddButton()
        setupSwipeToDelete()

        binding.toolbarFile.setNavigationOnClickListener {
            dismiss()
        }

        binding.toolbarFile.setOnMenuItemClickListener { menu ->
            when(menu.itemId) {
                R.id.delete_entity -> {
                    val tintedIcon = AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_delete_24px
                    )?.apply {
                        setTint(ContextCompat.getColor(requireContext(), R.color.error)) // Use your desired color
                    }

                    MaterialAlertDialogBuilder(
                        requireContext(),
                        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
                    )
                        .setTitle("Delete Course Folder")
                        .setMessage("Are you sure you want to delete this folder? This will also delete all the files.")
                        .setIcon(tintedIcon)
                        .setNeutralButton("Cancel") { _, _ -> }
                        .setNegativeButton("Delete") { _, _ ->
                            val fileList = fileViewModel.state.value.fileList
                            fileViewModel.deleteAllFiles(fileList)
                            folderViewModel.onEvent(FolderEvent.DeleteFolder(folder))
                            dismiss()
                        }
                        .show()
                        .apply {
                            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                                ContextCompat.getColor(context, R.color.error)
                            )
                        }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupHeader(name: String) {
        binding.CourseName.text = name
    }

    private fun setupRecyclerView() {
        fileAdapter = FileRecyclerAdapter { file -> openFile(file.filePath) }
        binding.fileRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.fileRecyclerView.adapter = fileAdapter

        binding.fileRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fileAdapter
            itemAnimator = DefaultItemAnimator()
        }

        collectLatestOnViewLifecycle(fileViewModel.state) { state ->
            fileAdapter.submitList(state.fileList)
        }
    }

    private fun setupAddButton() {
        binding.btnAddFile.setOnClickListener {
            filePickerLauncher.launch(arrayOf("*/*"))
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { fileUri ->
            val fileName = FileHelper.getFileName(requireContext(), fileUri)

            val mimeType = requireActivity().contentResolver.getType(fileUri) ?: "*/*"
            val fileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "bin"
            val filePath = "file_${System.currentTimeMillis()}.$fileType"
            val absolutePath = saveFileToInternalStorage(requireContext(), fileUri, filePath)

            val newFile = File(
                fileName = fileName,
                fileType = fileType,
                filePath = absolutePath,
                folderId = folder.folderId
            )

            fileViewModel.onEvent(FileEvent.SaveFile(newFile))
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    fileAdapter.clearFilter()
                } else {
                    fileAdapter.filter.filter(newText)
                }
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

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val fileToDelete = fileAdapter.currentList.getOrNull(position) ?: return

                var fileDeleted = true
                fileViewModel.onEvent(FileEvent.DeleteFile(fileToDelete))

                val snackBar = Snackbar.make(binding.root, "File deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        fileDeleted = false
                        fileViewModel.onEvent(FileEvent.SaveFile(fileToDelete))
                    }

                snackBar.show()

                snackBar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (fileDeleted) {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    FileHelper.deleteFileFromStorage(requireContext(), fileToDelete)
                                }
                            }
                        }
                    }
                })
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.fileRecyclerView)
    }


    private fun openFile(filePath: String) {
        val file = java.io.File(filePath)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val mimeType = requireContext().contentResolver.getType(uri) ?: "*/*"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            startActivity(Intent.createChooser(intent, "Open file with"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFileToInternalStorage(context: Context, uri: Uri, fileName: String): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ""

            val file = java.io.File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}