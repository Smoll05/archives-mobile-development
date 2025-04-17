package com.android.archives.ui.fragment.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.archives.R
import com.android.archives.databinding.FragmentSettingsBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.DateConverter
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.bumptech.glide.Glide
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
    ) = FragmentSettingsBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        val btnProfileEdit = view.findViewById<Button>(R.id.settings_edit_profile)

        val btnDeveloper = view.findViewById<LinearLayout>(R.id.settings_about)
        val btnReportAProblem = view.findViewById<LinearLayout>(R.id.settings_report_problem)
        val btnRequestAFeature = view.findViewById<LinearLayout>(R.id.settings_request_feature)
        val btnEraseAllContent = view.findViewById<LinearLayout>(R.id.settings_erase)
        val btnDeleteUserAccount = view.findViewById<LinearLayout>(R.id.settings_delete)


        // Boss peeps just ilisi lang ni using shared pref gihapon
        btnEraseAllContent.setOnClickListener {
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

        // Boss peeps just ilisi lang ni using shared pref gihapon
        btnDeleteUserAccount.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account permanently?")
                .setPositiveButton("Delete") { _, _ ->
                    val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()

                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnReportAProblem.setOnClickListener{
            val report = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScCo0HRxxtpJAvBvmgJOSrorQLgMLMrR_iMmYrP3Anw2EegnA/viewform?usp=header"))
            startActivity(report)
        }

        btnRequestAFeature.setOnClickListener{
            val report = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSdi18YdSY-gaGEl0Zm-qIAJDzWgFlAbFRHE9lRsRlQquUKceA/viewform?usp=header"))
            startActivity(report)
        }

        btnDeveloper.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_developerFragment)
        }

        btnProfileEdit.setOnClickListener {
            ProfileFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        btnLogout.setOnClickListener {
            val bottomSheet = LogOutDialogFragment()
            bottomSheet.show(
                parentFragmentManager,
                "ModalBottomSheet"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        Log.d("Profile", "I am settings and is destroyed")
    }

    private fun loadProfile() {
        collectLatestOnViewLifecycle(userViewModel.state) { state ->
            val user = state.currentUser

            if(user != null) {
                binding.profileName.text = user.fullName
                binding.profileBirthday.text = DateConverter.convertMillisToDateString(
                    user.birthday
                )
                binding.profileProgram.text = user.program
                binding.profileSchool.text = user.school

                val imgFile = user.pictureFilePath?.let { File(it) }

                if (imgFile != null) {
                    if (imgFile.exists()) {
                        Glide.with(this)
                            .load(imgFile)
                            .into(binding.profileCardImg)
                    }
                }
            }
        }
    }
}
