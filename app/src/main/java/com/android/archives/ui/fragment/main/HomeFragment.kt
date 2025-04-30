package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.databinding.FragmentHomeBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    private var lastClickTime: Long = 0
    private val clickInterval: Long = 3000 // 3 second

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = binding.homeToolbar
        val tabLayout = binding.homeTablayout
        val btnAdd = binding.homeAddTask

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        toolBar.subtitle = formattedDate

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedFragment: Fragment = when (tab?.position) {
                    0 -> TaskTodoFragment()
                    1 -> TaskCompleteFragment()
                    else -> TaskTodoFragment()
                }
                childFragmentManager.beginTransaction()
                    .replace(R.id.task_frame, selectedFragment)
                    .commit()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        childFragmentManager.beginTransaction().replace(R.id.task_frame, TaskTodoFragment()).commit()

        btnAdd.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            AddTaskFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        collectLatestOnViewLifecycle(userViewModel.state) { state ->
            val user = state.currentUser
            if (user != null) {
                toolBar.title = "Hello, ${user.fullName}"
            }
        }
    }

    private fun isDoubleClick(): Boolean {
        val currentClickTime = SystemClock.elapsedRealtime()
        return if (currentClickTime - lastClickTime < clickInterval) {
            true
        } else {
            lastClickTime = currentClickTime
            false
        }
    }

    override fun onResume() {
        super.onResume()
        lastClickTime = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
