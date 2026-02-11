package com.example.firebasetarea.ViewModel

import androidx.lifecycle.ViewModel
import com.example.firebasetarea.estado.LoginState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow< LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Campos vacíos")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loginState.value = LoginState.Success
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error(it.message ?: "Error desconocido")
            }
    }
}
