package com.example.taxidrivercalculator.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taxidrivercalculator.DBHelper
import com.example.taxidrivercalculator.MainActivity
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var textDate: TextView
    private lateinit var textEarnings: TextView
    private lateinit var textCosts: TextView
    private lateinit var textTime: TextView
    private lateinit var textTotal: TextView
    private lateinit var textPerHour: TextView
    private lateinit var textPlan: TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindItems()
        printShift()


        binding.buttonNewShift.setOnClickListener {

            newShift()
        }
    }

    private fun bindItems ()
    {
        textDate = binding.textHomeDateR
        textEarnings = binding.textHomeEarningsR
        textCosts = binding.textHomeCostsR
        textTime = binding.textHomeTimeR
        textTotal = binding.textHomeTotalR
        textPerHour = binding.textHomePerHourR
        textPlan = binding.textHomePlanR
    }

    @SuppressLint("SetTextI18n")
    private fun printShift ()
    {
        val db = DBHelper(requireActivity(), null)
        //db.addShift("1.01.1001", 8.0, 1337.0, 228.0, 1488.0, 30.0, 300.0)
        val cursor = db.getShift()
        cursor!!.moveToLast()
        if (cursor.position==-1)
        {
            setEmptyBDView()
            cursor.close()
            return
        }

        val perHour = (((cursor.getDouble(cursor.getColumnIndex(DBHelper.EARNINGS_COL)+0))/
                cursor.getDouble(cursor.getColumnIndex(DBHelper.TIME_COL)+0))*100.0).toInt()/100.0
        textDate.text = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_COl)+0)
        textEarnings.text = cursor.getString(cursor.getColumnIndex(DBHelper.EARNINGS_COL)+0)
        textCosts.text = ((cursor.getDouble(cursor.getColumnIndex(DBHelper.WASH_COL)+0))+
                (cursor.getDouble(cursor.getColumnIndex(DBHelper.FUEL_COL)+0))).toString()
        textTime.text = cursor.getString(cursor.getColumnIndex(DBHelper.TIME_COL)+0) + getString(R.string.hours)
        textTotal.text = cursor.getString(cursor.getColumnIndex(DBHelper.PROFIT_COL)+0)
        textPerHour.text = perHour.toString()


        val settings = this.activity?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val goalMonthString = settings?.getString("Goal_per_month", "")
        if (goalMonthString == null || goalMonthString == "")
        {
            textPlan.text = getString(R.string.n_a)
        }
        else
        {
            textPlan.text = cursor.getString(cursor.getColumnIndex(DBHelper.PROFIT_COL)+0) +" of " + goalMonthString
        }
        cursor.close()
    }

    private fun setEmptyBDView ()
    {
        textDate.text = getString(R.string.n_a)
        textEarnings.text = getString(R.string.n_a)
        textCosts.text = getString(R.string.n_a)
        textTime.text = getString(R.string.n_a)
        textTotal.text = getString(R.string.n_a)
        textPerHour.text = getString(R.string.n_a)
        textPlan.text = getString(R.string.n_a)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    public fun newShift()
    {
        findNavController().navigate(R.id.action_homeFragment_to_addShift)
    }

}