package com.example.taxidrivercalculator.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.taxidrivercalculator.R


class CardLastShift {

    @Composable
    fun drawLastShiftCard() {
        BaseCard {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = stringResource(R.string.date))
                    Text(text = stringResource(R.string.earnings))
                    Text(text = stringResource(R.string.costs))
                    Text(text = stringResource(R.string.time))
                    Text(text = stringResource(R.string.total))
                    Text(text = stringResource(R.string.per_hour))
                }
                Column {
                    Text(text = "Date")
                    Text(text = "Earnings")
                    Text(text = "Costs")
                    Text(text = "Time")
                    Text(text = "Total")
                    Text(text = "Per hour")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BaseCardPreview() {
        drawLastShiftCard()
        }
}