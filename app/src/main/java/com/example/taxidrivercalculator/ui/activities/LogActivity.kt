package com.example.taxidrivercalculator.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taxidrivercalculator.helpers.CustomRecyclerAdapter
import com.example.taxidrivercalculator.data.db.DBHelper
import com.example.taxidrivercalculator.helpers.LocaleHelper
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.data.model.Shift
import com.example.taxidrivercalculator.helpers.ShiftHelper
import com.example.taxidrivercalculator.databinding.ActivityLogBinding
import com.example.taxidrivercalculator.databinding.DialogShiftEditBinding
import com.example.taxidrivercalculator.databinding.RecyclerviewItemBinding
import com.example.taxidrivercalculator.ui.fragments.DatePickerFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.widget.Toolbar
import com.example.taxidrivercalculator.data.repository.ShiftRepository


class LogActivity : AppCompatActivity() {

    private var shifts = mutableListOf<Shift>()
    private var shiftRepository = ShiftRepository(DBHelper(this, null))

    private lateinit var binding: ActivityLogBinding
    private lateinit var bindingR: RecyclerviewItemBinding
    private lateinit var bindingE: DialogShiftEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        bindingR = RecyclerviewItemBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_log)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_my_shifts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        shifts = shiftRepository.getAllShifts()
        if (shifts.isEmpty())
        {
            Toast.makeText(applicationContext,
                getString(R.string.list_is_empty), Toast.LENGTH_SHORT).show()
        }
        val recycler: RecyclerView = findViewById(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)

        recycler.adapter = CustomRecyclerAdapter(fillList())
        recycler.hasOnClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_log, menu)
        return true
    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleHelper.updateLocale(
                newBase,
                LocaleHelper.getSavedLanguage(newBase)
            )
        )
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
    private fun fillList(): MutableList<Shift> {
        val data = mutableListOf<Shift>()
        (shifts.indices.reversed()).forEach { i -> data.add(shifts[i]) }

        return data
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun onClickElement(view: View)
    {
        val items = arrayOf(getString(R.string.edit), getString(R.string.delete))
        val alert = MaterialAlertDialogBuilder(this)
        alert.setTitle(getString(R.string.edit_or_delete_shift) + " " + view.id + "?")
        with(alert)
        {
            //setTitle("Edit or delete shift" + items[which])
            setItems(items) { dialog, id ->
                onPopUpMenuClicked(id, view.id)
            }
        alert.show()
        }
    }

    private fun onPopUpMenuClicked (item: Int, shiftId: Int)
    {
        //Toast.makeText(applicationContext, "$item for ID $shiftId is clicked", Toast.LENGTH_SHORT).show()
        if (item==0) editShift(shiftId)
        if (item==1) deleteShift(shiftId)
    }

    private fun editShift(index: Int)
    {
        showEditShiftDialog(index-1)
    }

    private fun deleteShift(index: Int)
    {
        val shiftRepository = ShiftRepository(DBHelper(this, null))
        shiftRepository.deleteShift(index)
        Toast.makeText(applicationContext,
            getString(R.string.shift_deleted_successfully, index.toString()), Toast.LENGTH_SHORT).show()
        recreate()
    }
    private fun deleteAll()
    {
        val shiftRepository = ShiftRepository(DBHelper(this, null))
        shiftRepository.deleteAllShifts()
        Toast.makeText(applicationContext,
            getString(R.string.all_shifts_have_been_deleted_successfully), Toast.LENGTH_SHORT).show()
        recreate()
    }

    @SuppressLint("SetTextI18n")
    private fun showEditShiftDialog(index: Int) {
        val shift = shifts[index].copy()
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
                if (shift==shifts[index])
                {
                    dialog.dismiss()
                    Toast.makeText(applicationContext,
                        getString(R.string.nothing_changed), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                shifts[index] = shift
                DBHelper(this, null).recreateDB(shifts)
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
    }
}