package com.example.domino.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LobbyUiState(
    val roomCode: String? = null,
    val loading: Boolean = false,
    val error: String? = null
)

/** Create/join a room. Person B wires it to the screens (B5); Person A implements it (A8). */
class LobbyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    fun createRoom() { TODO("A8") }
    fun join(code: String) { TODO("A8") }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun roomHandled() {
        _uiState.update { it.copy(roomCode = null) }
    }
}
