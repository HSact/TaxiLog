package com.hsact.taxilog.ui.fragments.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.shift.mappers.toUi
import java.util.Locale

/**
 * Adapter for the shift log list.
 * Displays shifts with a calculated sequence number for the user.
 */
class RecyclerAdapter(
    private val items: List<Shift>,
    private val settings: UserSettings,
    private val onItemClick: (shift: Shift) -> Unit,
    private val onItemMenuClick: (visibleNumber: Int, shift: Shift) -> Unit,
) : RecyclerView.Adapter<RecyclerAdapter.ShiftViewHolder>() {
    class ShiftViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.textShiftNumber1)
        val textDate: TextView = itemView.findViewById(R.id.textDate1)
        val textTime: TextView = itemView.findViewById(R.id.textTime1)
        val textEarnings: TextView = itemView.findViewById(R.id.textEarnings1)
        val textWash: TextView = itemView.findViewById(R.id.textWash1)
        val textFuel: TextView = itemView.findViewById(R.id.textFuel1)
        val textMileage: TextView = itemView.findViewById(R.id.textMileage1)
        val textPerHour: TextView = itemView.findViewById(R.id.textPerHour1)
        val textProfit: TextView = itemView.findViewById(R.id.textProfit1)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ShiftViewHolder {
        val itemView =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
        return ShiftViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ShiftViewHolder,
        index: Int,
    ) {
        val locale = Locale.getDefault()
        val shiftModel = items[index]
        val shiftUi =
            shiftModel.toUi(
                locale,
                settings.currency ?: CurrencySymbolMode.fromLocale(Locale.getDefault()),
            )
        val visibleNumber = shiftModel.sequenceNumber
        holder.textId.text = visibleNumber.toString()
        holder.textDate.text = shiftUi.dateBegin
        holder.textTime.text = shiftUi.duration
        holder.textEarnings.text = shiftUi.earnings
        holder.textWash.text = shiftUi.wash
        holder.textFuel.text = shiftUi.fuelCost
        holder.textMileage.text = shiftUi.mileageKm
        holder.textPerHour.text = shiftUi.earningsPerHour
        holder.textProfit.text = shiftUi.profit

        holder.itemView.setOnClickListener {
            onItemClick(shiftModel)
        }

        holder.itemView.setOnLongClickListener {
            onItemMenuClick(visibleNumber, shiftModel)
            true
        }
    }

    override fun getItemCount(): Int = items.size
}
