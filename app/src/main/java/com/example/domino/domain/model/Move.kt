package com.example.domino.domain.model

enum class Side { LEFT, RIGHT }

/**
 * A move a player wants to make on their turn.
 *
 * @param power power chosen BEFORE moving (null = normal move).
 * @param targetId SWAP only: the opponent player.
 * @param targetTile SWAP only: the opponent's tile that you receive.
 */
data class Move(
    val playerId: String = "",
    val tile: Tile = Tile(),
    val side: Side = Side.RIGHT,
    val power: PowerType? = null,
    val targetId: String? = null,
    val targetTile: Tile? = null
)