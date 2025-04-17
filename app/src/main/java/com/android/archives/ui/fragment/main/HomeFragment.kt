package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.archives.databinding.FragmentHomeBinding
import com.android.archives.ui.viewmodel.UserViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
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
    ) = FragmentHomeBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        binding.homeToolbar.subtitle = formattedDate

        binding.homeTablayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                val selectedFragment = when (tab?.position) {
                    0 -> TaskTodoFragment()
                    1 -> TaskCompleteFragment()
                    else -> return
                }
                childFragmentManager.beginTransaction().replace(binding.taskFrame.id, selectedFragment).commit()
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

        childFragmentManager.beginTransaction().replace(binding.taskFrame.id, TaskTodoFragment()).commit()

        binding.homeAddTask.setOnClickListener {
            AddTaskFragment().show(parentFragmentManager, "FullScreenDialog")
        }

        collectLatestOnViewLifecycle(userViewModel.state) { state ->
            binding.homeToolbar.title = "Hello, ${state.fullName}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
