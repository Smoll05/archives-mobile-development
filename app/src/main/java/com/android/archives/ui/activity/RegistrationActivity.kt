package com.android.archives.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.archives.R
import com.android.archives.utils.SharedPrefsHelper.UserSession
import com.google.android.material.textfield.TextInputEditText

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val emailEditText = findViewById<TextInputEditText>(R.id.tfEmail)
        val passwordEditText = findViewById<TextInputEditText>(R.id.tfPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.tfConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val btnBack = findViewById<ImageButton>(R.id.btnBACK)

        btnBack.setOnClickListener {
            startActivity(Intent(this, LandingScreen::class.java))
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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
            UserSession.saveUser(this, email, password)
            UserSession.setCurrentUser(this, email)

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
