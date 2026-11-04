package com.example.domino.domain.model

data class Player(
    /** Firebase Auth uid. */
    val id: String = "",
    val name: String = "",
    val hand: List<Tile> = emptyList(),
    val score: Int = 0,
    /** Remaining uses per power. Key = PowerType.name (Firestore does not allow enum keys). */
    val powerUses: Map<String, Int> = PowerType.entries.associate { it.name to INITIAL_USES_PER_POWER },
    /** Total powers used: tie-breaker (whoever used fewer wins). */
    val powersUsed: Int = 0,
    val abandoned: Boolean = false
) {
    fun usesLeft(power: PowerType): Int = powerUses[power.name] ?: 0
}