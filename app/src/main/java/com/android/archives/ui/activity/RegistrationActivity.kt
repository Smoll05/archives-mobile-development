package com.android.archives.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.archives.R
import com.android.archives.data.db.ArchivesDatabase
import com.android.archives.data.event.UserEvent
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.PasswordEncryptor
import com.android.archives.utils.SharedPrefsHelper.UserSession
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var onEvent: (UserEvent) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val userDao = ArchivesDatabase(this).userDao
        val viewModelProviderFactory = UserViewModel.UserViewModelProviderFactory(userDao)


        onEvent = userViewModel::onEvent
        onEvent(UserEvent.ShowForm)

        val toolBar = findViewById<MaterialToolbar>(R.id.registration_toolbar)

        val emailEditText = findViewById<TextInputEditText>(R.id.tfEmail)
        val passwordEditText = findViewById<TextInputEditText>(R.id.tfPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.tfConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        toolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        registerButton.setOnClickListener {
            val username = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordEditText.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPasswordEditText.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Save user data and clear all previous session data
            UserSession.clearAllUserData(this)
            UserSession.saveUser(this, username, password)

            onEvent(UserEvent.SetUserName(username))
            onEvent(UserEvent.SetPassword(
                PasswordEncryptor.hashPassword(password)
            ))

            startActivity(Intent(this, OnboardingActivity::class.java))
        }
    }
}
