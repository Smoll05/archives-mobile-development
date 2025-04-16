package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alamkanak.weekview.WeekView
import com.android.archives.R
import com.android.archives.ui.adapter.ScheduleWeekViewAdapter
import com.android.archives.ui.viewmodel.ScheduleViewModel
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment(), ScheduleWeekViewAdapter.OnEditScheduleClickListener {
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var weekView: WeekView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        val toolBar = view.findViewById<MaterialToolbar>(R.id.schedule_toolbar)

        weekView = view.findViewById(R.id.weekView)
        viewModel = ViewModelProvider(this,)[ScheduleViewModel::class.java]

        val adapter = ScheduleWeekViewAdapter(viewModel, viewLifecycleOwner, this)
        weekView.adapter = adapter

        toolBar.setOnMenuItemClickListener { menu ->
            when(menu.itemId) {
                R.id.add_schedule -> {
//                    startActivity(
//                        Intent(requireActivity(), AddScheduleActivity::class.java)
//                    )
                    AddScheduleFragment().show(parentFragmentManager, "FullScreenDialog")
                    true
                }
                else -> false
            }
        }
        return view
    }

    override fun onEditScheduleClick() {
        val dialog = EditScheduleFragment()
        dialog.show(parentFragmentManager, "FullScreenDialog")
    }
}