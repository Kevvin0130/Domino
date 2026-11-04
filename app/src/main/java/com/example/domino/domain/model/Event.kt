package com.example.domino.domain.model

/**
 * Random events that alter the game state (mockup 8).
 * Person A decides the final catalog; these are starter examples.
 */
enum class EventType {
    /** All players return tiles to the pile and draw again. E.g. "Tile storm". */
    TILE_STORM,
    /** The score of the following moves is doubled. */
    DOUBLE_SCORE,
    /** The player whose turn it is must draw one tile from the pile. */
    FORCED_DRAW,
    /** Reverses the turn direction. */
    TURN_REVERSAL
}

data class ActiveEvent(
    val type: EventType = EventType.DOUBLE_SCORE,
    /** Rounds left for the effect (0 = no longer active). */
    val roundsLeft: Int = 0
)