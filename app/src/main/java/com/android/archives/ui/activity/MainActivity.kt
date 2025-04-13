package com.android.archives.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.archives.R
import com.android.archives.data.db.ArchivesDatabase
import com.android.archives.data.event.UserEvent
import com.android.archives.ui.fragment.HomeFragment
import com.android.archives.ui.fragment.MainCourseFragment
import com.android.archives.ui.fragment.ScheduleFragment
import com.android.archives.ui.fragment.SettingsFragment
import com.android.archives.ui.viewmodel.UserViewModel
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    lateinit var userViewModel: UserViewModel
    private lateinit var onEvent: (UserEvent) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav: NavigationBarView = findViewById(R.id.bottom_navigation)
        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNav.setOnItemSelectedListener(navListener)

        supportFragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment()).commit()

        val userDao = ArchivesDatabase(this).userDao
        val viewModelProviderFactory = UserViewModel.UserViewModelProviderFactory(userDao)

        userViewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]
        onEvent = userViewModel::onEvent
        onEvent(UserEvent.ShowForm)
    }

    private val navListener = NavigationBarView.OnItemSelectedListener { item ->
        lateinit var selectedFragment: Fragment
        when(item.itemId) {
            R.id.navigation_home-> {
                selectedFragment = HomeFragment()
            }

            R.id.navigation_course -> {
                selectedFragment = MainCourseFragment()
            }

            R.id.navigation_schedule -> {
                selectedFragment = ScheduleFragment()
            }

            R.id.navigation_settings -> {
                selectedFragment = SettingsFragment()
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.content_frame, selectedFragment).commit()
        true
    }
}