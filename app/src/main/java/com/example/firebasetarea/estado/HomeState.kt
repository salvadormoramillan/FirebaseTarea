package com.example.firebasetarea.estado

import com.example.firebasetarea.data.Producto

data class HomeUiState(
    val productos: List<Producto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val productoEnEdicion: Producto? = null
)