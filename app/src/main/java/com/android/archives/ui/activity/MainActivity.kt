package com.android.archives.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.archives.R
import com.android.archives.databinding.ActivityMainBinding
import com.android.archives.ui.event.UserEvent
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.collectLatestOnLifecycle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.onEvent(UserEvent.LoadUser)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        preloadProfileImage()
    }

    private fun preloadProfileImage() {
        collectLatestOnLifecycle(userViewModel.state) { state ->
            val profileImage = state.pictureFilePath?.let { File(filesDir, it) }

            Glide.with(this)
                .load(profileImage)
                .preload()
        }
    }
}