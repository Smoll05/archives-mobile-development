package com.android.archives.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.archives.R
import com.android.archives.ui.fragment.SettingsFragment

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileReturnBtn = findViewById<ImageButton>(R.id.profile_page_back)
        val saveProfileBtn = findViewById<Button>(R.id.save_profile_btn)

        profileReturnBtn.setOnClickListener {
            finish()
//            startActivity(Intent(this, SettingsFragment::class.java))
        }

        saveProfileBtn.setOnClickListener {
            finish()
//            startActivity(Intent(this, SettingsFragment::class.java))
        }
    }
}