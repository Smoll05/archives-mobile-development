package com.android.archives.ui.fragment.auth

import android.content.Intent
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
import com.android.archives.databinding.FragmentLoginBinding
import com.android.archives.ui.activity.MainActivity
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.getContent
import com.android.archives.utils.isFieldEmptyOrNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.etUsername.addTextChangedListener {
            binding.tilUsername.error = null
        }

        binding.etPassword.addTextChangedListener {
            binding.tilPassword.error = null
        }

        binding.btnLogin.setOnClickListener {
            if (areFieldsEmpty()) return@setOnClickListener

            lifecycleScope.launch {
                val success = userViewModel.getUserWithUsernameAndPassword(
                    binding.etUsername.getContent(),
                    binding.etPassword.getContent()
                )

                if (success) {
                    val intent = Intent(requireContext(), MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Invalid user credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun areFieldsEmpty(): Boolean {
        var isEmpty = false
        val errorMsg = "This Field Is Required"

        if (binding.etUsername.isFieldEmptyOrNull()) {
            binding.tilUsername.error = errorMsg
            isEmpty = true
        }

        if (binding.etPassword.isFieldEmptyOrNull()) {
            binding.tilPassword.error = errorMsg
            isEmpty = true
        }

        return isEmpty
    }
}
