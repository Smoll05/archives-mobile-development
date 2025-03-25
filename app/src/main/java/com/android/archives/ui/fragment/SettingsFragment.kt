package com.android.archives.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Handle any result here if needed
        }

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val openBottomSheet = view.findViewById<Button>(R.id.btnYes)
        val developersBtn = view.findViewById<Button>(R.id.btnDeveloper)
        val editProfileBtn = view.findViewById<Button>(R.id.btnEditProfile)

        openBottomSheet.setOnClickListener {
            val bottomSheet = LogOutDialogFragment()
            bottomSheet.show(
                parentFragmentManager,
                "ModalBottomSheet"
            )
        }

        developersBtn.setOnClickListener {
            activityLauncher.launch(Intent(requireContext(), DeveloperActivity::class.java))
        }

        editProfileBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        return view
    }
}