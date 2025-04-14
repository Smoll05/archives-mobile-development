package com.android.archives.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.archives.R
import com.android.archives.data.event.UserEvent
import com.android.archives.databinding.ActivityMainBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var onEvent: (UserEvent) -> Unit
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val bottomNav: NavigationBarView = findViewById(R.id.bottom_navigation)
        binding.bottomNavigation.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
//        bottomNav.setOnItemSelectedListener(navListener)

//        supportFragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment()).commit()

        onEvent = userViewModel::onEvent
        onEvent(UserEvent.LoadUser)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

//    private val navListener = NavigationBarView.OnItemSelectedListener { item ->
//        lateinit var selectedFragment: Fragment
//        when(item.itemId) {
//            R.id.navigation_home-> {
//                selectedFragment = HomeFragment()
//            }
//
//            R.id.navigation_course -> {
//                selectedFragment = MainCourseFragment()
//            }
//
//            R.id.navigation_schedule -> {
//                selectedFragment = ScheduleFragment()
//            }
//
//            R.id.navigation_settings -> {
//                selectedFragment = SettingsFragment()
//            }
//        }
//
//        supportFragmentManager.beginTransaction().replace(R.id.content_frame, selectedFragment).commit()
//        true
//    }
}