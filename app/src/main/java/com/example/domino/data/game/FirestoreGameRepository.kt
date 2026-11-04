package com.example.domino.data.game

import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.GameSummary
import com.example.domino.domain.model.Player
import kotlinx.coroutines.flow.Flow

/** PERSON A: task A7. Implement with FirebaseFirestore + snapshot listeners (callbackFlow). */
class FirestoreGameRepository : GameRepository {
    override suspend fun createRoom(host: Player): Result<String> = TODO("A7")
    override suspend fun joinRoom(code: String, player: Player): Result<Unit> = TODO("A7")
    override fun observeGame(code: String): Flow<GameState?> = TODO("A7")
    override suspend fun update(code: String, transform: (GameState) -> GameState): Result<Unit> = TODO("A7")
    override suspend fun saveToHistory(uid: String, summary: GameSummary): Result<Unit> = TODO("A7")
    override fun observeHistory(uid: String): Flow<List<GameSummary>> = TODO("A7")
}