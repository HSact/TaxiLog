package com.hsact.taxilog.ui.fragments.shiftDetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.cards.BaseCard
import com.hsact.taxilog.ui.components.CardHeader
import com.hsact.taxilog.ui.components.LabelValueRow
import com.hsact.taxilog.ui.shift.ShiftOutputModel
import com.hsact.taxilog.ui.shift.mappers.toUi

@Composable
fun ShiftDetailScreen(
    uiState: ShiftDetailUiState,
    currencySymbolMode: CurrencySymbolMode?,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
) {
    AppTheme {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith
                    fadeOut(animationSpec = tween(400))
            },
            label = "ShiftDetailTransition",
        ) { state ->
            when (state) {
                is ShiftDetailUiState.Loading -> {
                    ShiftDetailShimmer()
                }
                is ShiftDetailUiState.Success -> {
                    ShiftDetailContent(
                        shift = state.shift,
                        currencySymbolMode = currencySymbolMode,
                        onEditClick = onEditClick,
                        onDeleteConfirmed = onDeleteConfirmed,
                    )
                }
                is ShiftDetailUiState.NotFound -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.no_shift_data),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShiftDetailContent(
    shift: Shift,
    currencySymbolMode: CurrencySymbolMode?,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]
    val ui =
        shift.toUi(
            locale,
            currencySymbolMode ?: CurrencySymbolMode.fromLocale(locale),
        )
    val textButtonColor = if (isSystemInDarkTheme()) Color.Black else Color.White

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item { CarCard(ui) }
        item { TimeCard(ui) }
        item { FinanceCard(shift, ui) }
        item { OtherCard(ui) }
        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.edit), color = Color.Black)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors =
                        androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        ),
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
                    },
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ShiftDetailShimmer() {
    val shimmerColors =
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1000),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmerTranslate",
        )

    val brush =
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim.value, y = translateAnim.value),
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        repeat(4) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(brush, RoundedCornerShape(12.dp)),
            )
        }
    }
}

@Composable
fun CarCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            CardHeader(text = stringResource(R.string.car))
            Space()
            if (ui.carName.isNotBlank()) {
                LabelValueRow(
                    label = stringResource(R.string.car_name),
                    value = ui.carName,
                )
            }
            LabelValueRow(
                label = stringResource(R.string.mileage),
                value = ui.mileageKm,
            )
            LabelValueRow(
                label = stringResource(R.string.fuel),
                value = ui.fuelConsumption,
            )
        }
    }
}

@Composable
fun TimeCard(ui: ShiftOutputModel) {
    BaseCard {
        Column {
            CardHeader(text = stringResource(R.string.time))
            Space()
            LabelValueRow(
                label = stringResource(R.string.date),
                value =
                    if (ui.dateBegin == ui.dateEnd) {
                        ui.dateBegin
                    } else {
                        "${ui.dateBegin} - ${ui.dateEnd}"
                    },
            )
            LabelValueRow(
                label = stringResource(R.string.time),
                value = "${ui.timeBegin} - ${ui.timeEnd}",
            )
            if (ui.timeRestBegin.isNotBlank() && ui.timeRestEnd.isNotBlank()) {
                LabelValueRow(
                    label = stringResource(R.string.rest),
                    value = "${ui.timeRestBegin} – ${ui.timeRestEnd}",
                )
            }
            LabelValueRow(
                label = stringResource(R.string.duration),
                value = ui.duration,
            )
        }
    }
}

@Composable
fun FinanceCard(
    shiftData: Shift,
    ui: ShiftOutputModel,
) {
    BaseCard {
        Column {
            CardHeader(text = stringResource(R.string.finance))
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
                CardHeader(text = stringResource(R.string.note))
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
