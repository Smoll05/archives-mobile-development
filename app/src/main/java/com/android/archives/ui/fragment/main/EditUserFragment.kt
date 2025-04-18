package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.android.archives.databinding.FragmentEditUserBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.PasswordEncryptor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditUserFragment : DialogFragment() {
    private var _binding: FragmentEditUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

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
        binding.btnBack.setOnClickListener { dismiss() }

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

        // Show current username
        val currentUser = viewModel.state.value
        lifecycleScope.launch {
            currentUser?.let {
                val userFromDb = viewModel.getUserById(it.userId).first()
                binding.etNewUsername.setText(userFromDb.username)
            }
        }

        // Listen to new password input and check strength
        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            // This function runs when user types in new password
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                val strength = getPasswordStrength(password)
                binding.tvPasswordStrength.text = strength.first
                binding.tvPasswordStrength.setTextColor(strength.second)
            }
        })

        // Save button logic
        binding.btnSave.setOnClickListener {
            val isUsernameEditing = binding.cbEditUsername.isChecked
            val isPasswordEditing = binding.cbEditPassword.isChecked

            val newUsername = binding.etNewUsername.text.toString().trim()
            val oldPassword = binding.etOldPassword.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()

            if (!isUsernameEditing && !isPasswordEditing) {
                Toast.makeText(requireContext(), "Nothing to update.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isPasswordEditing && (oldPassword.isEmpty() || newPassword.isEmpty())) {
                Toast.makeText(requireContext(), "Old and New Password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                currentUser?.let {
                    val userFromDb = viewModel.getUserById(it.userId).first()
                    var finalUsername = userFromDb.username
                    var finalPassword = userFromDb.password

                    if (isUsernameEditing) {
                        finalUsername = newUsername
                    }

                    if (isPasswordEditing) {
                        val hashedOldPassword = PasswordEncryptor.hashPassword(oldPassword)
                        if (userFromDb.password != hashedOldPassword) {
                            Toast.makeText(requireContext(), "Old password is incorrect.", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        finalPassword = PasswordEncryptor.hashPassword(newPassword)
                    }

                    val updatedUser = userFromDb.copy(
                        username = finalUsername,
                        password = finalPassword
                    )
                    viewModel.saveUser(updatedUser)
                    Toast.makeText(requireContext(), "User info updated.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }

    // Function to determine password strength (Weak, Medium, Strong)
    private fun getPasswordStrength(password: String): Pair<String, Int> {
        val length = password.length
        if (length < 6) return Pair("Weak", android.graphics.Color.RED)
        val strongRegex = Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}")
        if (strongRegex.matches(password)) return Pair("Strong", android.graphics.Color.GREEN)
        val mediumRegex = Regex("(?=.*[A-Za-z])(?=.*\\d).{6,}")
        if (mediumRegex.matches(password)) return Pair("Medium", android.graphics.Color.parseColor("#FFA500")) // Orange
        return Pair("Weak", android.graphics.Color.RED)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
