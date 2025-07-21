package com.hsact.taxilog.ui.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hsact.taxilog.ui.CardTheme

@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CardTheme {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BaseCardPreview() {
    BaseCard {
        Column {
            Text(text = "Card Example")
            Text(text = "Card Example2")
        }
    }
}