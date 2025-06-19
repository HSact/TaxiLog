package com.hsact.taxilog.ui.activities.log

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.ActivityLogBinding
import com.hsact.taxilog.databinding.RecyclerviewItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.widget.Toolbar
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.ui.locale.ContextWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogActivity : AppCompatActivity() {

    private val viewModel: LogViewModel by viewModels()

    private lateinit var binding: ActivityLogBinding
    private lateinit var bindingR: RecyclerviewItemBinding
    //private lateinit var bindingE: DialogShiftEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        bindingR = RecyclerviewItemBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_log)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_my_shifts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recycler: RecyclerView = findViewById(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)

        viewModel.shifts.observe(this) { shiftList ->
            if (shiftList.isNullOrEmpty()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.list_is_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }

            recycler.adapter = RecyclerAdapter(
                shiftList,
                onItemMenuClick = { shift ->
                    val shiftV2 = viewModel.shifts.value?.firstOrNull { it.id == shift.id }
                    if (shiftV2 != null) {
                        onClickElement(shiftV2)
                    }
                })
            if (viewModel.shifts.value.isNullOrEmpty()) {
                Toast.makeText(applicationContext,
                    getString(R.string.list_is_empty), Toast.LENGTH_SHORT).show()
            }
        }
        recycler.hasOnClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_log, menu)
        return true
    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper.wrapContext(newBase))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all -> {
                val alert = MaterialAlertDialogBuilder(this)
                alert.setTitle(R.string.delete)
                alert.setPositiveButton(getString(R.string.yes)) { dialog, id -> deleteAll()}
                alert.setNegativeButton(getString(R.string.cancel), null)
                alert.setMessage(getString(R.string.delete_all) + "?")
                alert.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun onClickElement(shift: ShiftV2)
    {
        val items = arrayOf(getString(R.string.edit), getString(R.string.delete))
        val alert = MaterialAlertDialogBuilder(this)
        alert.setTitle(getString(R.string.edit_or_delete_shift) + " " + shift.id.toString() + "?")
        with(alert)
        {
            //setTitle("Edit or delete shift" + items[which])
            setItems(items) { dialog, id ->
                onPopUpMenuClicked(id, shift)
            }
        alert.show()
        }
    }

    private fun onPopUpMenuClicked (item: Int, shift: ShiftV2)
    {
        //Toast.makeText(applicationContext, "$item for ID $shiftId is clicked", Toast.LENGTH_SHORT).show()
        if (item==0) editShift(shift)
        if (item==1) deleteShift(shift)
    }

    private fun editShift(shiftV2: ShiftV2)
    {
        //showEditShiftDialog(index-1)
        //TODO: make intent to AddShiftFragment
    }

    private fun deleteShift(shift: ShiftV2)
    {
        //shiftRepositoryLegacy.deleteShift(index)
        viewModel.handleIntent(LogIntent.DeleteShift(shift))
        Toast.makeText(applicationContext,
            getString(R.string.shift_deleted_successfully, shift.id.toString()), Toast.LENGTH_SHORT).show()
        recreate()
    }
    private fun deleteAll()
    {
        //shiftRepositoryLegacy.deleteAllShifts()
        viewModel.handleIntent(LogIntent.DeleteAllShifts)
        Toast.makeText(applicationContext,
            getString(R.string.all_shifts_have_been_deleted_successfully), Toast.LENGTH_SHORT).show()
        recreate()
    }

    /*@SuppressLint("SetTextI18n")
    private fun showEditShiftDialog(index: Int) {
        //val shift = shiftsLegacy[index].copy()
        val shift = viewModel.shifts.value!![index].copy()
        val builder = MaterialAlertDialogBuilder(this)
        val dialog = builder.create()

        bindingE = DialogShiftEditBinding.inflate(layoutInflater, null, false)
        dialog.setView(bindingE.root)

        val dialogTitle: TextView = bindingE.dialogTitle
        val editDate: EditText = bindingE.editDate
        val editTime: EditText = bindingE.editTime
        val editEarnings: EditText = bindingE.editEarnings
        val editWash: EditText = bindingE.editWash
        val editFuelCost: EditText = bindingE.editFuelCost
        val editMileage: EditText = bindingE.editMileage
        val editProfit: EditText = bindingE.editProfit
        val btnCancel: Button = bindingE.btnCancel
        val btnSave: Button = bindingE.btnSave

        dialogTitle.text = getString(R.string.title_shift_editing) + " " + (index + 1).toString()

        editDate.setText(shift.date)
        editTime.setText(shift.time)
        editEarnings.setText(shift.earnings.toString())
        editWash.setText(shift.wash.toString())
        editFuelCost.setText(shift.fuelCost.toString())
        editMileage.setText(shift.mileage.toString())
        editProfit.setText(shift.profit.toString())

        editDate.setOnClickListener { pickDate(editDate) }
        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            shift.date = editDate.text.toString()
            shift.time = editTime.text.toString()
            shift.earnings = editEarnings.text.toString().toDouble()
            shift.wash = editWash.text.toString().toDouble()
            shift.fuelCost = editFuelCost.text.toString().toDouble()
            shift.mileage = editMileage.text.toString().toDouble()
            shift.profit = editProfit.text.toString().toDouble()

            if (shift.isValid())
            {
                if (shift==shiftsLegacy[index])
                {
                    dialog.dismiss()
                    Toast.makeText(applicationContext,
                        getString(R.string.nothing_changed), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                shiftsLegacy[index] = shift
                DBHelper(this, null).recreateDB(shiftsLegacy)
                dialog.dismiss()
                Toast.makeText(applicationContext,
                    getString(R.string.shift_edited_successfully), Toast.LENGTH_SHORT).show()
                recreate()
            }
            else
            {
                Toast.makeText(applicationContext,
                    getString(R.string.edit_invalid_data), Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun pickDate(editObj: EditText) {
        DatePickerFragment.pickDate(context = this, editObj = editObj)
    }*/
}