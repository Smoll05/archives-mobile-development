package com.android.archives.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.archives.R
import com.android.archives.data.event.UserEvent
import com.android.archives.databinding.ActivityMainBinding
import com.android.archives.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var onEvent: (UserEvent) -> Unit
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val bottomNav: NavigationBarView = findViewById(R.id.bottom_navigation)
//        binding.bottomNavigation.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
//        bottomNav.setOnItemSelectedListener(navListener)

//        supportFragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment()).commit()

        onEvent = userViewModel::onEvent
        onEvent(UserEvent.LoadUser)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

//        navHostFragment.findNavController()
//            .addOnDestinationChangedListener { _, destination, _ ->
//                when(destination.id) {
//                    R.id.settingsFragment, R.id.homeFragment,
//                    R.id.scheduleFragment, R.id.mainCourseFragment ->
//                        binding.bottomNavigation.visibility = View.VISIBLE
//                    else -> binding.bottomNavigation.visibility = View.GONE
//                }
//            }

//        navHostFragment.findNavController()
//            .addOnDestinationChangedListener { _, destination, _ ->
//                binding.root.doOnPreDraw {
//                    when (destination.id) {
//                        R.id.settingsFragment, R.id.homeFragment,
//                        R.id.scheduleFragment, R.id.mainCourseFragment -> {
//                            binding.bottomNavigation.visibility = View.VISIBLE
//                        }
//                        else -> {
//                            binding.bottomNavigation.visibility = View.GONE
//                        }
//                    }
//                }
//            }

//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
//
//        val navController = navHostFragment.navController
//
//        binding.bottomNavigationView.setupWithNavController(navController)
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

    fun setBottomNavigationVisibility(visibility: Int) {
        val bottomNav = binding.bottomNavigation
        if(visibility == View.VISIBLE) {
            bottomNav.visibility = View.VISIBLE
//            bottomNav.animate()
//                .translationY(0f)
//                .setDuration(200)
//                .start()
        } else {
            bottomNav.visibility = View.GONE
//            bottomNav.animate()
//                .translationY(bottomNav.height.toFloat())
//                .setDuration(200)
//                .start()
        }

    }
}