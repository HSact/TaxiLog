package com.hsact.taxilog.ui.fragments.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.usecase.shift.GetShiftSequenceNumberUseCase
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.shift.mappers.toUi

@Composable
fun ShiftSelectionContent(
    shifts: List<Shift>,
    currencySymbolMode: CurrencySymbolMode,
    getShiftSequenceNumberUseCase: GetShiftSequenceNumberUseCase? = null,
    onShiftSelected: (Shift) -> Unit,
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.select_shift),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(shifts) { shift ->
                        ShiftListItem(
                            shift = shift,
                            currencySymbolMode = currencySymbolMode,
                            getShiftSequenceNumberUseCase = getShiftSequenceNumberUseCase,
                            onClick = { onShiftSelected(shift) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ShiftListItem(
    shift: Shift,
    currencySymbolMode: CurrencySymbolMode,
    getShiftSequenceNumberUseCase: GetShiftSequenceNumberUseCase? = null,
    onClick: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val ui = shift.toUi(locale, currencySymbolMode)

    val sequenceNumber = getShiftSequenceNumberUseCase?.invoke(shift.id)
        ?.collectAsStateWithLifecycle(initialValue = null)
        ?.value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (sequenceNumber != null) 
                    stringResource(R.string.shift_number) + sequenceNumber
                else 
                    "${ui.timeBegin} - ${ui.timeEnd}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            if (sequenceNumber != null) {
                Text(
                    text = "${ui.timeBegin} - ${ui.timeEnd}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = ui.dateBegin,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = ui.profit,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}