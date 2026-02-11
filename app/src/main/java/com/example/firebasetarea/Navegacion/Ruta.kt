package com.example.firebasetarea.Navegacion

import kotlinx.serialization.Serializable

@Serializable
sealed class Rutas {

    @Serializable
    data object Login : Rutas()

    @Serializable
    data object Register : Rutas()

    @Serializable
    data object Home : Rutas()

    @Serializable
    data class DetallesProducto(
        val productoId: String,
        val nombre: String,
        val precio: Double,
        val descripcion: String,
        val imageUrl: String
    ) : Rutas()
}