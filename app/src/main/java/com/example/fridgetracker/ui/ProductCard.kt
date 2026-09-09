package com.example.fridgetracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fridgetracker.data.repository.ProductRepository
import com.example.fridgetracker.domain.model.Product
import kotlinx.coroutines.launch

// Светло-красный цвет кнопки "-"
private val MinusButtonColor = Color(0xFFFFCDD2)
private val MinusButtonIconColor = Color(0xFFB71C1C)

// Кнопка "-" занимает не более 15% ширины блока продукта
private const val MinusButtonWidthFraction = 0.15f
private const val ContentWidthFraction = 1f - MinusButtonWidthFraction

@Composable
fun ProductCard(
    product: Product,
    repository: ProductRepository,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxSize()
        ) {
            // Основная кликабельная область карточки (открывает редактирование)
            Row(
                modifier = Modifier
                    .weight(ContentWidthFraction)
                    .fillMaxHeight()
                    .clickable(onClick = onClick)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (product.imageUri != null) {
                        AsyncImage(
                            model = product.imageUri,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null
                        )
                    }
                }

                Spacer(
                    Modifier.width(14.dp)
                )

                Column(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        formatProductName(product.name),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        "Вес: ${formatWeight(product.weight)} ${product.weightUnit}"
                    )

                    Text(
                        "Остаток: ${formatQuantity(product.currentQuantity)} (${remainingPercent(product)}%)"
                    )
                }
            }

            // Кнопка уменьшения количества / удаления карточки
            Box(
                modifier = Modifier
                    .weight(MinusButtonWidthFraction)
                    .fillMaxHeight()
                    .background(MinusButtonColor)
                    .clickable {
                        scope.launch {
                            repository.decrementProduct(product)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "-",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MinusButtonIconColor
                )
            }
        }
    }
}

/**
 * Первая буква названия — заглавная, все остальные — строчные,
 * независимо от того, как ввёл пользователь.
 */
private fun formatProductName(name: String): String {
    if (name.isEmpty()) return name
    return name.take(1).uppercase() + name.drop(1).lowercase()
}

private fun formatWeight(value: Double): String =
    if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        value.toString()
    }

private fun formatQuantity(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else "%.2f".format(value).trimEnd('0').trimEnd('.')

private fun remainingPercent(product: Product): Int =
    if (product.initialQuantity <= 0.0) 0
    else ((product.currentQuantity / product.initialQuantity) * 100).toInt().coerceIn(0, 100)
