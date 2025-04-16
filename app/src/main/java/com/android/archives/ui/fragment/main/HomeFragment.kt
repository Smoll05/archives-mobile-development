package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.R
import com.android.archives.databinding.FragmentHomeBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.google.android.material.appbar.MaterialToolbar
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = view.findViewById<MaterialToolbar>(R.id.home_toolbar)
        val tabLayout = view.findViewById<TabLayout>(R.id.home_tablayout)
        val btnAdd = view.findViewById<Button>(R.id.home_add_task)

        val currentDate = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        toolBar.subtitle = formattedDate

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                lateinit var selectedFragment : Fragment
                when (tab?.position) {
                    0 -> {
                        selectedFragment = TaskTodoFragment()
                    }
                    1 -> {
                        selectedFragment = TaskCompleteFragment()
                    }
                }

                childFragmentManager.beginTransaction().replace(R.id.task_frame, selectedFragment).commit()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) { }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }
        })

        childFragmentManager.beginTransaction().replace(R.id.task_frame, TaskTodoFragment()).commit()

        btnAdd.setOnClickListener {
            AddTaskFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        collectLatestOnViewLifecycle(userViewModel.state) { state ->
            toolBar.title = "Hello, ${state.fullName}"
        }
    }
}