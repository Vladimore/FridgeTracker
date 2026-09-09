package com.example.fridgetracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fridgetracker.data.repository.ProductRepository
import com.example.fridgetracker.domain.model.Product
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(repository: ProductRepository) {
    val context = LocalContext.current
    val products by repository
        .observeProducts()
        .collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    var query by remember {
        mutableStateOf("")
    }

    var showAdd by remember {
        mutableStateOf(false)
    }

    var editingProduct by remember {
        mutableStateOf<Product?>(null)
    }

    val filtered = products.filter {
        it.name.contains(
            query.trim(),
            ignoreCase = true
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                },
                modifier = Modifier.weight(4f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = {
                    Text("Поиск...")
                }
            )

            FilledTonalIconButton(
                onClick = {
                    showAdd = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Добавить продукт"
                )
            }
        }

        Spacer(
            Modifier.height(10.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(
                filtered,
                key = { it.id }
            ) { product ->
                ProductCard(
                    product = product,
                    repository = repository
                ) {
                    editingProduct = product
                }
            }
        }
    }

    if (showAdd) {
        ProductDialog(
            title = "Добавить продукт",
            initial = ProductFormState(),
            confirmText = "Добавить",
            onDismiss = {
                showAdd = false
            },
            onConfirm = { form ->
                scope.launch {
                    repository.addProduct(
                        Product(
                            name = form.name.trim(),
                            imageUri = form.imageUri,
                            weight = form.weight
                                .replace(',', '.')
                                .toDouble(),
                            initialQuantity = form.quantity.replace(',', '.').toDouble(),
                            currentQuantity = form.quantity.replace(',', '.').toDouble(),
                            expirationDate = null
                        )
                    )
                }
            },
            afterAddMode = true,
            repository = repository
        )
    }

    editingProduct?.let { product ->
        ProductDialog(
            title = "Редактировать продукт",
            initial = ProductFormState(
                name = product.name,
                imageUri = product.imageUri,
                weight = product.weight.toString(),
                quantity = product.currentQuantity.toString()
            ),
            confirmText = "Сохранить",
            onDismiss = {
                editingProduct = null
            },
            onConfirm = { form ->
                scope.launch {
                    repository.updateProduct(
                        product.copy(
                            name = form.name.trim(),
                            imageUri = form.imageUri,
                            weight = form.weight
                                .replace(',', '.')
                                .toDouble(),
                            initialQuantity = product.initialQuantity,
                            currentQuantity = form.quantity.replace(',', '.').toDouble()
                        )
                    )
                }

                editingProduct = null
            },
            onDelete = {
                scope.launch {
                    repository.deleteProduct(product)
                }

                editingProduct = null
            },
            repository = repository
        )
    }
}
