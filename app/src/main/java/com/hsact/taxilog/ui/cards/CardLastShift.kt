package com.hsact.taxilog.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import java.util.Locale

@Composable
fun CardLastShift(shift: StateFlow<Shift?>, symbolMode: CurrencySymbolMode) {
    val lastShift = shift.collectAsStateWithLifecycle()
    val lastShiftUi = lastShift.value?.toUi(Locale.getDefault(), symbolMode)
    BaseCard {
        Column {
            CardHeader(text = stringResource(R.string.last_shift))
            Spacer(Modifier.height(8.dp))
            LabelValueRow(stringResource(R.string.date), lastShiftUi?.dateBegin ?: "")
            LabelValueRow(stringResource(R.string.earnings), lastShiftUi?.earnings ?: "")
            LabelValueRow(stringResource(R.string.costs), lastShiftUi?.totalExpenses ?: "")
            LabelValueRow(stringResource(R.string.time), lastShiftUi?.duration ?: "")
            LabelValueRow(stringResource(R.string.profit), lastShiftUi?.profit ?: "")
            LabelValueRow(stringResource(R.string.per_hour), lastShiftUi?.earningsPerHour ?: "")
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
                "perHour" to na
            )
        )
    }
}