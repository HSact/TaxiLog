package com.hsact.taxilog.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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