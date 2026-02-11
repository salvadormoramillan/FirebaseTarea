package com.example.firebasetarea.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.firebasetarea.ViewModel.HomeViewModel
import com.example.firebasetarea.data.Producto
import com.google.firebase.auth.FirebaseAuth




@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToDetalles: (Producto) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val userName = FirebaseAuth.getInstance()
        .currentUser
        ?.email
        ?.substringBefore("@")

    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productImageUrl by remember { mutableStateOf("") }

    LaunchedEffect(uiState.productoEnEdicion) {
        uiState.productoEnEdicion?.let { producto ->
            productName = producto.nombre
            productPrice = producto.precio.toString()
            productDescription = producto.descripcion
            productImageUrl = producto.imageUrl
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text(
                text = if (!userName.isNullOrBlank())
                    "Bienvenido/a, $userName"
                else
                    "Bienvenido/a",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.productoEnEdicion != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Editando producto",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        TextButton(
                            onClick = {
                                viewModel.cancelarEdicion()
                                productName = ""
                                productPrice = ""
                                productDescription = ""
                                productImageUrl = ""
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }

        item {
            OutlinedTextField(
                value = productImageUrl,
                onValueChange = { productImageUrl = it },
                label = { Text("URL de la imagen") },
                placeholder = { Text("https://ejemplo.com/imagen.jpg") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
        }

        item {
            Button(
                onClick = {
                    val precio = productPrice.toDoubleOrNull() ?: 0.0
                    viewModel.guardarProducto(
                        productName,
                        precio,
                        productDescription,
                        productImageUrl
                    )
                    productName = ""
                    productPrice = ""
                    productDescription = ""
                    productImageUrl = ""
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        if (uiState.productoEnEdicion != null)
                            "Guardar cambios"
                        else
                            "Añadir producto"
                    )
                }
            }
        }

        uiState.errorMessage?.let {
            item {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }

        uiState.successMessage?.let {
            item {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.productos.isEmpty() && !uiState.isLoading) {
            item {
                Text(
                    text = "No hay productos agregados",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(uiState.productos) { producto ->
                ProductoCard(
                    producto = producto,
                    onShowDetails = { onNavigateToDetalles(producto) },
                    onEdit = { viewModel.establecerProductoParaEditar(producto) },
                    onDelete = { viewModel.eliminarProducto(producto.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onShowDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", producto.precio)}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )

            }

            Row {
                IconButton(onClick = onShowDetails) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ver detalles",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar producto",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar producto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}