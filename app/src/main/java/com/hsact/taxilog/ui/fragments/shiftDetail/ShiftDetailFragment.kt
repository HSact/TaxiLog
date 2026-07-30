package com.hsact.taxilog.ui.fragments.shiftDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hsact.taxilog.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShiftDetailFragment : Fragment() {
    private val viewModel: ShiftDetailViewModel by viewModels()

    private var shiftId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            shiftId = it.getInt("shiftId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_shift_detail, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        if (shiftId != -1) {
            viewModel.loadShift(shiftId)
        }
        requireActivity().invalidateOptionsMenu()
        val container = view.findViewById<FrameLayout>(R.id.compose_container)
        val composeView =
            ComposeView(requireContext()).apply {
                setContent {
                    val state by viewModel.uiState.collectAsState()
                    val settings by viewModel.settings.collectAsState()
                    ShiftDetailScreen(
                        state,
                        settings.currency,
                        { editShift() },
                        { deleteShift() },
                    )
                }
            }
        container.addView(composeView)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sequenceNumber.collect { number ->
                if (number != null) {
                    (requireActivity() as? AppCompatActivity)?.supportActionBar?.title =
                        getString(R.string.title_shift_detail, number)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentNumber = viewModel.sequenceNumber.value
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title =
            if (currentNumber != null && currentNumber != -1) {
                getString(R.string.title_shift_detail, currentNumber)
            } else {
                getString(R.string.shift)
            }
    }

    /**
     * Navigates to the shift form to edit the current shift.
     */
    fun editShift() {
        val action =
            ShiftDetailFragmentDirections.actionShiftDetailFragmentToShiftForm(
                shiftId = shiftId,
            )
        findNavController().navigate(action)
    }

    /**
     * Deletes the current shift and returns to the previous screen.
     */
    fun deleteShift() {
        viewModel.deleteShift()
        findNavController().popBackStack()
    }
}
