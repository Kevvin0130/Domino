package com.example.domino.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.domino.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val cargando: Boolean = false,
    /** Mensaje para mostrar una sola vez (Toast); luego se llama [AuthViewModel.mensajeMostrado]. */
    val mensaje: String? = null,
    /** true cuando el login/registro fue exitoso: navegar al menú y llamar [AuthViewModel.navegacionManejada]. */
    val autenticado: Boolean = false
)

/**
 * B1: lógica de autenticación fuera de AppNavigation (MVVM).
 * La UI solo llama estas funciones y observa [uiState].
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun haySesion(): Boolean = repository.haySesion()

    fun usuarioId(): String = repository.usuarioId()

    fun iniciarSesion(correo: String, contrasena: String) {
        if (_uiState.value.cargando) return
        _uiState.update { it.copy(cargando = true, mensaje = null) }
        repository.iniciarSesion(correo, contrasena) { exitoso, mensaje ->
            _uiState.update {
                it.copy(
                    cargando = false,
                    autenticado = exitoso,
                    mensaje = if (exitoso) null else mensaje ?: "No se pudo iniciar sesión."
                )
            }
        }
    }

    fun registrar(nombre: String, correo: String, contrasena: String) {
        if (_uiState.value.cargando) return
        _uiState.update { it.copy(cargando = true, mensaje = null) }
        repository.registrar(nombre, correo, contrasena) { exitoso, mensaje ->
            _uiState.update {
                it.copy(
                    cargando = false,
                    autenticado = exitoso,
                    // En éxito, el mensaje (si existe) es una advertencia, p. ej. "no se pudo guardar el nombre".
                    mensaje = mensaje ?: if (exitoso) null else "Error al registrar la cuenta"
                )
            }
        }
    }

    fun recuperarContrasena(correo: String) {
        repository.recuperarContrasena(correo) { _, mensaje ->
            _uiState.update { it.copy(mensaje = mensaje ?: "Solicitud procesada.") }
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        _uiState.value = AuthUiState()
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun navegacionManejada() {
        _uiState.update { it.copy(autenticado = false) }
    }
}
