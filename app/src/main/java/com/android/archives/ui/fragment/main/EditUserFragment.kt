package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.archives.R
import com.android.archives.databinding.FragmentEditUserBinding
import com.android.archives.ui.event.UserEvent
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.PasswordEncryptor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditUserFragment: DialogFragment() {
    private var _binding: FragmentEditUserBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var dbPassword : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.updateAccountToolbar.setNavigationOnClickListener {
            dismiss()
        }

        userViewModel.loadStateFromCurrentUser()
        loadUserInputs()

        // Disable username input by default
        binding.etNewUsername.isEnabled = false

        // Enable/disable username field based on checkbox
        binding.cbEditUsername.setOnCheckedChangeListener { _, isChecked ->
            binding.etNewUsername.isEnabled = isChecked
        }

        // Show/hide password fields based on checkbox
        binding.cbEditPassword.setOnCheckedChangeListener { _, isChecked ->
            binding.passwordFieldsContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.etNewUsername.addTextChangedListener { input ->
            userViewModel.onEvent(UserEvent.SetUserName(input.toString().trim()))
        }

        binding.etNewPassword.addTextChangedListener { input ->
            val password = input.toString().trim()
            userViewModel.onEvent(UserEvent.SetPassword(password))
            if(password.isNotEmpty()) {
                val strength = getPasswordStrength(password)
                binding.tvPasswordStrength.text = strength.first
                binding.tvPasswordStrength.setTextColor(strength.second)
            } else {
                binding.tvPasswordStrength.text = null
                binding.tilNewPassword.error = null
            }
        }

        binding.etOldPassword.addTextChangedListener { input ->
            val confirmPassword = input.toString().trim()
            if(confirmPassword.isEmpty()) {
                binding.tilOldPassword.error = null
            }
        }

        // Save button logic
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                val isUsernameEditing = binding.cbEditUsername.isChecked
                val isPasswordEditing = binding.cbEditPassword.isChecked

                val newUsername = binding.etNewUsername.text.toString().trim()
                val oldPassword = binding.etOldPassword.text.toString().trim()
                val newPassword = binding.etNewPassword.text.toString().trim()

                if (userViewModel.userExists(newUsername)) {
                    Toast.makeText(requireContext(), "Username already taken", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Nothing selected to update
                if (!isUsernameEditing && !isPasswordEditing) {
                    Toast.makeText(requireContext(), "Nothing to update.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Validate password update
                if (isPasswordEditing) {
                    if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                        Toast.makeText(requireContext(), "Old and New Password are required", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val strength = getPasswordStrength(newPassword)

                    binding.tilNewPassword.error = null
                    binding.tilOldPassword.error = null

                    if (strength.first == "Weak Password") {
                        binding.tilNewPassword.error = " "
                        binding.tilNewPassword.errorIconDrawable = null
                        Toast.makeText(requireContext(), "Create a much stronger password", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (PasswordEncryptor.hashPassword(oldPassword) != dbPassword) {
                        binding.tilOldPassword.error = "Old password is incorrect"
                        binding.tilOldPassword.errorIconDrawable = null
                        return@launch
                    }

                    userViewModel.onEvent(UserEvent.SetPassword(newPassword))
                }

                // Apply username update if selected
                if (isUsernameEditing) {
                    if (newUsername.isEmpty()) {
                        Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    userViewModel.onEvent(UserEvent.SetUserName(newUsername))
                }

                // Proceed with update
                lifecycleScope.launch {
                    if (userViewModel.updateUser()) {
                        Toast.makeText(requireContext(), "User info updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to update user info",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dismiss()
                }
            }
        }
    }

    // Function to determine password strength (Weak, Medium, Strong)
    private fun getPasswordStrength(password: String): Pair<String, Int> {
        val length = password.length
        if (length < 6) return Pair("Weak Password", android.graphics.Color.RED)
        val strongRegex = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}")
        if (strongRegex.matches(password)) return Pair("Strong Password", android.graphics.Color.GREEN)
        val mediumRegex = Regex("(?=.*[A-Za-z])(?=.*\\d).{6,}")
        if (mediumRegex.matches(password)) return Pair("Moderate Password", android.graphics.Color.parseColor("#FFA500")) // Orange
        return Pair("Weak Password", android.graphics.Color.RED)
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(
            R.style.dialog_animation_enter_up);
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUserInputs() {
        viewLifecycleOwner.lifecycleScope.launch {
            val state = userViewModel.state.first()
            dbPassword = state.password
            binding.etNewUsername.setText(state.username)
        }
    }
}
