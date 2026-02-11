package com.example.firebasetarea.Navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.firebasetarea.Screen.DetallesProductoScreen
import com.example.firebasetarea.Screen.HomeScreen
import com.example.firebasetarea.Screen.LoginScreen
import com.example.firebasetarea.Screen.RegisterScreen
import com.example.firebasetarea.data.Producto

@Composable
fun Navegacion() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.Login
    ) {

        composable<Rutas.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Rutas.Home) {
                        popUpTo(Rutas.Login) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Rutas.Register)
                }
            )
        }

        composable<Rutas.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Rutas.Home) {
                        popUpTo(Rutas.Register) { inclusive = true }
                    }
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Rutas.Home> {
            HomeScreen(
                onNavigateToDetalles = { producto ->
                    navController.navigate(
                        Rutas.DetallesProducto(
                            productoId = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            descripcion = producto.descripcion,
                            imageUrl = producto.imageUrl
                        )
                    )
                }
            )
        }

        composable<Rutas.DetallesProducto> { backStackEntry ->
            val detalles = backStackEntry.toRoute<Rutas.DetallesProducto>()
            val producto = Producto(
                id = detalles.productoId,
                nombre = detalles.nombre,
                precio = detalles.precio,
                descripcion = detalles.descripcion,
                imageUrl = detalles.imageUrl   )

            DetallesProductoScreen(
                producto = producto,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

    }
}