package com.example.domino.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domino.data.auth.currentPlayerOrNull
import com.example.domino.data.game.FirestoreGameRepository
import com.example.domino.data.game.GameRepository
import com.example.domino.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LobbyUiState(
    val isLoading: Boolean = false,
    val roomCode: String? = null,
    val error: String? = null
)

class LobbyViewModel(
    private val repository: GameRepository = FirestoreGameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    private val _roomCode = MutableStateFlow<String?>(null)
    val roomCode: StateFlow<String?> = _roomCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createRoom(onCreated: ((String) -> Unit)? = null) {
        val player = currentPlayerOrNull() ?: Player(id = "anon", name = "Jugador")
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.update { it.copy(isLoading = true, error = null) }
            _error.value = null

            val result = repository.createRoom(player)
            result.onSuccess { code ->
                _roomCode.value = code
                _isLoading.value = false
                _uiState.update { it.copy(isLoading = false, roomCode = code) }
                onCreated?.invoke(code)
            }.onFailure { ex ->
                val msg = ex.localizedMessage ?: "No se pudo crear la sala"
                _error.value = msg
                _isLoading.value = false
                _uiState.update { it.copy(isLoading = false, error = msg) }
            }
        }
    }

    fun joinRoom(code: String, onResult: ((Boolean) -> Unit)? = null) {
        val player = currentPlayerOrNull() ?: Player(id = "anon", name = "Jugador")
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.update { it.copy(isLoading = true, error = null) }
            _error.value = null

            val result = repository.joinRoom(code.trim().uppercase(), player)
            result.onSuccess {
                _roomCode.value = code
                _isLoading.value = false
                _uiState.update { it.copy(isLoading = false, roomCode = code) }
                onResult?.invoke(true)
            }.onFailure { ex ->
                val msg = ex.localizedMessage ?: "No se pudo unirse a la sala"
                _error.value = msg
                _isLoading.value = false
                _uiState.update { it.copy(isLoading = false, error = msg) }
                onResult?.invoke(false)
            }
        }
    }

    fun resetState() {
        _roomCode.value = null
        _isLoading.value = false
        _error.value = null
        _uiState.value = LobbyUiState()
    }
}
