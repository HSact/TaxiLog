package com.hsact.taxilog.ui.activities.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hsact.taxilog.R
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.ui.shift.mappers.toUi
import java.util.Locale

class RecyclerAdapter(
    private val shifts: List<ShiftV2>,
    private val onItemMenuClick: (ShiftV2) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapter.ShiftViewHolder>() {
    inner class ShiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        val shift = shifts[index].toUi(Locale.getDefault())
        holder.textId.text = shift.id.toString()
        holder.textDate.text = shift.date
        holder.textTime.text = shift.duration
        holder.textEarnings.text = shift.earnings
        holder.textWash.text = shift.wash
        holder.textFuel.text = shift.fuelCost
        holder.textMileage.text = shift.mileageKm
        holder.textPerHour.text = shift.earningsPerHour
        holder.textProfit.text = shift.profit
        //holder.shiftTable.id = shifts[index].id

        holder.itemView.setOnClickListener {
            onItemMenuClick(shifts[index])
            true
        }
    }

    override fun getItemCount(): Int {
        return shifts.size
    }
}