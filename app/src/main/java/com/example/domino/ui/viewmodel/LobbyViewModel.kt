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
    /** Set when a room was created/joined: navigate to the waiting room, then call [LobbyViewModel.roomHandled]. */
    val roomCode: String? = null,
    val loading: Boolean = false,
    /** Message to show once; call [LobbyViewModel.clearError] afterwards. */
    val error: String? = null
)

/**
 * Create / join a room (task A8). Person B wires it to the Create and Join screens (B5):
 * observe [uiState], navigate when [LobbyUiState.roomCode] is not null.
 */
class LobbyViewModel(
    private val repository: GameRepository = FirestoreGameRepository(),
    private val currentPlayer: () -> Player? = { currentPlayerOrNull() }
) : ViewModel() {

    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    fun createRoom() {
        val host = currentPlayer() ?: return fail("You need to sign in first")
        launchRequest { repository.createRoom(host) }
    }

    fun join(code: String) {
        val player = currentPlayer() ?: return fail("You need to sign in first")
        val clean = code.trim().uppercase()
        if (clean.length != ROOM_CODE_LENGTH) return fail("The code has $ROOM_CODE_LENGTH characters")
        launchRequest { repository.joinRoom(clean, player).map { clean } }
    }

    /** Call after navigating, so coming back to this screen does not navigate again. */
    fun roomHandled() {
        _uiState.update { it.copy(roomCode = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun launchRequest(request: suspend () -> Result<String>) {
        if (_uiState.value.loading) return
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            request()
                .onSuccess { code -> _uiState.update { it.copy(loading = false, roomCode = code) } }
                .onFailure { e -> _uiState.update { it.copy(loading = false, error = e.message ?: "Something went wrong") } }
        }
    }

    private fun fail(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    private companion object {
        const val ROOM_CODE_LENGTH = 6
    }
}