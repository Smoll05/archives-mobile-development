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
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import com.android.archives.R
import com.android.archives.databinding.FragmentSettingsBinding
import com.android.archives.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogout = binding.btnLogout
        val btnProfileEdit = binding.settingsEditProfile

        val btnDeveloper = binding.settingsAbout
        val btnReportAProblem = binding.settingsReportProblem
        val btnRequestAFeature = binding.settingsRequestFeature
        val btnEraseAllContent = binding.settingsErase
        val btnDeleteUserAccount = binding.settingsDelete

        val btnEditUserFragment = binding.settingsEditUser



        btnEditUserFragment.setOnClickListener {
            val editUserFragment = EditUserFragment()
            editUserFragment.show(parentFragmentManager, "EditUserDialog")
        }

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
}
