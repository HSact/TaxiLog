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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentLogBinding
import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.ui.activities.MainActivity

import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class LogFragment : Fragment() {

    private val viewModel: LogViewModel by viewModels()
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = getString(R.string.title_my_shifts)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.shifts.observe(viewLifecycleOwner) { shiftList ->
            if (shiftList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.list_is_empty), Toast.LENGTH_SHORT).show()
            }

            val shiftListWithVisibleId = shiftList.mapIndexed { index, shift ->
                Pair(shiftList.size - index, shift)
            }
            binding.recyclerView.adapter = RecyclerAdapter(
                shiftListWithVisibleId,
                onItemMenuClick = { visibleId, shift ->
                    viewModel.shifts.value?.firstOrNull { it.id == shift.id }?.let {
                        onClickElement(it, visibleId)
                    }
                }
            )
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

    private fun onClickElement(shift: Shift, visibleId: Int) {
        val items = arrayOf(getString(R.string.edit), getString(R.string.delete))
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${getString(R.string.edit_or_delete_shift)} ${visibleId}?")
            .setItems(items) { _, which -> onPopUpMenuClicked(which, shift, visibleId) }
            .show()
    }

    private fun onPopUpMenuClicked(item: Int, shift: Shift, visibleId: Int) {
        when (item) {
            0 -> editShift(shift, visibleId)
            1 -> deleteShift(shift)
        }
    }

    private fun editShift(shift: Shift, visibleId: Int) {
        val action = LogFragmentDirections.actionLogFragmentToShiftForm(shiftId = shift.id, visibleId = visibleId)
        findNavController().navigate(action)
    }

    private fun deleteShift(shift: Shift) {
        viewModel.handleIntent(LogIntent.DeleteShift(shift))
        Toast.makeText(requireContext(),
            getString(R.string.shift_deleted_successfully, shift.id.toString()), Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteAll() {
        viewModel.handleIntent(LogIntent.DeleteAllShifts)
        Toast.makeText(requireContext(),
            getString(R.string.all_shifts_have_been_deleted_successfully), Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        MainActivity.botNav.isVisible = false
        super.onResume()
    }

    override fun onDestroy() {
        MainActivity.botNav.isVisible = true
        super.onDestroy()
    }
}