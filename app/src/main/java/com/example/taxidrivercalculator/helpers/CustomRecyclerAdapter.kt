package com.example.taxidrivercalculator.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taxidrivercalculator.R


class CustomRecyclerAdapter(private val shifts: List<Shift>) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val shiftTable: TableLayout = itemView.findViewById(R.id.tableShift)

        val textId: TextView = itemView.findViewById(R.id.textId1)
        val textDate: TextView = itemView.findViewById(R.id.textDate1)
        val textTime: TextView = itemView.findViewById(R.id.textTime1)
        val textEarnings: TextView = itemView.findViewById(R.id.textEarnings1)
        val textWash: TextView = itemView.findViewById(R.id.textWash1)
        val textFuel: TextView = itemView.findViewById(R.id.textFuel1)
        val textMileage: TextView = itemView.findViewById(R.id.textMileage1)
        val textPerHour: TextView = itemView.findViewById(R.id.textPerHour1)
        val textProfit: TextView = itemView.findViewById(R.id.textProfit1)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.textId.text = shifts[position].id.toString()
        holder.textDate.text = shifts[position].date
        holder.textTime.text = shifts[position].time
        holder.textEarnings.text = shifts[position].earnings.toString()
        holder.textWash.text = shifts[position].wash.toString()
        holder.textFuel.text = shifts[position].fuelCost.toString()
        holder.textMileage.text = shifts[position].mileage.toString()
        holder.textPerHour.text = ShiftHelper.calcAverageEarningsPerHour(shifts[position]).toString()
        holder.textProfit.text = shifts[position].profit.toString()
        holder.shiftTable.id = shifts[position].id

    }

    /*override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }*/

    override fun getItemCount(): Int {
        return shifts.size
    }
}