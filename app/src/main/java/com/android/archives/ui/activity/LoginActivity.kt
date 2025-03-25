package com.android.archives.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.archives.R

class LoginActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val Button_Login = findViewById<Button>(R.id.btnLogin)
        val emailEditText = findViewById<EditText>(R.id.Email)

        Button_Login.setOnClickListener {
            val email: String = emailEditText.getText().toString().trim()

            if (email.isEmpty()) {
                emailEditText.setError("Email is required")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid email format")
            } else {
                Log.e("Logging in", "Logged In")
                Toast.makeText(this, "Logged In!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }
}