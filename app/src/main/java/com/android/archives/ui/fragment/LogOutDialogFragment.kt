package com.android.archives

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.android.archives.ui.activity.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LogOutDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
        return v
    }
}