package com.example.todocompose.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.todocompose.R

@Composable
fun DisplayAlertDialog(
    title: String,
    message: String,
    openDialog: Boolean,
    closeDialog: () -> Unit,
    onYesClicked: () -> Unit
) {
    if (openDialog) {
        AlertDialog(title = {
            Text(
                text = title,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.Bold
            )
        }, text = {
            Text(
                text = message, fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.Normal
            )
        }, confirmButton = {
            Button(onClick = {
                onYesClicked()
                closeDialog()
            }) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
            dismissButton = {
                OutlinedButton(onClick = {
                    closeDialog()
                }) {
                    Text(text = stringResource(id = R.string.no))
                }
            },
            onDismissRequest = {
                closeDialog()
            }
        )
    }
}