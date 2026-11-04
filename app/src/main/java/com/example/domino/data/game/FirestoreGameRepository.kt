package com.example.domino.data.game

import com.example.domino.domain.GameEngine
import com.example.domino.domain.GameEngineImpl
import com.example.domino.domain.MoveResult
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.GameSummary
import com.example.domino.domain.model.Player
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

/**
 * Firestore implementation of [GameRepository] (task A7).
 *
 * Data layout:
 *   games/{code}                         -> the whole GameState (see GameMapper)
 *   users/{uid}/history/{code_date}      -> one GameSummary per finished game
 *
 * Every change to a game goes through a TRANSACTION (read -> apply rules -> write), so two
 * players acting at the same time never overwrite each other. Rule violations are reported
 * as a failed [Result] whose message can be shown to the user.
 */
class FirestoreGameRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val engine: GameEngine = GameEngineImpl()
) : GameRepository {

    private val games get() = db.collection(GAMES)

    private fun history(uid: String) = db.collection(USERS).document(uid).collection(HISTORY)

    override suspend fun createRoom(host: Player): Result<String> = runCatching {
        repeat(MAX_CODE_ATTEMPTS) {
            val code = generateCode()
            val ref = games.document(code)
            val created = db.runTransaction<Boolean> { tx ->
                if (tx.get(ref).exists()) {
                    false
                } else {
                    tx.set(ref, engine.createGame(code, host).toData())
                    true
                }
            }.await()
            if (created) return@runCatching code
        }
        error("Could not find a free room code, try again")
    }

    override suspend fun joinRoom(code: String, player: Player): Result<Unit> =
        runOnGame(code.trim().uppercase()) { state ->
            if (state.players.any { it.id == player.id }) {
                state // already inside: simply reconnect
            } else {
                when (val result = engine.addPlayer(state, player)) {
                    is MoveResult.Success -> result.state
                    is MoveResult.Invalid -> throw IllegalStateException(result.reason)
                }
            }
        }

    override fun observeGame(code: String): Flow<GameState?> = callbackFlow {
        val registration = games.document(code).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val data = snapshot?.data
            trySend(if (data == null) null else runCatching { data.toGameState() }.getOrNull())
        }
        awaitClose { registration.remove() }
    }

    override suspend fun update(
        code: String,
        transform: (GameState) -> GameState
    ): Result<Unit> = runOnGame(code, transform)

    override suspend fun saveToHistory(uid: String, summary: GameSummary): Result<Unit> = runCatching {
        history(uid).document("${summary.code}_${summary.dateMillis}").set(summary.toData()).await()
        Unit
    }

    override fun observeHistory(uid: String): Flow<List<GameSummary>> = callbackFlow {
        val registration = history(uid)
            .orderBy("dateMillis", Query.Direction.DESCENDING)
            .limit(HISTORY_LIMIT)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.data?.toGameSummary() } ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Reads the game, applies [block] and writes the result, atomically.
     * If [block] throws (e.g. an invalid move) nothing is written and the failure carries its message.
     */
    private suspend fun runOnGame(code: String, block: (GameState) -> GameState): Result<Unit> = runCatching {
        val ref = games.document(code)
        val rejection = db.runTransaction<String?> { tx ->
            val snapshot = tx.get(ref)
            val data = snapshot.data
            if (!snapshot.exists() || data == null) {
                "Room not found"
            } else {
                try {
                    tx.set(ref, block(data.toGameState()).toData())
                    null
                } catch (e: Exception) {
                    e.message ?: "Invalid operation"
                }
            }
        }.await()
        if (rejection != null) throw IllegalStateException(rejection)
    }

    /** 6 characters, without easily confused ones (0/O, 1/I). */
    private fun generateCode(): String =
        List(CODE_LENGTH) { CODE_ALPHABET.random(Random.Default) }.joinToString("")

    private companion object {
        const val GAMES = "games"
        const val USERS = "users"
        const val HISTORY = "history"
        const val CODE_LENGTH = 6
        const val CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        const val MAX_CODE_ATTEMPTS = 5
        const val HISTORY_LIMIT = 50L
    }
}