package com.example.domino.data.game

import com.example.domino.domain.model.ActiveEvent
import com.example.domino.domain.model.Board
import com.example.domino.domain.model.EventType
import com.example.domino.domain.model.GamePhase
import com.example.domino.domain.model.GameState
import com.example.domino.domain.model.GameSummary
import com.example.domino.domain.model.Player
import com.example.domino.domain.model.Tile

/**
 * Conversion between the domain models and plain maps/lists that Firestore stores.
 *
 * It is written by hand (instead of letting Firestore reflect over the classes) so the
 * domain stays free of Firebase annotations and so it can be unit tested on the JVM.
 * Firestore returns every integer as Long, so numbers are read through [Number].
 */

// region Domain -> Firestore

internal fun Tile.toData(): Map<String, Any?> = mapOf("left" to left, "right" to right)

internal fun Player.toData(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "hand" to hand.map { it.toData() },
    "score" to score,
    "powerUses" to powerUses,
    "powersUsed" to powersUsed,
    "abandoned" to abandoned
)

internal fun GameState.toData(): Map<String, Any?> = mapOf(
    "code" to code,
    "hostId" to hostId,
    "phase" to phase.name,
    "players" to players.map { it.toData() },
    // Handy to query "games where I play" later; not read back.
    "playerIds" to players.map { it.id },
    "board" to board.chain.map { it.toData() },
    "drawPile" to drawPile.map { it.toData() },
    "currentTurn" to currentTurn,
    "direction" to direction,
    "round" to round,
    "maxRounds" to maxRounds,
    "activeEvent" to activeEvent?.let { mapOf("type" to it.type.name, "roundsLeft" to it.roundsLeft) },
    "skippedPlayerId" to skippedPlayerId,
    "winnerId" to winnerId,
    "lastMessage" to lastMessage
)

internal fun GameSummary.toData(): Map<String, Any?> = mapOf(
    "code" to code,
    "dateMillis" to dateMillis,
    "won" to won,
    "score" to score,
    "position" to position,
    "playerCount" to playerCount
)

// endregion

// region Firestore -> Domain

internal fun Map<String, Any?>.toTile(): Tile = Tile(int("left"), int("right"))

internal fun Map<String, Any?>.toPlayer(): Player = Player(
    id = str("id"),
    name = str("name"),
    hand = mapList("hand").map { it.toTile() },
    score = int("score"),
    powerUses = (this["powerUses"] as? Map<*, *>)
        ?.entries
        ?.associate { it.key.toString() to ((it.value as? Number)?.toInt() ?: 0) }
        ?: Player().powerUses,
    powersUsed = int("powersUsed"),
    abandoned = this["abandoned"] as? Boolean ?: false
)

internal fun Map<String, Any?>.toGameState(): GameState = GameState(
    code = str("code"),
    hostId = str("hostId"),
    phase = runCatching { GamePhase.valueOf(str("phase")) }.getOrDefault(GamePhase.WAITING),
    players = mapList("players").map { it.toPlayer() },
    board = Board(mapList("board").map { it.toTile() }),
    drawPile = mapList("drawPile").map { it.toTile() },
    currentTurn = int("currentTurn"),
    direction = int("direction", 1),
    round = int("round", 1),
    maxRounds = int("maxRounds", 30),
    activeEvent = (this["activeEvent"] as? Map<*, *>)?.asStringMap()?.let { event ->
        runCatching { ActiveEvent(EventType.valueOf(event.str("type")), event.int("roundsLeft")) }.getOrNull()
    },
    skippedPlayerId = this["skippedPlayerId"] as? String,
    winnerId = this["winnerId"] as? String,
    lastMessage = str("lastMessage")
)

internal fun Map<String, Any?>.toGameSummary(): GameSummary = GameSummary(
    code = str("code"),
    dateMillis = (this["dateMillis"] as? Number)?.toLong() ?: 0L,
    won = this["won"] as? Boolean ?: false,
    score = int("score"),
    position = int("position", 1),
    playerCount = int("playerCount", 2)
)

// endregion

// region Helpers

private fun Map<String, Any?>.int(key: String, default: Int = 0): Int =
    (this[key] as? Number)?.toInt() ?: default

private fun Map<String, Any?>.str(key: String): String = this[key] as? String ?: ""

private fun Map<*, *>.asStringMap(): Map<String, Any?> =
    entries.associate { it.key.toString() to it.value }

private fun Map<String, Any?>.mapList(key: String): List<Map<String, Any?>> =
    (this[key] as? List<*>)?.filterIsInstance<Map<*, *>>()?.map { it.asStringMap() } ?: emptyList()

// endregion