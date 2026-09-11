package com.example.fridgetracker.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
internal fun BarcodeScannerDialog(
    onBarcodeDetected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Сканировать штрихкод") },
        text = {
            if (!hasPermission) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Разрешите доступ к камере, чтобы распознать штрихкод.")
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Разрешить камеру")
                    }
                }
            } else {
                ScannerPreview(
                    lifecycleOwner = lifecycleOwner,
                    onBarcodeDetected = onBarcodeDetected
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@Composable
private fun ScannerPreview(
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val handled = remember { AtomicBoolean(false) }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val scanner = BarcodeScanning.getClient()
            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage == null || handled.get()) {
                    imageProxy.close()
                } else {
                    scanner.process(
                        InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                    ).addOnSuccessListener { barcodes ->
                        val value = barcodes.firstOrNull()?.rawValue
                        if (!value.isNullOrBlank() && handled.compareAndSet(false, true)) {
                            onBarcodeDetected(value)
                        }
                    }.addOnCompleteListener {
                        imageProxy.close()
                    }
                }
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            } catch (_: Exception) {
                analysis.clearAnalyzer()
            }
        }
        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProviderFuture.get().unbindAll()
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(top = 8.dp)
        )
        if (!handled.get()) {
            Text("Наведите камеру на штрихкод", modifier = Modifier.align(Alignment.BottomCenter))
        } else {
            CircularProgressIndicator()
        }
    }
}
