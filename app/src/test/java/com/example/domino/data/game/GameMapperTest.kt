package com.example.domino.data.game

import com.example.domino.domain.GameEngineImpl
import com.example.domino.domain.MoveResult
import com.example.domino.domain.model.ActiveEvent
import com.example.domino.domain.model.EventType
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.GameSummary
import com.example.domino.domain.model.Player
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class GameMapperTest {

    private val engine = GameEngineImpl()

    private fun startedGame(): GameState {
        var state = engine.createGame("ABC234", Player(id = "u1", name = "Ana"))
        state = (engine.addPlayer(state, Player(id = "u2", name = "Beto")) as MoveResult.Success).state
        return (engine.start(state, Random(7)) as MoveResult.Success).state
    }

    /** Firestore hands back every integer as Long; simulate that before mapping back. */
    private fun asFirestoreWould(value: Any?): Any? = when (value) {
        is Int -> value.toLong()
        is Map<*, *> -> value.entries.associate { it.key to asFirestoreWould(it.value) }
        is List<*> -> value.map { asFirestoreWould(it) }
        else -> value
    }

    @Test
    fun gameState_survivesTheRoundTrip() {
        val state = startedGame().copy(
            activeEvent = ActiveEvent(EventType.DOUBLE_SCORE, 2),
            skippedPlayerId = "u2",
            winnerId = null,
            lastMessage = "Ana placed 6-1"
        )

        @Suppress("UNCHECKED_CAST")
        val stored = asFirestoreWould(state.toData()) as Map<String, Any?>

        assertEquals(state, stored.toGameState())
    }

    @Test
    fun waitingRoom_survivesTheRoundTrip() {
        val state = engine.createGame("ABC234", Player(id = "u1", name = "Ana"))

        @Suppress("UNCHECKED_CAST")
        val stored = asFirestoreWould(state.toData()) as Map<String, Any?>

        assertEquals(state, stored.toGameState())
    }

    @Test
    fun missingFields_fallBackToDefaults() {
        val state = emptyMap<String, Any?>().toGameState()
        assertEquals(GameState(), state)
    }

    @Test
    fun summary_survivesTheRoundTrip() {
        val summary = GameSummary("ABC234", 1_762_000_000_000L, true, 120, 1, 3)

        @Suppress("UNCHECKED_CAST")
        val stored = asFirestoreWould(summary.toData()) as Map<String, Any?>

        assertEquals(summary, stored.toGameSummary())
    }
}