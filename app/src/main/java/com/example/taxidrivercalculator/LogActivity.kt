package com.example.taxidrivercalculator

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TableLayout
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taxidrivercalculator.databinding.ActivityLogBinding
import com.example.taxidrivercalculator.databinding.RecyclerviewItemBinding
import com.example.taxidrivercalculator.ui.home.HomeFragment


class LogActivity : AppCompatActivity() {

    private var shifts = mutableListOf<Shift>()

    private lateinit var binding: ActivityLogBinding
    private lateinit var bindingR: RecyclerviewItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogBinding.inflate(layoutInflater)
        bindingR = RecyclerviewItemBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_log)
        supportActionBar?.title = getString(R.string.title_my_shifts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //makeArray()
        shifts=ShiftHelper.makeArray(DBHelper(this, null))
        val recycler: RecyclerView = findViewById(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)

        recycler.adapter = CustomRecyclerAdapter(fillList())
        recycler.hasOnClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_log, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_delete_all -> {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Delete")
                alert.setPositiveButton("YES") {dialog, id -> deleteAll()}
                alert.setNegativeButton("CANCEL", null)
                alert.setMessage("Delete all?")
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
        onBackPressed();
        return super.onSupportNavigateUp()
    }

    fun onClickElement(view: View) {

        //Toast.makeText(this, view.id.toString(), Toast.LENGTH_SHORT).show()
        val items = arrayOf("Edit", "Delete")

        val alert = AlertDialog.Builder(this)

        /*alert.setPositiveButton("YES") {dialog, id -> deleteShift(view.id)}
        alert.setNegativeButton("CANCEL", null)
        alert.setMessage("Delete shift ID " + view.id + "?")*/
        alert.setTitle(getString(R.string.edit_or_delete_shift) + view.id + "?")
        with(alert)
        {
            //setTitle("Edit or delete shift" + items[which])
            setItems(items) { dialog, id ->
                onPopUpMenuClicked(id, view.id)
            }
        alert.show()
        }
    /*fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.shift_float_menu, popup.menu)

        popup.show()*/
    }

    private fun onPopUpMenuClicked (item: Int, shiftId: Int)
    {
        Toast.makeText(applicationContext, "$item for ID $shiftId is clicked", Toast.LENGTH_SHORT).show()
        if (item==0) editShift(shiftId)
        if (item==1) deleteShift(shiftId)
    }

    private fun editShift(index: Int)
    {
        //TODO: FIX intent
        /*startActivity(Intent (this, MainActivity::class.java))

        findNavController().navigate(R.id.action_homeFragment_to_addShift)
        val logIntent = Intent(this, AddShift::class.java)
        startActivity(logIntent)*/
        /*val bundle = Bundle().apply {
            putInt("SHIFT_ID", index-1) // Передаем ID смены в аргументы фрагмента
        }

        val fragment = AddShift()
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, fragment) // ID контейнера с фрагментами
            .addToBackStack(null) // Добавляем в стек возврата
            .commit()*/

    }

    private fun deleteShift(index: Int)
    {
        //shifts.removeAt(index)
        val db = DBHelper(this, null)
        db.deleteShift(index)
        db.close()
        recreate()
    }
    private fun deleteAll()
    {
        val db = DBHelper(this, null)
        db.deleteAll()
        db.close()
        recreate()
    }
}