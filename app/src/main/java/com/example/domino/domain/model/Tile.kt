package com.example.domino.domain.model

/**
 * Domino tile. Values range from 0 to 6.
 * It is stored "oriented" as it was placed on the board: [left] faces the
 * left end of the chain and [right] faces the right end.
 *
 * It only has plain properties (no computed getters) so Firestore can
 * serialize it without problems; logic lives in functions.
 */
data class Tile(
    val left: Int = 0,
    val right: Int = 0
) {
    fun isDouble(): Boolean = left == right
    fun points(): Int = left + right
    fun flipped(): Tile = Tile(right, left)
    fun contains(value: Int): Boolean = left == value || right == value
}