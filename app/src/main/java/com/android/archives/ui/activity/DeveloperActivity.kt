package com.android.archives.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.archives.R
import com.google.android.material.appbar.MaterialToolbar

class DeveloperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        val toolBar = findViewById<MaterialToolbar>(R.id.developer_toolbar)

        toolBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}