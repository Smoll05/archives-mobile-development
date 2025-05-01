package com.android.archives.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.archives.data.service.SharedPrefsService
import com.android.archives.databinding.FragmentLogOutDialogBinding
import com.android.archives.ui.activity.AuthActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogOutDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentLogOutDialogBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPrefs: SharedPrefsService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Properly initialize the binding
        _binding = FragmentLogOutDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        val yesBtn = binding.btnYesLogout
        val cancelBtn = binding.btnCancel

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

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks by clearing the binding reference
    }
}
