package com.example.fridgetracker.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.fridgetracker.data.repository.ProductRepository

@Composable
internal fun ProductDialog(
    title: String,
    initial: ProductFormState,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (ProductFormState) -> Unit,
    onDelete: (() -> Unit)? = null,
    afterAddMode: Boolean = false,
    repository: ProductRepository
) {
    val context = LocalContext.current

    var form by remember(initial) {
        mutableStateOf(initial)
    }

    var nameError by remember {
        mutableStateOf(false)
    }

    var weightError by remember {
        mutableStateOf(false)
    }

    var quantityError by remember {
        mutableStateOf(false)
    }

    var showAdded by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirm by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val optimized = optimizeImage(
                context,
                uri
            )

            if (optimized != null) {
                form = form.copy(
                    imageUri = optimized.toString()
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
            },
            title = {
                Text("Удалить карточку?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete?.invoke()
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Нет")
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = form.name,
                    onValueChange = {
                        form = form.copy(name = it)
                        nameError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nameError,
                    shape = RoundedCornerShape(14.dp),
                    label = {
                        Text("Название *")
                    },
                    supportingText = if (nameError) {
                        {
                            Text("Укажите название продукта")
                        }
                    } else {
                        null
                    }
                )

                OutlinedButton(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        if (form.imageUri == null) {
                            "Добавить изображение"
                        } else {
                            "Заменить изображение"
                        }
                    )
                }

                OutlinedTextField(
                    value = form.weight,
                    onValueChange = {
                        form = form.copy(
                            weight = normalizeWeight(it)
                        )

                        weightError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = weightError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    shape = RoundedCornerShape(14.dp),
                    label = {
                        Text("Вес, кг")
                    },
                    supportingText = if (weightError) {
                        {
                            Text("Введите число не меньше 0")
                        }
                    } else {
                        null
                    }
                )

                OutlinedTextField(
                    value = form.quantity,
                    onValueChange = {
                        form = form.copy(
                            quantity = normalizeQuantity(it)
                        )

                        quantityError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = quantityError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(14.dp),
                    label = {
                        Text("Количество")
                    },
                    supportingText = if (quantityError) {
                        {
                            Text("Введите целое число от 1")
                        }
                    } else {
                        null
                    }
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onDelete != null) {
                        Button(
                            onClick = {
                                showDeleteConfirm = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Удалить")
                        }
                    }

                    Button(
                        onClick = {
                            nameError = form.name
                                .trim()
                                .isEmpty()

                            weightError =
                                form.weight
                                    .replace(',', '.')
                                    .toDoubleOrNull()
                                    ?.let { it < 0 } != false

                            quantityError =
                                form.quantity
                                    .toIntOrNull()
                                    ?.let { it < 1 } != false

                            if (!nameError &&
                                !weightError &&
                                !quantityError
                            ) {
                                if (afterAddMode) {
                                    onConfirm(form)
                                    showAdded = true
                                } else {
                                    onConfirm(form)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(confirmText)
                    }
                }
            }
        }
    }

    if (showAdded) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("Продукт добавлен")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAdded = false
                        form = ProductFormState(
                            name = "",
                            imageUri = null,
                            weight = "1",
                            quantity = "1"
                        )
                    }
                ) {
                    Text("Продолжить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAdded = false
                        onDismiss()
                    }
                ) {
                    Text("Закончить")
                }
            }
        )
    }
}
