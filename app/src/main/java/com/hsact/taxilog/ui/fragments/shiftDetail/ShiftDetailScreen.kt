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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.cards.BaseCard
import com.hsact.taxilog.ui.shift.ShiftOutputModel
import com.hsact.taxilog.ui.shift.mappers.toUi
import java.util.Locale

@Composable
fun ShiftDetailScreen(
    shift: Shift?,
    currencySymbolMode: CurrencySymbolMode?,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
) {
    AppTheme {
        var showDeleteDialog by remember { mutableStateOf(false) }

        shift?.let {
            val ui = it.toUi(
                Locale.getDefault(),
                currencySymbolMode ?: CurrencySymbolMode.fromLocale(Locale.getDefault())
            )
            val textButtonColor = if (isSystemInDarkTheme()) Color.Black else Color.White

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item { CarCard(ui) }
                item { TimeCard(ui) }
                item { FinanceCard(shift, ui) }
                item { OtherCard(ui) }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(R.string.edit), color = textButtonColor)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(text = stringResource(R.string.delete), color = textButtonColor)
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = stringResource(R.string.delete_shift)) },
                    text = { Text(stringResource(R.string.dialog_delete_message)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                onDeleteConfirmed()
                            }
                        ) {
                            Text(text = stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_shift_data),
                    style = MaterialTheme.typography.bodyMedium
                )
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
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
}

@Composable
fun CarCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header(text = stringResource(R.string.car))
            Space()
            if (ui.carName.isNotBlank()) {
                LabelValueRow(
                    label = stringResource(R.string.car_name),
                    value = ui.carName
                )
            }
            LabelValueRow(
                label = stringResource(R.string.mileage),
                value = ui.mileageKm
            )
            LabelValueRow(
                label = stringResource(R.string.fuel),
                value = ui.fuelConsumption
            )
        }
    }
}

@Composable
fun TimeCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header(text = stringResource(R.string.time))
            Space()
            LabelValueRow(
                label = stringResource(R.string.date),
                value = if (ui.dateBegin == ui.dateEnd)
                    ui.dateBegin
                else
                    "${ui.dateBegin} - ${ui.dateEnd}"
            )
            LabelValueRow(
                label = stringResource(R.string.time),
                value = "${ui.timeBegin} - ${ui.timeEnd}"
            )
            if (ui.timeRestBegin.isNotBlank() && ui.timeRestEnd.isNotBlank()) {
                LabelValueRow(
                    label = stringResource(R.string.rest),
                    value = "${ui.timeRestBegin} – ${ui.timeRestEnd}"
                )
            }
            LabelValueRow(
                label = stringResource(R.string.duration),
                value = ui.duration
            )
        }
    }
}

@Composable
fun FinanceCard(shiftData: Shift, ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header(text = stringResource(R.string.finance))
            Space()
            LabelValueRow(stringResource(R.string.earnings), ui.earnings)
            LabelValueRow(stringResource(R.string.earnings_per_hour), ui.earningsPerHour)
            LabelValueRow(stringResource(R.string.earnings_per_km), ui.earningsPerKm)
            if (shiftData.tipsIsNotZero) {
                LabelValueRow(stringResource(R.string.tips), ui.tips)
            }
            if (shiftData.rentIsNotZero) {
                LabelValueRow(stringResource(R.string.rent), ui.rent)
            }
            LabelValueRow(stringResource(R.string.fuel), ui.fuelCost)
            if (shiftData.washIsNotZero) {
                LabelValueRow(stringResource(R.string.wash), ui.wash)
            }
            if (shiftData.serviceIsNotZero) {
                LabelValueRow(stringResource(R.string.service), ui.serviceCost)
            }
            if (shiftData.taxIsNotZero) {
                LabelValueRow(stringResource(R.string.tax), ui.tax)
            }
            LabelValueRow(stringResource(R.string.total_expenses), ui.totalExpenses)
            LabelValueRow(stringResource(R.string.profit), ui.profit)
            LabelValueRow(stringResource(R.string.profit_per_km), ui.profitPerKm)
            LabelValueRow(stringResource(R.string.profit_per_hour), ui.profitPerHour)
            LabelValueRow(stringResource(R.string.profit_margin), ui.profitMarginPercent)
        }
    }
}

@Composable
fun OtherCard(ui: ShiftOutputModel) {
    if (!ui.note.isNullOrBlank()) {
        BaseCard {
            Column {
                Header(text = stringResource(R.string.note))
                Space()
                Text(ui.note)
            }
        }
    }
}

@Composable
private fun LabelValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label,
            style = MaterialTheme.typography.bodyLarge)
        Text(text = value,
            style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun Space() {
    Spacer(Modifier.height(8.dp))
}