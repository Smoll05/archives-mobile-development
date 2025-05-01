package com.android.archives.ui.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.archives.R
import com.android.archives.databinding.FragmentRegisterBinding
import com.android.archives.ui.event.UserEvent
import com.android.archives.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            onEvent(UserEvent.SetUserName(it.toString().trim()))
        }

        binding.registrationToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            onEvent(UserEvent.HideForm)
        }

        binding.tfPassword.addTextChangedListener { input ->
            val password = input.toString().trim()
            userViewModel.onEvent(UserEvent.SetPassword(password))
            if(password.isNotEmpty()) {
                val strength = getPasswordStrength(password)
                binding.tvPasswordStrength.text = strength.first
                binding.tvPasswordStrength.setTextColor(strength.second)
            } else {
                binding.tvPasswordStrength.text = null
                binding.tilPassword.error = null
            }
        }

        binding.tfConfirmPassword.addTextChangedListener { input ->
            val confirmPassword = input.toString().trim()
            if(confirmPassword.isEmpty()) {
                binding.tilPassword.error = null
            }
        }

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                val username = binding.tfEmail.text.toString().trim()
                val password = binding.tfPassword.text.toString().trim()
                val confirmPassword = binding.tfConfirmPassword.text.toString().trim()

                if (userViewModel.userExists(username)) {
                    Toast.makeText(requireContext(), "Username already taken", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val strength = getPasswordStrength(password)
                binding.tilPassword.error = null
                binding.tilConfirmPassword.error = null

                if (strength.first == "Weak Password") {
                    binding.tilPassword.error = " "
                    binding.tilPassword.errorIconDrawable = null
                    Toast.makeText(requireContext(), "Create a much stronger password", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (password != confirmPassword) {
                    binding.tilConfirmPassword.error = "Passwords do not match"
                    binding.tilPassword.errorIconDrawable = null
                    return@launch
                }

                findNavController().navigate(R.id.action_registerFragment_to_onboardingFragment)
            }
        }
    }

    private fun getPasswordStrength(password: String): Pair<String, Int> {
        val length = password.length
        if (length < 6) return Pair("Weak Password", android.graphics.Color.RED)
        val strongRegex = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}")
        if (strongRegex.matches(password)) return Pair("Strong Password", android.graphics.Color.GREEN)
        val mediumRegex = Regex("(?=.*[A-Za-z])(?=.*\\d).{6,}")
        if (mediumRegex.matches(password)) return Pair("Moderate Password", android.graphics.Color.parseColor("#FFA500")) // Orange
        return Pair("Weak Password", android.graphics.Color.RED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}