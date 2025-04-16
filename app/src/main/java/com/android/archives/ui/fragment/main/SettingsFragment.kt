package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        val btnProfileEdit = view.findViewById<Button>(R.id.settings_edit_profile)

        val btnDeveloper = view.findViewById<LinearLayout>(R.id.settings_about)

        btnLogout.setOnClickListener {
            val bottomSheet = LogOutDialogFragment()
            bottomSheet.show(
                parentFragmentManager,
                "ModalBottomSheet"
            )
        }

        btnDeveloper.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_developerFragment)
        }

        btnProfileEdit.setOnClickListener {
            ProfileFragment().show(parentFragmentManager, "FullScreenDialog")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        Log.d("Profile", "I am settings and is destroyed")
    }
}