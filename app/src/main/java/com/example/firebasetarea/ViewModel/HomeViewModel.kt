package com.example.firebasetarea.ViewModel

import com.example.firebasetarea.data.Producto
import com.example.firebasetarea.estado.HomeUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



class HomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val productosCollection = firestore.collection("productos")

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        obtenerProductos()
    }



    fun establecerProductoParaEditar(producto: Producto) {
        _uiState.value = _uiState.value.copy(
            productoEnEdicion = producto
        )
    }

    fun cancelarEdicion() {
        _uiState.value = _uiState.value.copy(
            productoEnEdicion = null
        )
    }

    fun guardarProducto(nombre: String, precio: Double, descripcion: String, imageUrl: String) {
        if (nombre.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "El nombre no puede estar vacío"
            )
            return
        }

        if (precio <= 0) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "El precio debe ser mayor a 0"
            )
            return
        }

        val productoEnEdicion = _uiState.value.productoEnEdicion

        if (productoEnEdicion != null) {
            editarProducto(productoEnEdicion.id, nombre, precio, descripcion, imageUrl)
        } else {
            agregarProducto(nombre, precio, descripcion, imageUrl)
        }
    }

    private fun agregarProducto(nombre: String, precio: Double, descripcion: String, imageUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val producto = hashMapOf(
                    "nombre" to nombre,
                    "precio" to precio,
                    "descripcion" to descripcion,
                    "imageUrl" to imageUrl
                )

                productosCollection.add(producto).await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Producto agregado"
                )

                obtenerProductos()

                kotlinx.coroutines.delay(2000)
                limpiarMensajes()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    private fun editarProducto(id: String, nombre: String, precio: Double, descripcion: String, imageUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val producto = hashMapOf(
                    "nombre" to nombre,
                    "precio" to precio,
                    "descripcion" to descripcion,
                    "imageUrl" to imageUrl
                )

                productosCollection.document(id).set(producto).await()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Producto actualizado",
                    productoEnEdicion = null
                )

                obtenerProductos()

                kotlinx.coroutines.delay(2000)
                limpiarMensajes()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al editar: ${e.message}"
                )
            }
        }
    }

    fun obtenerProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val snapshot = productosCollection.get().await()

                val productos = snapshot.documents.mapNotNull { doc ->
                    Producto(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        precio = doc.getDouble("precio") ?: 0.0,
                        descripcion = doc.getString("descripcion") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                }

                _uiState.value = _uiState.value.copy(
                    productos = productos,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar: ${e.message}"
                )
            }
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            try {
                productosCollection.document(productoId).delete().await()
                obtenerProductos()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al eliminar: ${e.message}"
                )
            }
        }
    }

    private fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}