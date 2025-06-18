package com.hsact.taxilog.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hsact.taxilog.R
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import com.hsact.taxilog.ui.shift.ShiftOutputModel

class ShiftLogRecyclerAdapter(private val shifts: List<ShiftOutputModel>) :
    RecyclerView.Adapter<ShiftLogRecyclerAdapter.ShiftViewHolder>() {
    class ShiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
        return ShiftViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, index: Int) {
        holder.textId.text = shifts[index].id.toString()
        holder.textDate.text = shifts[index].date
        holder.textTime.text = shifts[index].duration
        holder.textEarnings.text = shifts[index].earnings
        holder.textWash.text = shifts[index].wash
        holder.textFuel.text = shifts[index].fuelCost
        holder.textMileage.text = shifts[index].mileageKm
        holder.textPerHour.text = shifts[index].earningsPerHour
        holder.textProfit.text = shifts[index].profit
        holder.shiftTable.id = shifts[index].id
    }

    override fun getItemCount(): Int {
        return shifts.size
    }
}