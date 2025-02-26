package com.example.taxidrivercalculator.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taxidrivercalculator.R


class CustomRecyclerAdapter(private val myShifts: List<Shift>) :
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
        val textProfit: TextView = itemView.findViewById(R.id.textProfit1)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.textId.text = myShifts[position].id.toString()
        holder.textDate.text = myShifts[position].date
        holder.textTime.text = myShifts[position].time
        holder.textEarnings.text = myShifts[position].earnings.toString()
        holder.textWash.text = myShifts[position].wash.toString()
        holder.textFuel.text = myShifts[position].fuelCost.toString()
        holder.textMileage.text = myShifts[position].mileage.toString()
        holder.textProfit.text = myShifts[position].profit.toString()
        holder.shiftTable.id = myShifts[position].id

    }

    /*override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }*/

    override fun getItemCount(): Int {
        return myShifts.size
    }
}