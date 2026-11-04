package com.example.domino.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domino.data.game.FirestoreGameRepository
import com.example.domino.data.game.GameRepository
import com.example.domino.domain.GameEngine
import com.example.domino.domain.GameEngineImpl
import com.example.domino.domain.MoveResult
import com.example.domino.domain.model.GamePhase
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.GameSummary
import com.example.domino.domain.model.Move
import com.example.domino.domain.model.PowerType
import com.example.domino.domain.model.Side
import com.example.domino.domain.model.Tile
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** The ONLY thing the game UI needs to read (Person B draws from this). */
data class GameUiState(
    val state: GameState? = null,
    val myId: String = "",
    val selectedTile: Tile? = null,
    val selectedPower: PowerType? = null,
    /** SWAP only: the opponent and the opponent's tile chosen for the exchange. */
    val swapTargetId: String? = null,
    val swapTargetTile: Tile? = null,
    /** Valid moves for me right now (empty if it is not my turn). Use it to highlight tiles/sides. */
    val validMoves: List<Move> = emptyList(),
    val isMyTurn: Boolean = false,
    val isHost: Boolean = false,
    val loading: Boolean = true,
    /** True after I left the room: the UI should navigate back to the menu. */
    val hasLeft: Boolean = false,
    /** Message to show once (Toast/Snackbar); call [GameViewModel.clearError] afterwards. */
    val error: String? = null
)

/**
 * Game screen logic (task A8). The UI only calls these functions and reads [uiState];
 * the rules live in [GameEngine] and the synchronization in [GameRepository].
 *
 * Every action runs in a Firestore transaction. We never edit [uiState] to reflect a move:
 * the new game state comes back through the real-time listener, for every player alike.
 */
