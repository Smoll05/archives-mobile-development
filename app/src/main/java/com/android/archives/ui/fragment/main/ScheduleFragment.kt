package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alamkanak.weekview.WeekView
import com.android.archives.R
import com.android.archives.databinding.FragmentScheduleBinding
import com.android.archives.ui.adapter.ScheduleWeekViewAdapter
import com.android.archives.ui.viewmodel.ScheduleViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    lateinit var adapter: ScheduleWeekViewAdapter
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
    private lateinit var weekView: WeekView
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private var lastClickTime: Long = 0
    private val clickInterval: Long = 3000 // 1 second

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = ScheduleWeekViewAdapter { schedule ->
            if (isDoubleClick()) return@ScheduleWeekViewAdapter
            val editScheduleFragment = EditScheduleFragment()
            editScheduleFragment.arguments = Bundle().apply {
                putParcelable("schedule", schedule)
            }
            editScheduleFragment.show(parentFragmentManager, "FullScreenDialog")
        }

        val toolBar = binding.scheduleToolbar
        weekView = binding.weekView
        weekView.adapter = adapter

        collectLatestOnViewLifecycle(scheduleViewModel.state) { state ->
            if (state.isLoading) {
                weekView.isEnabled = false
                Log.d("Schedule", "Not Getting Schedules")
                return@collectLatestOnViewLifecycle
            } else {
                weekView.isEnabled = true
            }
            Log.d("Schedule", "Updated")
            adapter.submitList(state.scheduleList)
        }

        toolBar.setOnMenuItemClickListener { menu ->
            if (isDoubleClick()) return@setOnMenuItemClickListener true
            when (menu.itemId) {
                R.id.add_schedule -> {
                    AddScheduleFragment().show(parentFragmentManager, "FullScreenDialog")
                    true
                }
                else -> false
            }
        }

        return view
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
