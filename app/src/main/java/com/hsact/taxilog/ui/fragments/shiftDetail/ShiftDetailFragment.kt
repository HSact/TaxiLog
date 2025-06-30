package com.hsact.taxilog.ui.fragments.shiftDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShiftDetailFragment : Fragment() {
    private val viewModel: ShiftDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_shift_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val shiftId = arguments?.getInt("shiftId") ?: -1
        val visibleId = arguments?.getInt("visibleId") ?: -1
        if (shiftId != -1) {
            viewModel.loadShift(shiftId)
        }

        val container = view.findViewById<FrameLayout>(R.id.compose_container)
        val composeView = ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    val state by viewModel.shift.collectAsState()
                    ShiftDetailScreen(state)
                }
            }
        }
        container.addView(composeView)
    }
}