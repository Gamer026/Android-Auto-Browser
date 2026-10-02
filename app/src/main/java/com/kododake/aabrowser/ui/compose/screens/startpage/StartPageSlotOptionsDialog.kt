package com.kododake.aabrowser.ui.compose.screens.startpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kododake.aabrowser.R

@Composable
fun StartPageSlotOptionsDialog(
    slotIndex: Int,
    currentUrl: String,
    onOpen: () -> Unit,
    onSaveUrl: (String) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showRemoveConfirm by remember { mutableStateOf(false) }

    when {
        showEditDialog -> {
            var draftUrl by remember(currentUrl) { mutableStateOf(currentUrl) }
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                title = { Text(stringResource(R.string.start_page_slot_edit_title)) },
                text = {
                    OutlinedTextField(
                        value = draftUrl,
                        onValueChange = { draftUrl = it },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val trimmed = draftUrl.trim()
                            if (trimmed.isNotEmpty()) {
                                onSaveUrl(trimmed)
                                onDismiss()
                            }
                            showEditDialog = false
                        }
                    ) {
                        Text(stringResource(android.R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            )
        }

        showRemoveConfirm -> {
            AlertDialog(
                onDismissRequest = { showRemoveConfirm = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                title = { Text(stringResource(R.string.start_page_slot_remove_confirm_title)) },
                text = { Text(stringResource(R.string.start_page_slot_remove_confirm_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onRemove()
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.start_page_slot_remove))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRemoveConfirm = false }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            )
        }

        else -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                title = {
                    Text(stringResource(R.string.start_page_slot_options_title, slotIndex + 1))
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            currentUrl,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = {
                            onOpen()
                            onDismiss()
                        }) {
                            Text(stringResource(R.string.start_page_slot_open))
                        }
                        TextButton(onClick = { showEditDialog = true }) {
                            Text(stringResource(R.string.start_page_slot_edit))
                        }
                        TextButton(onClick = { showRemoveConfirm = true }) {
                            Text(
                                stringResource(R.string.start_page_slot_remove),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            )
        }
    }
}
