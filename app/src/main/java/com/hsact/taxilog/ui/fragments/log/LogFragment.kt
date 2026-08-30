package com.hsact.taxilog.ui.fragments.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hsact.domain.model.Shift
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentLogBinding
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.components.EmptyStateView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogFragment : Fragment() {
    private val viewModel: LogViewModel by viewModels()
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = getString(R.string.title_my_shifts)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.filterComposeView.setContent {
            AppTheme {
                val currentPeriod by viewModel.filterPeriod.collectAsState()
                val currentSort by viewModel.sortOrder.collectAsState()
                var showSortSheet by remember { mutableStateOf(false) }

                FilterSortBar(
                    currentPeriod = currentPeriod,
                    onPeriodSelected = { viewModel.handleIntent(LogIntent.ChangeFilter(it)) },
                    onSortClick = { showSortSheet = true },
                )

                if (showSortSheet) {
                    SortBottomSheet(
                        currentSort = currentSort,
                        onSortSelected = {
                            viewModel.handleIntent(LogIntent.ChangeSort(it))
                            showSortSheet = false
                        },
                        onDismiss = { showSortSheet = false },
                    )
                }
            }
        }

        binding.statusComposeView.setContent {
            AppTheme {
                val isLoading by viewModel.isLoading.collectAsState()
                val isDbEmpty by viewModel.isDatabaseEmpty.collectAsState()
                val shifts by viewModel.shifts.collectAsState()

                Crossfade(
                    targetState = isLoading,
                    animationSpec = tween(500),
                    modifier = Modifier.fillMaxSize(),
                    label = "LogStatusTransition",
                ) { loading ->
                    if (loading) {
                        LogShimmer()
                    } else if (shifts.isEmpty()) {
                        val title = if (isDbEmpty) stringResource(R.string.log_empty_title) else stringResource(R.string.list_is_empty)
                        val description = if (isDbEmpty) stringResource(R.string.log_empty_description) else stringResource(R.string.filter_empty_description)

                        EmptyStateView(
                            icon = Icons.AutoMirrored.Filled.List,
                            title = title,
                            description = description,
                            actionText = stringResource(R.string.new_shift),
                            onAction = {
                                val action = LogFragmentDirections.actionLogFragmentToShiftForm(shiftId = -1)
                                findNavController().navigate(action)
                            },
                        )
                    } else {
                        // Keep it transparent but fill size to prevent "shrink" effect during transition
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.shifts,
                    viewModel.settings,
                    viewModel.isLoading,
                ) { shiftList, settings, isLoading ->
                    Triple(shiftList, settings, isLoading)
                }.collect { (shiftList, settings, isLoading) ->
                    val isEmpty = shiftList.isEmpty()
                    val isDataReady = !isLoading && !isEmpty

                    if (isDataReady) {
                        // Data loaded, fade out status layer smoothly
                        if (binding.statusComposeView.visibility == View.VISIBLE) {
                            binding.statusComposeView.animate()
                                .alpha(0f)
                                .setDuration(400)
                                .withEndAction {
                                    binding.statusComposeView.visibility = View.GONE
                                    binding.statusComposeView.alpha = 1f
                                }
                                .start()
                        }
                        binding.recyclerView.visibility = View.VISIBLE

                        binding.recyclerView.adapter =
                            RecyclerAdapter(
                                shiftList,
                                settings = settings,
                                onItemClick = { shift ->
                                    onClickElement(shift)
                                },
                                onItemMenuClick = { visibleNumber, shift ->
                                    onLongClickElement(shift, visibleNumber)
                                },
                            )
                        // Restore the RecyclerView scroll state
                        viewModel.recyclerViewState?.let { state ->
                            binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
                        }
                    } else {
                        // Loading or Empty, show status layer
                        binding.statusComposeView.alpha = 1f
                        binding.statusComposeView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }
            }
        }

        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater,
                ) {
                    menuInflater.inflate(R.menu.menu_log, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.action_delete_all -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.delete)
                                .setMessage(getString(R.string.delete_all) + "?")
                                .setPositiveButton(getString(R.string.yes)) { _, _ -> deleteAll() }
                                .setNegativeButton(getString(R.string.cancel), null)
                                .show()
                            true
                        }

                        else -> false
                    }
            },
            viewLifecycleOwner,
        )
    }

    override fun onPause() {
        super.onPause()
        // Saving the RecyclerView scroll state
        binding.recyclerView.layoutManager
            ?.onSaveInstanceState()
            ?.let { viewModel.recyclerViewState = it }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleIntent(LogIntent.UpdateList)
    }

    /**
     * Handles navigation to the shift details screen.
     * @param shift The selected shift.
     */
    private fun onClickElement(shift: Shift) {
        val action =
            LogFragmentDirections.actionLogFragmentToShiftDetails(
                shiftId = shift.id,
            )
        findNavController().navigate(action)
    }

    /**
     * Shows a context menu for editing or deleting a shift.
     * @param shift The selected shift.
     * @param visibleNumber The sequence number to show to the user.
     */
    private fun onLongClickElement(
        shift: Shift,
        visibleNumber: Int,
    ) {
        val items = arrayOf(getString(R.string.edit), getString(R.string.delete))
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${getString(R.string.edit_or_delete_shift)} $visibleNumber?")
            .setItems(items) { _, which -> onPopUpMenuClicked(which, shift, visibleNumber) }
            .show()
    }

    private fun onPopUpMenuClicked(
        item: Int,
        shift: Shift,
        visibleNumber: Int,
    ) {
        when (item) {
            0 -> editShift(shift)
            1 -> deleteShift(shift, visibleNumber)
        }
    }

    /**
     * Navigates to the shift form to edit the selected shift.
     */
    private fun editShift(shift: Shift) {
        val action =
            LogFragmentDirections.actionLogFragmentToShiftForm(
                shiftId = shift.id,
            )
        findNavController().navigate(action)
    }

    /**
     * Deletes the selected shift and shows a confirmation toast.
     */
    private fun deleteShift(
        shift: Shift,
        visibleNumber: Int,
    ) {
        viewModel.handleIntent(LogIntent.DeleteShift(shift))
        Toast
            .makeText(
                requireContext(),
                getString(R.string.shift_deleted_successfully, visibleNumber.toString()),
                Toast.LENGTH_SHORT,
            ).show()
    }

    private fun deleteAll() {
        viewModel.handleIntent(LogIntent.DeleteAllShifts)
        Toast
            .makeText(
                requireContext(),
                getString(R.string.all_shifts_have_been_deleted_successfully),
                Toast.LENGTH_SHORT,
            ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
