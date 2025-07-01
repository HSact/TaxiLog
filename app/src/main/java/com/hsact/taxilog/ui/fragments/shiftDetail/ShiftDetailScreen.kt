package com.hsact.taxilog.ui.fragments.shiftDetail

import androidx.compose.ui.res.stringResource
import com.hsact.taxilog.R
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
import androidx.compose.ui.unit.dp
import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.cards.BaseCard
import com.hsact.taxilog.ui.shift.ShiftOutputModel
import com.hsact.taxilog.ui.shift.mappers.toUi
import java.util.Locale

@Composable
fun ShiftDetailScreen(
    shift: Shift?,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
) {
    AppTheme {
        var showDeleteDialog by remember { mutableStateOf(false) }

        shift?.let {
            val ui = it.toUi(Locale.getDefault())
            val textButtonColor = if (isSystemInDarkTheme()) Color.Black else Color.White

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { CarCard(ui) }
                item { TimeCard(ui) }
                item { FinanceCard(ui) }
                item { OtherCard(ui) }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(R.string.edit), color = textButtonColor)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
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
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun CarCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header(text = stringResource(R.string.car))
            Space()
            if (ui.carName.isNotBlank()) {
                Text("${stringResource(R.string.car_name)}: ${ui.carName}")
            }
            Text("${stringResource(R.string.mileage)}: ${ui.mileageKm}")
            Text("${stringResource(R.string.fuel)}: ${ui.fuelConsumption}")
        }
    }
}

@Composable
fun TimeCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header(text = stringResource(R.string.time))
            Space()
            Text("${stringResource(R.string.date)}: ${ui.date}")
            Text("${stringResource(R.string.start)}: ${ui.timeBegin}")
            Text("${stringResource(R.string.end)}: ${ui.timeEnd}")
            if (ui.timeRestBegin.isNotBlank() && ui.timeRestEnd.isNotBlank()) {
                Text("${stringResource(R.string.rest)}: ${ui.timeRestBegin} – ${ui.timeRestEnd}")
            }
            Text("${stringResource(R.string.duration)}: ${ui.duration}")
        }
    }
}

@Composable
fun FinanceCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            Header(text = stringResource(R.string.finance))
            Space()
            Text("${stringResource(R.string.earnings)}: ${ui.earnings}")
            Text("${stringResource(R.string.tips)}: ${ui.tips}")
            Text("${stringResource(R.string.fuel)}: ${ui.fuelCost}")
            Text("${stringResource(R.string.wash)}: ${ui.wash}")
            Text("${stringResource(R.string.tax)}: ${ui.tax}")
            Text("${stringResource(R.string.profit)}: ${ui.profit}")
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
private fun Space() {
    Spacer(Modifier.height(8.dp))
}