class GameViewModel(
    private val repository: GameRepository = FirestoreGameRepository(),
    private val engine: GameEngine = GameEngineImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var roomCode: String? = null
    private var observeJob: Job? = null
    private var historySaved = false

    /** Starts listening to the room. Safe to call again with the same arguments. */
    fun connect(code: String, myId: String) {
        if (roomCode == code && _uiState.value.myId == myId && observeJob?.isActive == true) return
        roomCode = code
        historySaved = false
        observeJob?.cancel()
        _uiState.value = GameUiState(myId = myId)
        observeJob = viewModelScope.launch {
            repository.observeGame(code)
                .catch { e -> _uiState.update { it.copy(loading = false, error = e.message ?: "Connection lost") } }
                .collect { onRemoteState(it) }
        }
    }

    // region Selection (local, nothing is sent)

    fun selectTile(tile: Tile) {
        val ui = _uiState.value
        if (!ui.isMyTurn) return
        _uiState.update { it.copy(selectedTile = tile) }
    }

    /** Pick a power BEFORE moving; null clears it. */
    fun selectPower(power: PowerType?) {
        _uiState.update {
            it.copy(
                selectedPower = power,
                swapTargetId = if (power == PowerType.SWAP) it.swapTargetId else null,
                swapTargetTile = if (power == PowerType.SWAP) it.swapTargetTile else null
            )
        }
    }

    /** SWAP only: choose the opponent and which of their tiles you want. */
    fun selectSwapTarget(targetId: String, tile: Tile) {
        _uiState.update { it.copy(swapTargetId = targetId, swapTargetTile = tile) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // endregion

    // region Actions (sent to Firestore)

    /** Host only: leaves the waiting room and deals the tiles. */
    fun startGame() {
        val myId = _uiState.value.myId
        execute { state ->
            if (state.hostId != myId) MoveResult.Invalid("Only the host can start the game") else engine.start(state)
        }
    }

    /** Places the selected tile (with the selected power, if any) on [side]. */
    fun placeTile(side: Side) {
        val ui = _uiState.value
        val tile = ui.selectedTile ?: return fail("Select a tile first")
        val move = Move(ui.myId, tile, side, ui.selectedPower)
        execute { state -> afterMove(state, engine.applyMove(state, move)) }
    }

    /** SWAP: exchanges the selected tile with the chosen opponent's tile. */
    fun confirmSwap() {
        val ui = _uiState.value
        val tile = ui.selectedTile ?: return fail("Select one of your tiles first")
        val targetId = ui.swapTargetId ?: return fail("Choose an opponent")
        val targetTile = ui.swapTargetTile ?: return fail("Choose the opponent's tile")
        val move = Move(ui.myId, tile, power = PowerType.SWAP, targetId = targetId, targetTile = targetTile)
        execute { state -> afterMove(state, engine.applyMove(state, move)) }
    }

    /** No valid move: takes a tile from the pile, or passes the turn if the pile is empty. */
    fun drawFromPile() {
        val myId = _uiState.value.myId
        execute { state -> afterMove(state, engine.drawFromPile(state, myId)) }
    }

    /** Leaves the game. When it succeeds [GameUiState.hasLeft] becomes true. */
    fun abandon() {
        val myId = _uiState.value.myId
        execute(onDone = { _uiState.update { it.copy(hasLeft = true) } }) { state ->
            MoveResult.Success(engine.abandon(state, myId))
        }
    }

    /** Host only: same players, new game. */
    fun restart() {
        val myId = _uiState.value.myId
        execute { state ->
            if (state.hostId != myId) MoveResult.Invalid("Only the host can restart") else MoveResult.Success(engine.restart(state))
        }
    }

    // endregion

    // region Internals

    private fun fail(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    /**
     * Runs [transform] on the latest game state inside a transaction. Invalid results become an
     * error message for the user; valid ones are written and come back through the listener.
     */
    private fun execute(onDone: () -> Unit = {}, transform: (GameState) -> MoveResult) {
        val code = roomCode ?: return fail("Not connected to a room")
        viewModelScope.launch {
            repository.update(code) { state ->
                when (val result = transform(state)) {
                    is MoveResult.Success -> result.state
                    is MoveResult.Invalid -> throw IllegalStateException(result.reason)
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(selectedTile = null, selectedPower = null, swapTargetId = null, swapTargetTile = null, error = null)
                }
                onDone()
            }.onFailure { e ->
                fail(e.message ?: "Something went wrong")
            }
        }
    }

    /** A random event may trigger once per round: only when this move closed a round. */
    private fun afterMove(before: GameState, result: MoveResult): MoveResult =
        if (result is MoveResult.Success && result.state.round > before.round) {
            MoveResult.Success(engine.applyRandomEvent(result.state))
        } else {
            result
        }

    private fun onRemoteState(state: GameState?) {
        if (state == null) {
            _uiState.update { it.copy(state = null, loading = false, error = "This room no longer exists") }
            return
        }
        val myId = _uiState.value.myId
        val myTurn = state.phase == GamePhase.IN_PROGRESS && state.currentPlayer()?.id == myId
        val moves = if (myTurn) engine.validMoves(state, myId) else emptyList()
        val myHand = state.players.firstOrNull { it.id == myId }?.hand.orEmpty()

        _uiState.update { current ->
            // Drop the selection if it is no longer meaningful (not my turn, tile gone).
            val tile = current.selectedTile?.takeIf { myTurn && it in myHand }
            current.copy(
                state = state,
                isMyTurn = myTurn,
                isHost = state.hostId == myId,
                validMoves = moves,
                selectedTile = tile,
                selectedPower = if (tile != null) current.selectedPower else null,
                swapTargetId = if (tile != null) current.swapTargetId else null,
                swapTargetTile = if (tile != null) current.swapTargetTile else null,
                loading = false
            )
        }

        if (state.phase == GamePhase.FINISHED) saveHistoryOnce(state) else historySaved = false
    }

    /** Each player stores their own result once when the game finishes. */
    private fun saveHistoryOnce(state: GameState) {
        if (historySaved) return
        val myId = _uiState.value.myId
        val me = state.players.firstOrNull { it.id == myId } ?: return
        historySaved = true

        val ranking = state.players.filter { !it.abandoned }.sortedByDescending { it.score }
        val position = ranking.indexOfFirst { it.id == myId }.let { if (it < 0) state.players.size else it + 1 }
        val summary = GameSummary(
            code = state.code,
            dateMillis = System.currentTimeMillis(),
            won = state.winnerId == myId,
            score = me.score,
            position = position,
            playerCount = state.players.size
        )
        viewModelScope.launch { repository.saveToHistory(myId, summary) }
    }

    // endregion
}