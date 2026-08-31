package com.hsact.taxilog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsact.taxilog.R

/**
 * A dialog for users to enter feedback or report a bug.
 *
 * @param onDismiss Callback to close the dialog.
 * @param onSend Callback when the send button is clicked with the entered message.
 * @param isSending Whether the feedback is currently being sent.
 */
@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    isSending: Boolean = false
) {
    var message by remember { mutableStateOf("") }
    val maxChars = 1000

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.feedback_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.feedback_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { if (it.length <= maxChars) message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text(text = stringResource(R.string.feedback_placeholder)) },
                    enabled = !isSending,
                    supportingText = {
                        Text(
                            text = "${message.length} / $maxChars",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
        },
        confirmButton = {
            val trimmedMessage = message.trim()
            TextButton(
                onClick = {
                    if (trimmedMessage.isNotBlank()) {
                        onSend(trimmedMessage)
                    }
                },
                enabled = trimmedMessage.isNotBlank() && !isSending
            ) {
                if (isSending) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.padding(start = 8.dp))
                        Text(text = stringResource(R.string.feedback_send))
                    }
                } else {
                    Text(text = stringResource(R.string.feedback_send))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSending
            ) {
                Text(text = stringResource(R.string.feedback_cancel))
            }
        }
    )
}
