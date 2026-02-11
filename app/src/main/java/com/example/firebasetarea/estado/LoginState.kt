package com.example.firebasetarea.estado

sealed class LoginState {
    data object Idle : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
