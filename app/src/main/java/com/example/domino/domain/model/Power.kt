package com.example.domino.domain.model

/** Special powers. The assignment requires at least 1; all 4 are listed here. */
enum class PowerType {
    /** Allows placing a tile without matching one of the ends. */
    WILDCARD,
    /** The next player loses their turn. */
    SKIP,
    /** Swaps one of your tiles with one from an opponent. */
    SWAP,
    /** Reverses the turn direction. */
    REVERSE
}

/** Uses each power has per player when the game starts. */
const val INITIAL_USES_PER_POWER = 2