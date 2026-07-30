package com.hsact.taxilog.ui.fragments.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = getString(R.string.title_my_shifts)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.emptyStateCompose.setContent {
            AppTheme {
                EmptyStateView(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = getString(R.string.log_empty_title),
                    description = getString(R.string.log_empty_description),
                    actionText = getString(R.string.new_shift),
                    onAction = {
                        val action = LogFragmentDirections.actionLogFragmentToShiftForm(shiftId = -1)
                        findNavController().navigate(action)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shifts.combine(viewModel.settings) { shiftList, settings ->
                    Pair(shiftList, settings)
                }.collect { (shiftList, settings) ->
                    val isEmpty = shiftList.isEmpty()
                    binding.recyclerView.isVisible = !isEmpty
                    binding.emptyStateCompose.isVisible = isEmpty

                    if (!isEmpty) {
                        binding.recyclerView.adapter = RecyclerAdapter(
                            shiftList,
                            settings = settings,
                            onItemClick = { shift ->
                                onClickElement(shift)
                            },
                            onItemMenuClick = { visibleNumber, shift ->
                                onLongClickElement(shift, visibleNumber)
                            }
                        )
                        // Restore the RecyclerView scroll state
                        viewModel.recyclerViewState?.let { state ->
                            binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
                        }
                    }
                }
            }
        }

        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_log, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
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
            }
        }, viewLifecycleOwner)
    }

    override fun onPause() {
        super.onPause()
        //Saving the RecyclerView scroll state
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
        val action = LogFragmentDirections.actionLogFragmentToShiftDetails(
            shiftId = shift.id
        )
        findNavController().navigate(action)
    }

    /**
     * Shows a context menu for editing or deleting a shift.
     * @param shift The selected shift.
     * @param visibleNumber The sequence number to show to the user.
     */
    private fun onLongClickElement(shift: Shift, visibleNumber: Int) {
        val items = arrayOf(getString(R.string.edit), getString(R.string.delete))
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${getString(R.string.edit_or_delete_shift)} $visibleNumber?")
            .setItems(items) { _, which -> onPopUpMenuClicked(which, shift, visibleNumber) }
            .show()
    }

    private fun onPopUpMenuClicked(item: Int, shift: Shift, visibleNumber: Int) {
        when (item) {
            0 -> editShift(shift)
            1 -> deleteShift(shift, visibleNumber)
        }
    }

    /**
     * Navigates to the shift form to edit the selected shift.
     */
    private fun editShift(shift: Shift) {
        val action = LogFragmentDirections.actionLogFragmentToShiftForm(
            shiftId = shift.id
        )
        findNavController().navigate(action)
    }

    /**
     * Deletes the selected shift and shows a confirmation toast.
     */
    private fun deleteShift(shift: Shift, visibleNumber: Int) {
        viewModel.handleIntent(LogIntent.DeleteShift(shift))
        Toast.makeText(
            requireContext(),
            getString(R.string.shift_deleted_successfully, visibleNumber.toString()), Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteAll() {
        viewModel.handleIntent(LogIntent.DeleteAllShifts)
        Toast.makeText(
            requireContext(),
            getString(R.string.all_shifts_have_been_deleted_successfully), Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}