package com.hsact.taxilog.ui.fragments.shiftDetail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.cards.BaseCard
import com.hsact.taxilog.ui.shift.ShiftOutputModel
import com.hsact.taxilog.ui.shift.mappers.toUi
import java.util.Locale

@Composable
fun ShiftDetailScreen(shift: Shift?) {
    AppTheme {
        shift?.let {
            val ui = it.toUi(Locale.getDefault())
            val textButtonColor = if (isSystemInDarkTheme()) Color.Black else Color.White
            Column(modifier = Modifier.padding(10.dp)) {
                CarCard(ui)
                TimeCard(ui)
                FinanceCard(ui)
                OtherCard(ui)
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .padding(bottom = 50.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Edit", color = textButtonColor)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(text = "Delete", color = textButtonColor)
                    }
                }
            }
        }
            ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No shift data", style = MaterialTheme.typography.bodyMedium)
                }
            }
    }
}

@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun CarCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header("Car")
            Space()
            if (ui.carName.isNotBlank()) {
                Text("Name: ${ui.carName}")
            }
            Text("Mileage: ${ui.mileageKm}")
            Text("Fuel: ${ui.fuelConsumption}")
        }
    }
}

@Composable
fun TimeCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header("Time")
            Space()
            Text("Date: ${ui.date}")
            Text("Start: ${ui.timeBegin}")
            Text("End: ${ui.timeEnd}")
            if (ui.timeRestBegin.isNotBlank() && ui.timeRestEnd.isNotBlank()) {
                Text("Rest: ${ui.timeRestBegin} – ${ui.timeRestEnd}")
            }
            Text("Duration: ${ui.duration}")
        }
    }
}

@Composable
fun FinanceCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header("Finance")
            Space()
            Text("Earnings: ${ui.earnings}")
            Text("Tips: ${ui.tips}")
            Text("Fuel: ${ui.fuelCost}")
            Text("Wash: ${ui.wash}")
            Text("Tax: ${ui.tax}")
            Text("Profit: ${ui.profit}")
        }
    }
}

@Composable
fun OtherCard(ui: ShiftOutputModel) {
    if (!ui.note.isNullOrBlank()) {
        BaseCard {
            Column {
                Header("Note")
                Space()
                Text(ui.note)
            }
        }
    }
}

@Composable
private fun Space() {
    Spacer(Modifier.height(8.dp))
}