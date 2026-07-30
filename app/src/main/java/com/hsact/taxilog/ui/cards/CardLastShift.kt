package com.hsact.taxilog.ui.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.components.CardHeader
import com.hsact.taxilog.ui.components.LabelValueRow
import com.hsact.taxilog.ui.shift.mappers.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CardLastShift(
    shift: StateFlow<Shift?>,
    symbolMode: CurrencySymbolMode,
    onClick: () -> Unit,
) {
    val lastShift = shift.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val lastShiftValue = lastShift.value

    BaseCard(modifier = Modifier.clickable(onClick = onClick)) {
        Column {
            CardHeader(text = stringResource(R.string.last_shift))
            Spacer(Modifier.height(8.dp))

            if (lastShiftValue != null) {
                val lastShiftUi = lastShiftValue.toUi(locale, symbolMode)
                LabelValueRow(stringResource(R.string.date), lastShiftUi.dateBegin)
                LabelValueRow(stringResource(R.string.earnings), lastShiftUi.earnings)
                LabelValueRow(stringResource(R.string.costs), lastShiftUi.totalExpenses)
                LabelValueRow(stringResource(R.string.time), lastShiftUi.duration)
                LabelValueRow(stringResource(R.string.profit), lastShiftUi.profit)
                LabelValueRow(stringResource(R.string.per_hour), lastShiftUi.earningsPerHour)
            } else {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.home_no_shifts_yet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardPreview() {
    val na = "N/A"
    remember {
        MutableStateFlow(
            mapOf(
                "date" to na,
                "earnings" to na,
                "costs" to na,
                "time" to na,
                "total" to na,
                "perHour" to na,
            ),
        )
    }
}
