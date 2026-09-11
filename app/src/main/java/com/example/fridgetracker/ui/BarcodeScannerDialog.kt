package com.example.fridgetracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
internal fun BarcodeScannerDialog(
    onBarcodeDetected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        GmsBarcodeScanning.getClient(context)
            .startScan()
            .addOnSuccessListener { result ->
                result.rawValue
                    ?.takeIf { it.isNotBlank() }
                    ?.let(onBarcodeDetected)
            }
            .addOnCanceledListener(onDismiss)
            .addOnFailureListener { onDismiss() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Сканирование штрихкода") },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Открывается Google Code Scanner...")
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
