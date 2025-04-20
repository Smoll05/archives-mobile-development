package com.android.archives.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.archives.data.db.ArchivesDatabase
import com.android.archives.utils.SharedPrefsHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartupActivity: AppCompatActivity() {
    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var database: ArchivesDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = if (sharedPrefsHelper.isUserLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}