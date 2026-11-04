package com.example.domino.domain.model

enum class GamePhase { WAITING, IN_PROGRESS, FINISHED }

/** Chain of tiles already placed, from left to right. */
data class Board(val chain: List<Tile> = emptyList()) {
    fun isEmpty(): Boolean = chain.isEmpty()
    /** Open value on the left end; null if the board is empty. */
    fun leftEnd(): Int? = chain.firstOrNull()?.left
    /** Open value on the right end; null if the board is empty. */
    fun rightEnd(): Int? = chain.lastOrNull()?.right
}

/**
 * COMPLETE game state. It is the only thing sent to Firestore:
 * document `games/{code}`. Every device observes it and the UI
 * is drawn only from it.
 */
data class GameState(
    val code: String = "",
    val hostId: String = "",
    val phase: GamePhase = GamePhase.WAITING,
    val players: List<Player> = emptyList(),
    val board: Board = Board(),
    val drawPile: List<Tile> = emptyList(),
    /** Index in [players] of the player whose turn it is. */
    val currentTurn: Int = 0,
    /** 1 = normal direction, -1 = reversed. */
    val direction: Int = 1,
    val round: Int = 1,
    val maxRounds: Int = 30,
    val activeEvent: ActiveEvent? = null,
    /** Id of the player who will lose their next turn because of a SKIP, if any. */
    val skippedPlayerId: String? = null,
    val winnerId: String? = null,
    /** Short message about the last thing that happened, shown in the UI ("Ana used Skip"). */
    val lastMessage: String = ""
) {
    fun currentPlayer(): Player? = players.getOrNull(currentTurn)
}

/** Summary saved in the history (mockup 11). */
data class GameSummary(
    val code: String = "",
    val dateMillis: Long = 0L,
    val won: Boolean = false,
    val score: Int = 0,
    val position: Int = 1,
    val playerCount: Int = 2
)