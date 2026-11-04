package com.example.domino.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.Move
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** The ONLY thing the board UI needs to read (Person B draws from this). */
data class GameUiState(
    val state: GameState? = null,
    val myId: String = "",
    val selectedTile: Tile? = null,
    val selectedPower: PowerType? = null,
    val swapTargetId: String? = null,
    val swapTargetTile: Tile? = null,
    val validMoves: List<Move> = emptyList(),
    val isMyTurn: Boolean = false,
    val loading: Boolean = true,
    val error: String? = null
)

/**
 * Contract of the actions on the game screen.
 * Person B: call these functions from the UI and read [uiState].
 * Person A: fill in the bodies (A8) using GameEngine + GameRepository.
 */
class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun connect(code: String, myId: String) { TODO("A8") }
    fun startGame() { TODO("A8") }
    fun selectTile(tile: Tile) { TODO("A8") }
    fun selectPower(power: PowerType?) { TODO("A8") }
    fun selectSwapTarget(targetId: String, tile: Tile) { TODO("A8") }
    fun confirmSwap() { TODO("A8") }
    fun placeTile(side: Side) { TODO("A8") }
    fun drawFromPile() { TODO("A8") }
    fun abandon() { TODO("A8") }
    fun restart() { TODO("A8") }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
