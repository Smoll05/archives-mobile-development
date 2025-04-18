package com.android.archives.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.archives.R
import com.android.archives.databinding.FragmentScheduleBinding
import com.android.archives.ui.adapter.ScheduleWeekViewAdapter
import com.android.archives.ui.viewmodel.ScheduleViewModel
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment(), ScheduleWeekViewAdapter.OnEditScheduleClickListener {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val toolBar = view.findViewById<MaterialToolbar>(R.id.schedule_toolbar)

        // weekView = view.findViewById(R.id.weekView)
        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        val adapter = ScheduleWeekViewAdapter(viewModel, viewLifecycleOwner, this)
        binding.weekView.adapter = adapter

        binding.scheduleToolbar.setOnMenuItemClickListener { menu ->
            when(menu.itemId) {
                R.id.add_schedule -> {
                    // startActivity(
                    //     Intent(requireActivity(), AddScheduleActivity::class.java)
                    // )
                    AddScheduleFragment().show(parentFragmentManager, "FullScreenDialog")
                    true
                }
                else -> false
            }
        }
    }


    override fun onEditScheduleClick() {
        val dialog = EditScheduleFragment()
        dialog.show(parentFragmentManager, "FullScreenDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
