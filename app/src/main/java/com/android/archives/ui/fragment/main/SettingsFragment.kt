package com.android.archives.ui.fragment.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.databinding.FragmentSettingsBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

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
            val editUserFragment = EditUserFragment()
            editUserFragment.show(parentFragmentManager, "EditUserDialog")
        }

        binding.settingsErase.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Erase All Content")
                .setMessage("Are you sure you want to erase all app data? This action cannot be undone.")
                .setPositiveButton("Yes") { _, _ ->
                    val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.settingsDelete.setOnClickListener {
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
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setIcon(tintedIcon)
                .setNeutralButton("Cancel") { _, _ -> }
                .setNegativeButton("Delete") { _, _ ->

                }
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }
        }

        binding.settingsReportProblem.setOnClickListener {
            val report = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScCo0HRxxtpJAvBvmgJOSrorQLgMLMrR_iMmYrP3Anw2EegnA/viewform?usp=header"))
            startActivity(report)
        }

        binding.settingsRequestFeature.setOnClickListener {
            val report = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSdi18YdSY-gaGEl0Zm-qIAJDzWgFlAbFRHE9lRsRlQquUKceA/viewform?usp=header"))
            startActivity(report)
        }

        binding.settingsAbout.setOnClickListener {
            DeveloperFragment().show(parentFragmentManager, "FullScreenDialog")
//            findNavController().navigate(R.id.action_settingsFragment_to_developerFragment)
        }

        binding.settingsEditProfile.setOnClickListener {
            ProfileFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        binding.btnLogout.setOnClickListener {
            val bottomSheet = LogOutDialogFragment()
            bottomSheet.show(parentFragmentManager, "ModalBottomSheet")
        }
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
