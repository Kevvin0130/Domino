package com.example.domino.domain

import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.Move
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.Tile
import kotlin.random.Random

/** Result of an engine operation: the new state or the reason it was rejected. */
sealed interface MoveResult {
    data class Success(val state: GameState) : MoveResult
    data class Invalid(val reason: String) : MoveResult
}

/**
 * CONTRACT of the game logic (Person A implements it; Person B uses it
 * through the ViewModels). Rules:
 *  - Pure Kotlin: no Android, Firebase or Compose in the `domain` package.
 *  - Pure functions: they receive a state and return a NEW one (immutable).
 *    This makes them easy to test and to run inside a Firestore transaction.
 */
interface GameEngine {

    /** Initial state in WAITING phase with only the host. */
    fun createGame(code: String, host: Player): GameState

    /** Adds a player to a game in WAITING phase. */
    fun addPlayer(state: GameState, player: Player): MoveResult

    /** Goes from WAITING to IN_PROGRESS: shuffles, deals 7 tiles each, builds the pile and sets the first turn. */
    fun start(state: GameState, random: Random = Random.Default): MoveResult

    /** All valid moves for the player (to highlight tiles in the UI). */
    fun validMoves(state: GameState, playerId: String): List<Move>

    /** Validates and applies a move (including powers), updates the score and advances the turn. */
    fun applyMove(state: GameState, move: Move): MoveResult

    /** No valid move: draws a tile from the pile. If the pile is empty, the player loses the turn. */
    fun drawFromPile(state: GameState, playerId: String): MoveResult

    /** Generates a random event (or none) and applies it to the state. */
    fun applyRandomEvent(state: GameState, random: Random = Random.Default): GameState

    /** Marks a player as having abandoned and resolves whether the game must end. */
    fun abandon(state: GameState, playerId: String): GameState

    /** Restarts the game with the same players (back to WAITING or straight to IN_PROGRESS). */
    fun restart(state: GameState, random: Random = Random.Default): GameState

    /**
     * Detects the end of the game: someone has no tiles, nobody can move, or
     * maxRounds was reached. If it ended, returns the state with phase FINISHED
     * and winnerId (tie-breaker: fewer powers used).
     */
    fun evaluateEnd(state: GameState): GameState

    /** Points this tile is worth when placed (takes active events into account). */
    fun calculatePoints(state: GameState, tile: Tile): Int
}