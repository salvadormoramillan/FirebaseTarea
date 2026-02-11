package com.example.firebasetarea.ViewModel

import androidx.lifecycle.ViewModel
import com.example.firebasetarea.estado.RegisterState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    fun register(email: String, password: String, repeatPassword: String) {

        if (email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            _state.value = RegisterState.Error("Completa todos los campos")
            return
        }

        if (password != repeatPassword) {
            _state.value = RegisterState.Error("Las contraseñas no coinciden")
            return
        }

        _state.value = RegisterState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _state.value = RegisterState.Success
            }
            .addOnFailureListener {
                _state.value =
                    RegisterState.Error(it.message ?: "Error al registrarse")
            }
    }
}
