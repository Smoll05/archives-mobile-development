package com.android.archives.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.android.archives.R
import com.android.archives.ui.activity.AddTaskActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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
            startActivity(
                Intent(requireActivity(), AddTaskActivity::class.java)
            )
        }

        return view
    }
}