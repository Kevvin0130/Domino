package com.example.domino.data.game

import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.GameSummary
import com.example.domino.domain.model.Player
import kotlinx.coroutines.flow.Flow

/**
 * CONTRACT for Firebase access (Person A implements; both people's ViewModels use it).
 * Suggested Firestore collections:
 *   games/{code}                       -> full GameState
 *   users/{uid}/history/{id}           -> GameSummary
 */
interface GameRepository {

    /** Creates the room and returns its 6-character code. */
    suspend fun createRoom(host: Player): Result<String>

    /** Joins an existing room that is in WAITING phase. */
    suspend fun joinRoom(code: String, player: Player): Result<Unit>

    /** Emits the state every time it changes in Firestore (null = the room does not exist). */
    fun observeGame(code: String): Flow<GameState?>

    /**
     * Reads the state, applies [transform] and writes it back, all in ONE transaction,
     * so two players do not overwrite each other. The GameEngine is called here.
     */
    suspend fun update(
        code: String,
        transform: (GameState) -> GameState
    ): Result<Unit>

    suspend fun saveToHistory(uid: String, summary: GameSummary): Result<Unit>

    fun observeHistory(uid: String): Flow<List<GameSummary>>
}