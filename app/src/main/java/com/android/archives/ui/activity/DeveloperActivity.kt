package com.android.archives.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.archives.R
import android.content.Intent
import android.widget.ImageButton
import com.android.archives.ui.fragment.SettingsFragment

class DeveloperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        val btnBack = findViewById<ImageButton>(R.id.developer_page_back)

        btnBack.setOnClickListener {
            finish()
        }
    }
}