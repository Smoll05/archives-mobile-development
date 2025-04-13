package com.android.archives.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.android.archives.LogOutDialogFragment
import com.android.archives.R
import com.android.archives.ui.activity.DeveloperActivity
import com.android.archives.ui.activity.ProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnLogout = view.findViewById<Button>(R.id.settings_logout)
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

        btnLogout.setOnClickListener {
            val bottomSheet = LogOutDialogFragment()
            bottomSheet.show(
                parentFragmentManager,
                "ModalBottomSheet"
            )
        }



        btnDeveloper.setOnClickListener {
            activityLauncher.launch(Intent(requireContext(), DeveloperActivity::class.java))
        }

        btnProfileEdit.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        return view
    }
}