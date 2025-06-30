package com.hsact.taxilog.ui.fragments.shiftDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShiftDetailFragment : Fragment() {
    private val viewModel: ShiftDetailViewModel by viewModels()

    private var shiftId: Int = -1
    private var visibleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.botNav.isVisible = false
        arguments?.let {
            shiftId = it.getInt("shiftId", -1)
            visibleId = it.getInt("visibleId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_shift_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (shiftId != -1) {
            viewModel.loadShift(shiftId)
        }

        val container = view.findViewById<FrameLayout>(R.id.compose_container)
        val composeView = ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.shift.collectAsState()
                ShiftDetailScreen(state)
            }
        }
        container.addView(composeView)
    }

    override fun onResume() {
        super.onResume()
        MainActivity.botNav.isVisible = false
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title =
            getString(R.string.title_shift_detail, visibleId)
    }
}