package com.android.archives.ui.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.archives.R
import com.android.archives.databinding.FragmentRegisterBinding
import com.android.archives.ui.event.UserEvent
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.PasswordEncryptor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var onEvent: (UserEvent) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRegisterBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onEvent = userViewModel::onEvent
        onEvent(UserEvent.ShowForm)

        binding.tfEmail.addTextChangedListener {
            onEvent(
                UserEvent.SetUserName(
                it.toString()
            ))
        }

        binding.registrationToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            onEvent(UserEvent.HideForm)
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.tfEmail.text.toString().trim()
            val password = binding.tfPassword.text.toString().trim()
            val confirmPassword = binding.tfConfirmPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.tilPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            onEvent(
                UserEvent.SetPassword(
                PasswordEncryptor.hashPassword(password)
            ))

            findNavController().navigate(R.id.action_registerFragment_to_onboardingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}