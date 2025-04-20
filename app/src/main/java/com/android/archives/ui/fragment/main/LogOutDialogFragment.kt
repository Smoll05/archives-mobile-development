package com.android.archives.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.android.archives.R
import com.android.archives.ui.activity.AuthActivity
import com.android.archives.utils.SharedPrefsHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogOutDialogFragment : BottomSheetDialogFragment() {
    @Inject
    lateinit var sharedPrefs : SharedPrefsHelper

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(
            R.layout.fragment_log_out_dialog,
            container, false
        )

        val yesBtn = v.findViewById<Button>(R.id.btnYesLogout)
        val cancelBtn = v.findViewById<Button>(R.id.btnCancel)

        yesBtn.setOnClickListener {
            Toast.makeText(
                activity,
                "Logging Out", Toast.LENGTH_SHORT
            ).show()
            dismiss()

            sharedPrefs.clearCurrentUser()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
        return v
    }
}