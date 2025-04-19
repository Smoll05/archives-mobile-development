package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alamkanak.weekview.WeekView
import com.android.archives.R
import com.android.archives.ui.adapter.ScheduleWeekViewAdapter
import com.android.archives.ui.viewmodel.ScheduleViewModel
import com.android.archives.utils.collectLatestOnViewLifecycle
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    lateinit var adapter: ScheduleWeekViewAdapter
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
    private lateinit var weekView: WeekView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        adapter = ScheduleWeekViewAdapter { schedule ->
            val editScheduleFragment = EditScheduleFragment()

            editScheduleFragment.arguments = Bundle().apply {
                putParcelable("schedule", schedule)
            }

            editScheduleFragment.show(parentFragmentManager, "FullScreenDialog")
        }

        val toolBar = view.findViewById<MaterialToolbar>(R.id.schedule_toolbar)

        weekView = view.findViewById(R.id.weekView)

        weekView.adapter = adapter

        collectLatestOnViewLifecycle(scheduleViewModel.state) { state ->
            if(state.isLoading) {
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
            when(menu.itemId) {
                R.id.add_schedule -> {
                    AddScheduleFragment().show(parentFragmentManager, "FullScreenDialog")
                    true
                }
                else -> false
            }
        }
        return view
    }
}