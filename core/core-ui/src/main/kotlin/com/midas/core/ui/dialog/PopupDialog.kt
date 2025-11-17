package com.midas.core.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.midas.core.ui.theme.sizing

@Composable
fun PopupDialog(
    title: String? = null,
    message: String,
    confirmButton: String,
    cancelButton: String? = null,
    onCancel: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.sizing.spaceLarge
            ),
            shape = RoundedCornerShape(
                MaterialTheme.sizing.spaceLarge
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.sizing.spaceLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                title?.let {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
                Text(
                    modifier = Modifier.padding(
                        top = MaterialTheme.sizing.spaceSmall,
                        bottom = MaterialTheme.sizing.spaceMedium,
                    ),
                    textAlign = TextAlign.Center,
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.sizing.spaceLarge
                    ),
                ) {
                    cancelButton?.let {
                        OutlinedButton(
                            modifier = Modifier.weight(0.5f),
                            onClick = {
                                onCancel?.invoke()
                                onDismiss?.invoke()
                            }
                        ) {
                            Text(text = it)
                        }
                    }

                    Button(
                        modifier = Modifier.weight(0.5f),
                        onClick = {
                            onConfirm()
                            onDismiss?.invoke()
                        }
                    ) {
                        Text(text = confirmButton)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PopupDialogPreview() {
    PopupDialog(
        message = "Connection error occurred.",
        confirmButton = "Ok",
        onConfirm = { /* no-op */ },
        onDismiss = { /* no-op */ },
    )
}

@Preview
@Composable
private fun CancellablePopupDialogPreview() {
    PopupDialog(
        title = "Logout",
        message = "Are you sure you want to log out?",
        confirmButton = "Ok",
        cancelButton = "Cancel",
        onConfirm = { /* no-op */ },
        onCancel = { /* no-op */ },
        onDismiss = { /* no-op */ },
    )
}
