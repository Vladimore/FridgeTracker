package com.example.fridgetracker.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

data class ProductFormState(
    val name: String = "",
    val imageUri: String? = null,
    val weight: String = "1",
    val quantity: String = "1",
    val barcode: String? = null
)

fun ProductFormState.isValid(): Boolean =
    name.trim().isNotEmpty() &&
    weight.toDoubleOrNull()?.let { it >= 0 } == true &&
    quantity.toIntOrNull()?.let { it >= 1 } == true

fun normalizeWeight(value: String): String =
    value.replace(',', '.').filter { it.isDigit() || it == '.' }.let {
        if (it.count { c -> c == '.' } <= 1) it else it.dropLastWhile { c -> c == '.' }
    }

fun normalizeQuantity(value: String): String =
    value.filter(Char::isDigit)

fun optimizeImage(context: Context, uri: Uri): Uri? {
    return try {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(input)
        input.close()
        if (bitmap == null) return null

        val maxSize = 128
        val scale = min(
            maxSize.toFloat() / bitmap.width,
            maxSize.toFloat() / bitmap.height
        ).coerceAtMost(1f)

        val width = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val height = (bitmap.height * scale).toInt().coerceAtLeast(1)
        val resized = Bitmap.createScaledBitmap(bitmap, width, height, true)

        val directory = File(context.filesDir, "product_images").apply { mkdirs() }
        val file = File(directory, "product_${System.currentTimeMillis()}.jpg")

        FileOutputStream(file).use {
            resized.compress(Bitmap.CompressFormat.JPEG, 85, it)
        }

        if (resized !== bitmap) resized.recycle()
        bitmap.recycle()

        Uri.fromFile(file)
    } catch (_: Exception) {
        null
    }
}
