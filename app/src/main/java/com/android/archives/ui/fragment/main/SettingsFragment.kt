package com.android.archives.ui.fragment.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.data.service.SharedPrefsService
import com.android.archives.databinding.FragmentSettingsBinding
import com.android.archives.ui.activity.AuthActivity
import com.android.archives.ui.event.ScheduleEvent
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.event.UserEvent
import com.android.archives.ui.viewmodel.FolderViewModel
import com.android.archives.ui.viewmodel.ScheduleViewModel
import com.android.archives.ui.viewmodel.TaskViewModel
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment() {
    @Inject lateinit var sharedPrefs: SharedPrefsService

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private val folderViewModel: FolderViewModel by activityViewModels()
    private val taskViewModel: TaskViewModel by activityViewModels()
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()

    private var lastClickTime: Long = 0
    private val clickInterval: Long = 1000 // 1 second

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()

        binding.settingsEditUser.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            EditUserFragment().show(parentFragmentManager, "EditUserDialog")
        }

        binding.settingsErase.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            val tintedIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_delete_24px
            )?.apply {
                setTint(ContextCompat.getColor(requireContext(), R.color.error))
            }

            MaterialAlertDialogBuilder(
                requireContext(),
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle("Delete All Content")
                .setMessage("Are you sure you want to erase all app data? This action cannot be undone.")
                .setIcon(tintedIcon)
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Delete") { _, _ ->
                    taskViewModel.onEvent(TaskEvent.DeleteAllTask)
                    scheduleViewModel.onEvent(ScheduleEvent.DeleteAllSchedule)
                    folderViewModel.deleteAllFilesFromFolders()
                    Toast.makeText(requireContext(), "All content and data deleted", Toast.LENGTH_SHORT).show()
                }
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }
        }

        binding.settingsDelete.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener

            val tintedIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_person_remove_24px
            )?.apply {
                setTint(ContextCompat.getColor(requireContext(), R.color.error))
            }

            MaterialAlertDialogBuilder(
                requireContext(),
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle("Delete User Account")
                .setMessage("Are you sure you want to delete this user account?")
                .setIcon(tintedIcon)
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Delete") { _, _ ->
                    folderViewModel.deleteAllFilesFromFolders()
                    userViewModel.onEvent(UserEvent.DeleteUser)
                    sharedPrefs.clearCurrentUser()

                    val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }
        }

        binding.settingsReportProblem.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            val report = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScCo0HRxxtpJAvBvmgJOSrorQLgMLMrR_iMmYrP3Anw2EegnA/viewform?usp=header"))
            startActivity(report)
        }

        binding.settingsRequestFeature.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            val report = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSdi18YdSY-gaGEl0Zm-qIAJDzWgFlAbFRHE9lRsRlQquUKceA/viewform?usp=header"))
            startActivity(report)
        }

        binding.settingsAbout.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            DeveloperFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        binding.settingsEditProfile.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            ProfileFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        binding.btnLogout.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            val bottomSheet = LogOutDialogFragment()
            bottomSheet.show(parentFragmentManager, "ModalBottomSheet")
        }
    }

    private fun isDoubleClick(): Boolean {
        val currentClickTime = SystemClock.elapsedRealtime()
        return if (currentClickTime - lastClickTime < clickInterval) {
            true
        } else {
            lastClickTime = currentClickTime
            false
        }
    }

    override fun onResume() {
        super.onResume()
        lastClickTime = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadProfile() {
        collectLatestOnViewLifecycle(userViewModel.state) { state ->
            val user = state.currentUser

            if (user != null) {
                binding.profileName.text = user.fullName
                binding.profileBirthday.text = DateConverter.convertMillisToDateString(user.birthday)
                binding.profileProgram.text = user.program
                binding.profileSchool.text = user.school

                val imgFile = user.pictureFilePath?.let { File(it) }
                imgFile?.takeIf { it.exists() }?.let {
                    Glide.with(this)
                        .load(it)
                        .into(binding.profileCardImg)
                }
            }
        }
    }
}
